package org.myrobotlab.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.myrobotlab.codec.CodecUtils;
import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.interfaces.Attachable;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.programab.Response;
import org.myrobotlab.service.config.Gpt3Config;
import org.myrobotlab.service.data.Utterance;
import org.myrobotlab.service.interfaces.ResponsePublisher;
import org.myrobotlab.service.interfaces.TextListener;
import org.myrobotlab.service.interfaces.TextPublisher;
import org.myrobotlab.service.interfaces.UtteranceListener;
import org.myrobotlab.service.interfaces.UtterancePublisher;
import org.slf4j.Logger;

/**
 * https://beta.openai.com/account/api-keys
 * 
 * https://beta.openai.com/playground
 * 
 * https://beta.openai.com/examples
 * 
 * <pre>
     * curl https://api.openai.com/v1/completions \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $OPENAI_API_KEY" \
      -d '{
      "model": "text-ada-001",
      "prompt": "why is the sun so bright ?\n\nThe sun is bright because it is a star. It is born on theentlement, or radiance, which is the product of the surface area of the sun's clouds and the surface area of the sun's atmosphere.",
      "temperature": 0.7,
      "max_tokens": 256,
      "top_p": 1,
      "frequency_penalty": 0,
      "presence_penalty": 0
    }'
 * 
 * </pre>
 * 
 * @author GroG
 *
 */
public class Gpt3 extends Service implements TextListener, TextPublisher, UtterancePublisher, UtteranceListener, ResponsePublisher {

  private static final long serialVersionUID = 1L;

  public final static Logger log = LoggerFactory.getLogger(Gpt3.class);

  protected String currentChannel;

  private String currentBotName;

  private String currentChannelName;

  private String currentChannelType;

  public Gpt3(String n, String id) {
    super(n, id);
  }

  public Response getResponse(String text) {

    try {

      Gpt3Config c = (Gpt3Config) config;
      
      invoke("publishRequest", text);

      if (text == null || text.trim().length() == 0) {
        log.info("emtpy text, not responding");
        return null;
      }

      String responseText = "";

      if (c.sleepWord != null && text.contains(c.sleepWord) && !c.sleeping) {
        sleep();
        responseText = "Ok, I will go to sleep";
      }

      if (!c.sleeping) {

        String json =

            "{\r\n" + "  \"model\": \"" + c.engine + "\",\r\n" + "  \"prompt\": \"" + text + "\",\r\n" + "  \"temperature\": " + c.temperature + ",\r\n" + "  \"max_tokens\": "
                + c.maxTokens + ",\r\n" + "  \"top_p\": 1,\r\n" + "  \"frequency_penalty\": 0,\r\n" + "  \"presence_penalty\": 0\r\n" + "}";

        HttpClient http = (HttpClient) startPeer("http");

        String msg = http.postJson(c.token, c.url, json);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Map<String, Object> payload = (Map) CodecUtils.fromJson(msg, LinkedHashMap.class);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<Object> choices = (List) payload.get("choices");
        if (choices != null && choices.size() > 0) {
          @SuppressWarnings({ "unchecked", "rawtypes" })
          Map<String, Object> textObject = (Map) choices.get(0);
          responseText = (String) textObject.get("text");
          invoke("publishText", responseText);

        } else {
          warn("no response for %s", text);
        }

      } else {
        log.info("sleeping waiting for wake word \"{}\"", c.wakeWord);
      }

      if (c.wakeWord != null && text.contains(c.wakeWord) && c.sleeping) {
        responseText = "Hello, I am awake";
        wake();
      }

      Response response = new Response("friend", getName(), responseText, null);
      Utterance utterance = new Utterance();
      utterance.username = getName();
      utterance.text = responseText;
      utterance.isBot = true;
      utterance.channel = currentChannel;
      utterance.channelType = currentChannelType;
      utterance.channelBotName = currentBotName;
      utterance.channelName = currentChannelName;
      if (responseText != null && responseText.length() > 0) {
        invoke("publishUtterance", utterance);
        invoke("publishResponse", response);
      }

      return response;

    } catch (IOException e) {
      error(e);
    }
    return null;
  }
  
  public String publishRequest(String text) {
    return text;
  }

  public void setToken(String token) {
    Gpt3Config c = (Gpt3Config) config;
    c.token = token;
  }

  public String setEngine(String engine) {
    Gpt3Config c = (Gpt3Config) config;
    c.engine = engine;
    return engine;
  }

  @Override
  public void onUtterance(Utterance utterance) throws Exception {
    currentChannelType = utterance.channelType;
    currentChannel = utterance.channel;
    currentBotName = utterance.channelBotName;
    currentChannelName = utterance.channelName;
    // prevent bots going off the rails
    if (utterance.isBot) {
      log.info("Not responding to bots.");
      return;
    }
    getResponse(utterance.text);
  }

  @Override
  public Utterance publishUtterance(Utterance utterance) {
    return utterance;
  }

  @Override
  public String publishText(String text) {
    return text;
  }

  @Override
  public void onText(String text) throws IOException {
    getResponse(text);
  }

  @Override
  public Response publishResponse(Response response) {
    return response;
  }

  /**
   * wakes the global session up
   */
  public void wake() {
    Gpt3Config c = (Gpt3Config) config;
    log.info("wake now");
    c.sleeping = false;
  }

  /**
   * sleeps the global session
   */
  public void sleep() {
    Gpt3Config c = (Gpt3Config) config;
    log.info("sleeping now");
    c.sleeping = true;
  }

  @Override
  public void attach(Attachable attachable) {

    /*
     * if (attachable instanceof ResponseListener) { // this one is done
     * correctly attachResponseListener(attachable.getName()); } else
     */
    if (attachable instanceof TextPublisher) {
      attachTextPublisher((TextPublisher) attachable);
    } else if (attachable instanceof TextListener) {
      addListener("publishText", attachable.getName(), "onText");
    } else if (attachable instanceof UtteranceListener) {
      attachUtteranceListener(attachable.getName());
    } else {
      log.error("don't know how to attach a {}", attachable.getName());
    }
  }

  public static void main(String[] args) {
    try {

      LoggingFactory.init(Level.INFO);

      // Runtime runtime = Runtime.getInstance();
      // Runtime.startConfig("gpt3-01");

      WebGui webgui = (WebGui) Runtime.create("webgui", "WebGui");
      webgui.autoStartBrowser(false);
      webgui.startService();

      /*
       * Gpt3 i01_chatBot = (Gpt3) Runtime.start("i01.chatBot", "Gpt3");
       * 
       * bot.attach("i01.chatBot"); i01_chatBot.attach("bot");
       * 
       * i01_chatBot.getResponse("hi, how are you?");
       * 
       * Runtime.start("webgui", "WebGui");
       */

    } catch (Exception e) {
      log.error("main threw", e);
    }
  }
}
