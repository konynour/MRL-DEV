package org.myrobotlab.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.myrobotlab.framework.Message;
import org.myrobotlab.framework.Plan;
import org.myrobotlab.framework.Platform;
import org.myrobotlab.framework.Registration;
import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.Status;
import org.myrobotlab.framework.interfaces.ServiceInterface;
import org.myrobotlab.io.FileIO;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.opencv.OpenCVData;
import org.myrobotlab.programab.PredicateEvent;
import org.myrobotlab.programab.Response;
import org.myrobotlab.service.abstracts.AbstractSpeechRecognizer;
import org.myrobotlab.service.abstracts.AbstractSpeechSynthesis;
import org.myrobotlab.service.config.InMoov2Config;
import org.myrobotlab.service.config.ServiceConfig;
import org.myrobotlab.service.data.JoystickData;
import org.myrobotlab.service.data.LedDisplayData;
import org.myrobotlab.service.data.Locale;
import org.myrobotlab.service.interfaces.IKJointAngleListener;
import org.myrobotlab.service.interfaces.JoystickListener;
import org.myrobotlab.service.interfaces.LocaleProvider;
import org.myrobotlab.service.interfaces.ServiceLifeCycleListener;
import org.myrobotlab.service.interfaces.ServoControl;
import org.myrobotlab.service.interfaces.Simulator;
import org.myrobotlab.service.interfaces.SpeechRecognizer;
import org.myrobotlab.service.interfaces.SpeechSynthesis;
import org.myrobotlab.service.interfaces.TextListener;
import org.myrobotlab.service.interfaces.TextPublisher;
import org.slf4j.Logger;

public class InMoov2 extends Service implements ServiceLifeCycleListener, TextListener, TextPublisher, JoystickListener, LocaleProvider, IKJointAngleListener {

  public final static Logger log = LoggerFactory.getLogger(InMoov2.class);

  public static LinkedHashMap<String, String> lpVars = new LinkedHashMap<String, String>();

  private static final long serialVersionUID = 1L;

  static String speechRecognizer = "WebkitSpeechRecognition";

  /**
   * This method will load a python file into the python interpreter.
   * 
   * @param file
   *          file to load
   * @return success/failure
   */
  @Deprecated /* use execScript - this doesn't handle resources correctly */
  public static boolean loadFile(String file) {
    File f = new File(file);
    Python p = (Python) Runtime.getService("python");
    log.info("Loading  Python file {}", f.getAbsolutePath());
    if (p == null) {
      log.error("Python instance not found");
      return false;
    }

    boolean result = false;
    try {
      // This will open a gazillion tabs in InMoov
      // result = p.execFile(f.getAbsolutePath(), true);

      // old way - not using execFile :(
      String script = FileIO.toString(f.getAbsolutePath());
      result = p.exec(script, true);
    } catch (IOException e) {
      log.error("IO Error loading file : ", e);
      return false;
    }

    if (!result) {
      log.error("Error while loading file {}", f.getAbsolutePath());
      return false;
    } else {
      log.debug("Successfully loaded {}", f.getAbsolutePath());
    }
    return true;
  }

  public static void main(String[] args) {
    try {

      LoggingFactory.init(Level.ERROR);
      // Platform.setVirtual(true);
      // Runtime.start("s01", "Servo");
      // Runtime.start("intro", "Intro");

      // Runtime.startConfig("pr-1213-1");
      
      Runtime.main(new String[] {"--log-level", "info", "-s", "webgui", "WebGui", "intro", "Intro", "python", "Python"});
      
      boolean done = true;
      if (done) {
        return;
      }


      WebGui webgui = (WebGui) Runtime.create("webgui", "WebGui");
      // webgui.setSsl(true);
      webgui.autoStartBrowser(false);
      // webgui.setPort(8888);
      webgui.startService();

      Runtime.start("python", "Python");
      // Runtime.start("ros", "Ros");
      Runtime.start("intro", "Intro");
      // InMoov2 i01 = (InMoov2) Runtime.start("i01", "InMoov2");
      // i01.startPeer("simulator");
      // Runtime.startConfig("i01-05");
      // Runtime.startConfig("pir-01");

      // Polly polly = (Polly)Runtime.start("i01.mouth", "Polly");
      // i01 = (InMoov2) Runtime.start("i01", "InMoov2");


      // polly.speakBlocking("Hi, to be or not to be that is the question,
      // wheather to take arms against a see of trouble, and by aposing them end
      // them, to sleep, to die");
      // i01.startPeer("mouth");
      // i01.speakBlocking("Hi, to be or not to be that is the question,
      // wheather to take arms against a see of trouble, and by aposing them end
      // them, to sleep, to die");

      Runtime.start("python", "Python");

      // i01.startSimulator();
      Plan plan = Runtime.load("webgui", "WebGui");
      // WebGuiConfig webgui = (WebGuiConfig) plan.get("webgui");
      // webgui.autoStartBrowser = false;
      Runtime.startConfig("webgui");
      Runtime.start("webgui", "WebGui");

      Random random = (Random) Runtime.start("random", "Random");

      random.addRandom(3000, 8000, "i01", "setLeftArmSpeed", 8.0, 25.0, 8.0, 25.0, 8.0, 25.0, 8.0, 25.0);
      random.addRandom(3000, 8000, "i01", "setRightArmSpeed", 8.0, 25.0, 8.0, 25.0, 8.0, 25.0, 8.0, 25.0);

      random.addRandom(3000, 8000, "i01", "moveLeftArm", 0.0, 5.0, 85.0, 95.0, 25.0, 30.0, 10.0, 15.0);
      random.addRandom(3000, 8000, "i01", "moveRightArm", 0.0, 5.0, 85.0, 95.0, 25.0, 30.0, 10.0, 15.0);

      random.addRandom(3000, 8000, "i01", "setLeftHandSpeed", 8.0, 25.0, 8.0, 25.0, 8.0, 25.0, 8.0, 25.0, 8.0, 25.0, 8.0, 25.0);
      random.addRandom(3000, 8000, "i01", "setRightHandSpeed", 8.0, 25.0, 8.0, 25.0, 8.0, 25.0, 8.0, 25.0, 8.0, 25.0, 8.0, 25.0);

      random.addRandom(3000, 8000, "i01", "moveRightHand", 10.0, 160.0, 10.0, 60.0, 10.0, 60.0, 10.0, 60.0, 10.0, 60.0, 130.0, 175.0);
      random.addRandom(3000, 8000, "i01", "moveLeftHand", 10.0, 160.0, 10.0, 60.0, 10.0, 60.0, 10.0, 60.0, 10.0, 60.0, 5.0, 40.0);

      random.addRandom(200, 1000, "i01", "setHeadSpeed", 8.0, 20.0, 8.0, 20.0, 8.0, 20.0);
      random.addRandom(200, 1000, "i01", "moveHead", 70.0, 110.0, 65.0, 115.0, 70.0, 110.0);

      random.addRandom(200, 1000, "i01", "setTorsoSpeed", 2.0, 5.0, 2.0, 5.0, 2.0, 5.0);
      random.addRandom(200, 1000, "i01", "moveTorso", 85.0, 95.0, 88.0, 93.0, 70.0, 110.0);

      random.save();

//      i01.startChatBot();
//
//      i01.startAll("COM3", "COM4");
      Runtime.start("python", "Python");

    } catch (Exception e) {
      log.error("main threw", e);
    }
  }

  transient ProgramAB chatBot;

  protected List<String> configList;

  String currentConfigurationName = "default";

  transient SpeechRecognizer ear;

  //
  // public void onStartConfig(String configName) {
  // log.info("onStartConfig");
  //
  // }

  transient Tracking eyesTracking;

  // waiting controable threaded gestures we warn user
  boolean gestureAlreadyStarted = false;

  Set<String> gestures = new TreeSet<String>();

  transient Tracking headTracking;

  transient HtmlFilter htmlFilter;

  transient ImageDisplay imageDisplay;

  String lastGestureExecuted;

  Long lastPirActivityTime;

  LedDisplayData led = new LedDisplayData();

  /**
   * supported locales
   */
  Map<String, Locale> locales = null;

  int maxInactivityTimeSeconds = 120;

  transient SpeechSynthesis mouth;

  // FIXME ugh - new MouthControl service that uses AudioFile output
  transient public MouthControl mouthControl;

  boolean mute = false;

  // transient JMonkeyEngine simulator;

  transient OpenCV opencv;

  transient Pir pir;

  transient Python python;

  transient ServoMixer servoMixer;

  transient UltrasonicSensor ultrasonicLeft;

  transient UltrasonicSensor ultrasonicRight;

  protected String voiceSelected;

  transient WebGui webgui;

  public InMoov2(String n, String id) {
    super(n, id);
    Runtime.getInstance().attachServiceLifeCycleListener(getName());    
  }

  public void addTextListener(TextListener service) {
    // CORRECT WAY ! - no direct reference - just use the name in a subscription
    addListener("publishText", service.getName());
  }

  @Override
  public ServiceConfig apply(ServiceConfig c) {
    InMoov2Config config = (InMoov2Config) super.apply(c);
    try {

      locales = Locale.getLocaleMap("en-US", "fr-FR", "es-ES", "de-DE", "nl-NL", "ru-RU", "hi-IN", "it-IT", "fi-FI", "pt-PT", "tr-TR");

      if (config.locale != null) {
        setLocale(config.locale);
      } else {
        setLocale(getSupportedLocale(Runtime.getInstance().getLocale().toString()));
      }

      init();

      if (config.loadGestures) {
        loadGestures();
      }

      if (config.heartbeat) {
        startHeartbeat();
      } else {
        stopHeartbeat();
      }

    } catch (Exception e) {
      error(e);
    }
    return c;
  }

  // FIXME FIXME !!! THIS IS A MESS !!
  public void applyConfig() {
    super.apply();
    log.error("applyConfig()");
    // always getResponse !
    speak("InMoov apply config");
  }

  @Override
  public void attachTextListener(String name) {
    addListener("publishText", name);
  }

  @Override
  public void attachTextListener(TextListener service) {
    attachTextListener(service.getName());
  }

  @Override
  public void attachTextPublisher(String name) {
    subscribe(name, "publishText");
  }

  @Override
  public void attachTextPublisher(TextPublisher service) {
    subscribe(service.getName(), "publishText");
  }

  public void beginCheckingOnInactivity() {
    beginCheckingOnInactivity(maxInactivityTimeSeconds);
  }

  public void beginCheckingOnInactivity(int maxInactivityTimeSeconds) {
    this.maxInactivityTimeSeconds = maxInactivityTimeSeconds;
    // speakBlocking("power down after %s seconds inactivity is on",
    // this.maxInactivityTimeSeconds);
    log.info("power down after %s seconds inactivity is on", this.maxInactivityTimeSeconds);
    addTask("checkInactivity", 5 * 1000, 0, "checkInactivity");
  }

  public void cameraOff() {
    if (opencv != null) {
      opencv.stopCapture();
      opencv.disableAll();
    }
  }

  public void cameraOn() {
    try {
      if (opencv == null) {
        startOpenCV();
      }
      opencv.capture();
    } catch (Exception e) {
      error(e);
    }
  }

  public String captureGesture() {
    return captureGesture(null);
  }

  public String captureGesture(String gesture) {

    if (gesture == null) {
      gesture = "gesture";
    }

    StringBuffer script = new StringBuffer();

    // File file = new File

    // Date date = new Date();
    // script.append("# - " + date + " - Captured gesture :\n");
    script.append(String.format("# %s - captured gesture :\n", getName()));

    script.append(String.format("def %s():\n", gesture));

    String name = getName();

    try {
      // FIXME - types are exposed here - not a good thing
      // real fix should be use the ServoMixer
      InMoov2Head head = (InMoov2Head) getPeer("head");
      if (head != null) {
        script.append("  " + head.getScript(name));
      }

      InMoov2Arm leftArm = (InMoov2Arm) getPeer("leftArm");
      if (leftArm != null) {
        script.append("  " + leftArm.getScript(name));
      }

      InMoov2Arm rightArm = (InMoov2Arm) getPeer("rightArm");
      if (rightArm != null) {
        script.append("  " + rightArm.getScript(name));
      }

      InMoov2Hand leftHand = (InMoov2Hand) getPeer("leftHand");
      if (leftHand != null) {
        script.append("  " + leftHand.getScript(name));
      }

      InMoov2Hand rightHand = (InMoov2Hand) getPeer("rightHand");
      if (rightHand != null) {
        script.append("  " + rightHand.getScript(name));
      }

      InMoov2Torso torso = (InMoov2Torso) getPeer("torso");
      if (torso != null) {
        script.append("  " + torso.getScript(name));
      }
    } catch (Exception e) {
      error(e);
    }

    send("python", "appendScript", script.toString());
    return script.toString();
  }

  public long checkInactivity() {
    // speakBlocking("checking");
    long lastActivityTime = getLastActivityTime();
    long now = System.currentTimeMillis();
    long inactivitySeconds = (now - lastActivityTime) / 1000;
    if (inactivitySeconds > maxInactivityTimeSeconds) {
      // speakBlocking("%d seconds have passed without activity",
      // inactivitySeconds);
      powerDown();
    } else {
      // speakBlocking("%d seconds have passed without activity",
      // inactivitySeconds);
      info("checking checkInactivity - %d seconds have passed without activity", inactivitySeconds);
    }
    return lastActivityTime;
  }

  public void closeAllImages() {
    // FIXME - follow this pattern ?
    // CON npe possible although unlikely
    // PRO start it when its needed
    // PRO small easy to read - no clutter npe
    imageDisplay = (ImageDisplay) startPeer("imageDisplay");
    imageDisplay.closeAll();
  }

  public void cycleGestures() {
    // if not loaded load -
    // FIXME - this needs alot of "help" :P
    // WHY IS THIS DONE ?
    if (gestures.size() == 0) {
      loadGestures();
    }

    for (String gesture : gestures) {
      try {
        String methodName = gesture.substring(0, gesture.length() - 3);
        speakBlocking(methodName);
        log.info("executing gesture {}", methodName);
        python.eval(methodName + "()");

        // wait for finish - or timeout ?

      } catch (Exception e) {
        error(e);
      }
    }
  }

  public void disable() {
    sendToPeer("head", "disable");
    sendToPeer("rightHand", "disable");
    sendToPeer("leftHand", "disable");
    sendToPeer("rightArm", "disable");
    sendToPeer("leftArm", "disable");
    sendToPeer("torso", "disable");
  }

  public void displayFullScreen(String src) {
    try {
      if (imageDisplay == null) {
        imageDisplay = (ImageDisplay) startPeer("imageDisplay");
      }
      imageDisplay.displayFullScreen(src);
      log.error("implement webgui.displayFullScreen");
    } catch (Exception e) {
      error("could not display picture %s", src);
    }
  }

  public void enable() {
    sendToPeer("head", "enable");
    sendToPeer("rightHand", "enable");
    sendToPeer("leftHand", "enable");
    sendToPeer("rightArm", "enable");
    sendToPeer("leftArm", "enable");
    sendToPeer("torso", "enable");
  }

  /**
   * Single place for InMoov2 service to execute arbitrary code - needed
   * initially to set "global" vars in python
   * 
   * @param pythonCode
   * @return
   */
  public boolean exec(String pythonCode) {
    try {
      Python p = (Python) Runtime.start("python", "Python");
      return p.exec(pythonCode, true);
    } catch (Exception e) {
      error("unable to execute script %s", pythonCode);
      return false;
    }
  }

  /**
   * This method will try to launch a python command with error handling
   * 
   * @param gesture
   *          the gesture
   * @return gesture result
   */
  public String execGesture(String gesture) {

    lastGestureExecuted = gesture;
    if (python == null) {
      log.warn("execGesture : No jython engine...");
      return null;
    }
    subscribe(python.getName(), "publishStatus", this.getName(), "onGestureStatus");
    startedGesture(lastGestureExecuted);
    return python.evalAndWait(gesture);
  }

  /**
   * FIXME - I think there was lots of confusion of executing resources or just
   * a file on the file system ... "execScript" I would expect to be just a file
   * on the file system.
   * 
   * If resource semantics are needed there should be a execResourceScript which
   * adds the context and calls the underlying execScript "which only" executes
   * a filesystem file :P
   * 
   * @param someScriptName
   *          execute a resource script
   * @return success or failure
   */
  public boolean execScript(String someScriptName) {
    try {
      Python p = (Python) Runtime.start("python", "Python");
      String script = getResourceAsString(someScriptName);
      return p.exec(script, true);
    } catch (Exception e) {
      error("unable to execute script %s", someScriptName);
      return false;
    }
  }

  public void finishedGesture() {
    finishedGesture("unknown");
  }

  // FIXME - this may drive to locall references for all servos
  public void finishedGesture(String nameOfGesture) {
    if (gestureAlreadyStarted) {
      waitTargetPos();
      // RobotCanMoveRandom = true;
      gestureAlreadyStarted = false;
      log.info("gesture : {} finished...", nameOfGesture);
    }
  }

  // FIXME - this isn't the callback for fsm - why is it needed here ?
  public void fire(String event) {
    invoke("publishEvent", event);
  }

  public void fullSpeed() {
    sendToPeer("head", "fullSpeed");
    sendToPeer("rightHand", "fullSpeed");
    sendToPeer("leftHand", "fullSpeed");
    sendToPeer("rightArm", "fullSpeed");
    sendToPeer("leftArm", "fullSpeed");
    sendToPeer("torso", "fullSpeed");
  }

  // FIXME - remove all of this form of localization
  public String get(String key) {
    String ret = localize(key);
    if (ret != null) {
      return ret;
    }
    return "not yet translated";
  }

  public InMoov2Arm getArm(String side) {
    if (!"left".equals(side) && !"right".equals(side)) {
      error("side must be left or right - instead of %s", side);
      return null;
    }
    return (InMoov2Arm) getPeer(side + "Arm");
  }

  public Tracking getEyesTracking() {
    return eyesTracking;
  }

  public InMoov2Hand getHand(String side) {
    if (!"left".equals(side) && !"right".equals(side)) {
      error("side must be left or right - instead of %s", side);
      return null;
    }
    return (InMoov2Hand) getPeer(side + "Hand");
  }

  public InMoov2Head getHead() {
    return (InMoov2Head) getPeer("head");
  }

  public Tracking getHeadTracking() {
    return headTracking;
  }

  /**
   * finds most recent activity
   * 
   * @return the timestamp of the last activity time.
   */
  public Long getLastActivityTime() {
    try {

      Long lastActivityTime = 0L;

      Long head = (Long) sendToPeerBlocking("head", "getLastActivityTime", getName());
      Long leftArm = (Long) sendToPeerBlocking("leftArm", "getLastActivityTime", getName());
      Long rightArm = (Long) sendToPeerBlocking("rightArm", "getLastActivityTime", getName());
      Long leftHand = (Long) sendToPeerBlocking("leftHand", "getLastActivityTime", getName());
      Long rightHand = (Long) sendToPeerBlocking("rightHand", "getLastActivityTime", getName());
      Long torso = (Long) sendToPeerBlocking("torso", "getLastActivityTime", getName());

      lastActivityTime = Math.max(head, leftArm);
      lastActivityTime = Math.max(lastActivityTime, rightArm);
      lastActivityTime = Math.max(lastActivityTime, leftHand);
      lastActivityTime = Math.max(lastActivityTime, rightHand);
      lastActivityTime = Math.max(lastActivityTime, torso);

      return lastActivityTime;

    } catch (Exception e) {
      error(e);
      return null;
    }

  }

  public InMoov2Arm getLeftArm() {
    return (InMoov2Arm) getPeer("leftArm");
  }

  public InMoov2Hand getLeftHand() {
    return (InMoov2Hand) getPeer("leftHand");
  }

  @Override
  public Map<String, Locale> getLocales() {
    return locales;
  }

  public OpenCV getOpenCV() {
    return opencv;
  }

  public Object getPredicate(String key) {
    ProgramAB chatBot = (ProgramAB) getPeer("chatBot");
    if (chatBot != null) {
      return chatBot.getPredicate(key);
    } else {
      error("no chatBot available");
    }
    return null;
  }

  public Response getResponse(String text) {
    ProgramAB chatBot = (ProgramAB) getPeer("chatBot");
    if (chatBot != null) {
      Response response = chatBot.getResponse(text);
      return response;
    } else {
      log.info("chatbot not ready");
    }
    return null;
  }

  public InMoov2Arm getRightArm() {
    return (InMoov2Arm) getPeer("rightArm");
  }

  public InMoov2Hand getRightHand() {
    return (InMoov2Hand) getPeer("rightHand");
  }

  public Simulator getSimulator() {
    return (Simulator) getPeer("simulator");
  }

  /**
   * matches on language only not variant expands language match to full InMoov2
   * bot locale
   * 
   * @param inLocale
   * @return
   */
  public String getSupportedLocale(String inLocale) {
    String ret = "en-US";
    if (inLocale == null) {
      return ret;
    }

    int pos = inLocale.indexOf("-");
    if (pos > 0) {
      inLocale = inLocale.substring(0, pos);
    }

    for (String fullLocale : locales.keySet()) {
      if (fullLocale.startsWith(inLocale)) {
        return fullLocale;
      }
    }
    return ret;
  }

  public InMoov2Torso getTorso() {
    return (InMoov2Torso) getPeer("torso");
  }

  public InMoov2Config getTypedConfig() {
    return (InMoov2Config) config;
  }

  public void halfSpeed() {
    sendToPeer("head", "setSpeed", 25.0, 25.0, 25.0, 25.0, 100.0, 25.0);
    sendToPeer("rightHand", "setSpeed", 30.0, 30.0, 30.0, 30.0, 30.0, 30.0);
    sendToPeer("leftHand", "setSpeed", 30.0, 30.0, 30.0, 30.0, 30.0, 30.0);
    sendToPeer("rightArm", "setSpeed", 25.0, 25.0, 25.0, 25.0);
    sendToPeer("leftArm", "setSpeed", 25.0, 25.0, 25.0, 25.0);
    sendToPeer("torso", "setSpeed", 20.0, 20.0, 20.0);
  }

  /**
   * execute python scripts in the init directory on startup of the service
   * 
   * @throws IOException
   */
  public void init() throws IOException {
    invoke("publishEvent", "INIT");
    loadScripts(getResourceDir() + fs + "init");
  }

  public boolean isCameraOn() {
    if (opencv != null) {
      if (opencv.isCapturing()) {
        return true;
      }
    }
    return false;
  }

  public boolean isMute() {
    return mute;
  }

  public void loadGestures() {
    loadGestures(getResourceDir() + fs + "gestures");
  }

  /**
   * This blocking method will look at all of the .py files in a directory. One
   * by one it will load the files into the python interpreter. A gesture python
   * file should contain 1 method definition that is the same as the filename.
   * 
   * @param directory
   *          - the directory that contains the gesture python files.
   * @return true/false
   */
  public boolean loadGestures(String directory) {
    invoke("publishEvent", "LOAD GESTURES");

    // iterate over each of the python files in the directory
    // and load them into the python interpreter.
    String extension = "py";
    Integer totalLoaded = 0;
    Integer totalError = 0;

    File dir = new File(directory);
    dir.mkdirs();

    if (dir.exists()) {
      for (File f : dir.listFiles()) {
        if (FilenameUtils.getExtension(f.getAbsolutePath()).equalsIgnoreCase(extension)) {
          if (loadFile(f.getAbsolutePath()) == true) {
            totalLoaded += 1;
            String methodName = f.getName().substring(0, f.getName().length() - 3) + "()";
            gestures.add(methodName);
          } else {
            error("could not load %s", f.getName());
            totalError += 1;
          }
        } else {
          log.info("{} is not a {} file", f.getAbsolutePath(), extension);
        }
      }
    }
    info("%s Gestures loaded, %s Gestures with error", totalLoaded, totalError);
    broadcastState();
    if (totalError > 0) {
      speakAlert(get("GESTURE_ERROR"));
      return false;
    }
    return true;
  }

  /**
   * Generalized directory python script loading method
   * 
   * @param directory
   * @throws IOException
   */
  public void loadScripts(String directory) throws IOException {
    File dir = new File(directory);

    if (!dir.exists() || !dir.isDirectory()) {
      invoke("publishEvent", "LOAD SCRIPTS ERROR");
      return;
    }

    String[] extensions = { "py" };

    for (String extension : extensions) {
      File[] files = dir.listFiles(new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.toLowerCase().endsWith("." + extension);
        }
      });

      if (files != null) {
        for (File file : files) {
          Python p = (Python) Runtime.start("python", "Python");
          if (p != null) {
            p.execFile(file.getAbsolutePath());
          }
        }
      }
    }
  }

  public void moveArm(String which, Double bicep, Double rotate, Double shoulder, Double omoplate) {
    invoke("publishMoveArm", which, bicep, rotate, shoulder, omoplate);
  }

  public void moveEyelids(Double eyelidleftPos, Double eyelidrightPos) {
    sendToPeer("head", "moveEyelids", eyelidleftPos, eyelidrightPos);
  }

  public void moveEyes(Double eyeX, Double eyeY) {
    moveHead(null, null, eyeX, eyeY, null, null);
  }

  public void moveHand(String which, Double thumb, Double index, Double majeure, Double ringFinger, Double pinky) {
    moveHand(which, thumb, index, majeure, ringFinger, pinky, null);
  }

  public void moveHand(String which, Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    invoke("publishMoveHand", which, thumb, index, majeure, ringFinger, pinky, wrist);
  }

  public void moveHead(Double neck, Double rothead) {
    moveHead(neck, rothead, null, null, null, null);
  }

  public void moveHead(Double neck, Double rothead, Double rollNeck) {
    moveHead(neck, rothead, null, null, null, rollNeck);
  }

  public void moveHead(Double neck, Double rothead, Double eyeX, Double eyeY, Double jaw) {
    moveHead(neck, rothead, eyeX, eyeY, jaw, null);
  }

  public void moveHead(Double neck, Double rothead, Double eyeX, Double eyeY, Double jaw, Double rollNeck) {
    invoke("publishMoveHead", neck, rothead, eyeX, eyeY, jaw, rollNeck);
  }

  public void moveHead(Integer neck, Integer rothead, Integer rollNeck) {
    moveHead((double) neck, (double) rothead, null, null, null, (double) rollNeck);
  }

  public void moveHeadBlocking(Double neck, Double rothead) {
    moveHeadBlocking(neck, rothead, null);
  }

  public void moveHeadBlocking(Double neck, Double rothead, Double rollNeck) {
    moveHeadBlocking(neck, rothead, null, null, null, rollNeck);
  }

  public void moveHeadBlocking(Double neck, Double rothead, Double eyeX, Double eyeY, Double jaw) {
    moveHeadBlocking(neck, rothead, eyeX, eyeY, jaw, null);
  }

  public void moveHeadBlocking(Double neck, Double rothead, Double eyeX, Double eyeY, Double jaw, Double rollNeck) {
    // the "right" way
    sendToPeer("head", "moveToBlocking", neck, rothead, eyeX, eyeY, jaw, rollNeck);
  }

  public void moveLeftArm(Double bicep, Double rotate, Double shoulder, Double omoplate) {
    moveArm("left", bicep, rotate, shoulder, omoplate);
  }

  public void moveLeftHand(Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    moveHand("left", thumb, index, majeure, ringFinger, pinky, wrist);
  }

  public void moveLeftHand(Integer thumb, Integer index, Integer majeure, Integer ringFinger, Integer pinky, Integer wrist) {
    moveHand("left", (double) thumb, (double) index, (double) majeure, (double) ringFinger, (double) pinky, (double) wrist);
  }

  public void moveRightArm(Double bicep, Double rotate, Double shoulder, Double omoplate) {
    moveArm("right", bicep, rotate, shoulder, omoplate);
  }

  public void moveRightHand(Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    moveHand("right", thumb, index, majeure, ringFinger, pinky, wrist);
  }

  public void moveRightHand(Integer thumb, Integer index, Integer majeure, Integer ringFinger, Integer pinky, Integer wrist) {
    moveHand("right", (double) thumb, (double) index, (double) majeure, (double) ringFinger, (double) pinky, (double) wrist);
  }

  public void moveTorso(Double topStom, Double midStom, Double lowStom) {
    // the "right" way
    invoke("publishMoveTorso", topStom, midStom, lowStom);
  }

  public void moveTorsoBlocking(Double topStom, Double midStom, Double lowStom) {
    // the "right" way
    sendToPeer("torso", "moveToBlocking", topStom, midStom, lowStom);
  }

  public PredicateEvent onChangePredicate(PredicateEvent event) {
    log.error("onChangePredicate {}", event);
    if (event.name.equals("topic")) {
      invoke("publishEvent", String.format("TOPIC CHANGED TO %s", event.value));
    }
    // depending on configuration ....
    // call python ?
    // fire fsm events ?
    // do defaults ?
    return event;
  }

  /**
   * comes in from runtime which owns the config list
   * 
   * @param configList
   *          list of configs
   */
  public void onConfigList(List<String> configList) {
    this.configList = configList;
    invoke("publishConfigList");
  }

  @Override
  public void onCreated(String fullname) {
    log.info("{} created", fullname);
  }

  public void onFinishedConfig(String configName) {
    log.info("onFinishedConfig");
    // invoke("publishEvent", "configFinished");
    invoke("publishEvent", "CONFIG LOADED");
  }

  public void onGestureStatus(Status status) {
    if (!status.equals(Status.success()) && !status.equals(Status.warn("Python process killed !"))) {
      error("I cannot execute %s, please check logs", lastGestureExecuted);
    }
    finishedGesture(lastGestureExecuted);
    unsubscribe(python.getName(), "publishStatus", this.getName(), "onGestureStatus");
  }

  @Override
  public void onJointAngles(Map<String, Double> angleMap) {
    log.debug("onJointAngles {}", angleMap);
    // here we can make decisions on what ik sets we want to use and
    // what body parts are to move
    for (String name : angleMap.keySet()) {
      ServiceInterface si = Runtime.getService(name);
      if (si != null && si instanceof ServoControl) {
        ((Servo) si).moveTo(angleMap.get(name));
      }
    }
  }

  @Override
  public void onJoystickInput(JoystickData input) throws Exception {
    // TODO Auto-generated method stub

  }

  public String onNewState(String state) {
    log.error("onNewState {}", state);

    // put configurable filter here !

    // state substitutions ?
    // let python subscribe directly to fsm.publishNewState

    // if
    invoke(state);
    // depending on configuration ....
    // call python ?
    // fire fsm events ?
    // do defaults ?
    return state;
  }

  public OpenCVData onOpenCVData(OpenCVData data) {
    return data;
  }

  /**
   * initial callback for Pir sensor Default behavior will be: send fsm event
   * onPirOn flash neopixel
   */
  public void onPirOn() {
    led.action = "flash";
    led.red = 50;
    led.green = 100;
    led.blue = 150;
    led.count = 5;
    led.interval = 500;

    invoke("publishFlash");
    // pirOn event vs wake event
    invoke("publishEvent", "WAKE");
  }

  // GOOD GOOD GOOD - LOOPBACK - flexible and replacable by python
  // yet provides a stable default, which can be fully replaced
  // Works using common pub/sub rules
  // TODO - this is a loopback power up
  // its replaceable by typical subscription rules
  public void onPowerUp() {
    // CON - type aware
    NeoPixel neoPixel = (NeoPixel) getPeer("neoPixel");
    // CON - necessary NPE checking
    if (neoPixel != null) {
      neoPixel.setColor(0, 130, 0);
      neoPixel.playAnimation("Larson Scanner");
    }
  }

  @Override
  public void onRegistered(Registration registration) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onReleased(String name) {
    // TODO Auto-generated method stub

  }

  // I THINK THIS IS GOOD (good simple one)- need more info though
  public boolean onSense(boolean b) {
    // if wake on Pir config &&
    // setEvent("pir-sense-on" .. also sets it in config ?
    // config.handledEvents["pir-sense-on"]
    if (b) {
      invoke("publishEvent", "PIR ON");
    } else {
      invoke("publishEvent", "PIR OFF");
    }
    return b;
  }

  /**
   * Part of service life cycle - a new servo has been started
   * 
   * need a directed message sent to a callback simlar to this except it should
   * be a "key" not a "fullname" ! .. this could be created with minimal
   * structure .. i think
   */
  @Override
  public void onStarted(String name) {
    log.info("{} started", name);
    try {

      InMoov2Config c = (InMoov2Config) config;
      Runtime runtime = Runtime.getInstance();
      log.info("onStarted {}", name);

//    BAD IDEA - better to ask for a system report or an error report
//      if (runtime.isProcessingConfig()) {
//        invoke("publishEvent", "CONFIG STARTED");
//      }

      String peerKey = getPeerKey(name);
      if (peerKey != null) {
        invoke("publishEvent", "STARTED " + peerKey);
      }

      String actualName = getPeerName("ear");
      if (actualName.equals(name)) {
        AbstractSpeechRecognizer ear = (AbstractSpeechRecognizer) Runtime.getService(actualName);
        ear.attachTextListener(getPeerName("chatBot"));
      }

      actualName = getPeerName("mouth");
      if (actualName.equals(name)) {
        AbstractSpeechSynthesis mouth = (AbstractSpeechSynthesis) Runtime.getService(actualName);
        mouth.attachSpeechListener(getPeerName("ear"));
      }

      actualName = getPeerName("chatBot");
      if (actualName.equals(name)) {
        ProgramAB chatBot = (ProgramAB) Runtime.getService(actualName);
        chatBot.attachTextListener(getPeerName("htmlFilter"));
        startPeer("htmlFilter");
      }

      actualName = getPeerName("htmlFilter");
      if (actualName.equals(name)) {
        TextPublisher htmlFilter = (TextPublisher) Runtime.getService(actualName);
        htmlFilter.attachTextListener(getPeerName("mouth"));
      }

      // FIXME - this is a much better way to do it than the code above
      // the types are not exposed, its not using a local reference of the
      // service
      // its remote compatible, and it uses the peers actual name - it does not
      // break easily,
      // allows user modification and subscribing to topics for extensibility,
      // no NPEs uses messaging, and follows mrl pub/sub conventions - above
      // code should be
      // refactored accordingly.
      actualName = getPeerName("head");
      if (actualName.equals(name)) {
        addListener("publishMoveHead", actualName);
      }

      actualName = getPeerName("torso");
      if (actualName.equals(name)) {
        addListener("publishMoveTorso", actualName);
      }

      // mapping a channel to a single end point depending on peer
      actualName = getPeerName("leftHand");
      if (actualName.equals(name)) {
        addListener("publishMoveLeftHand", actualName, "onMoveHand");
      }

      // mapping a channel to a single end point depending on peer
      actualName = getPeerName("rightHand");
      if (actualName.equals(name)) {
        addListener("publishMoveRightHand", actualName, "onMoveHand");
      }

      // mapping a channel to a single end point depending on peer
      actualName = getPeerName("leftArm");
      if (actualName.equals(name)) {
        addListener("publishMoveLeftArm", actualName, "onMoveArm");
      }

      // mapping a channel to a single end point depending on peer
      actualName = getPeerName("rightArm");
      if (actualName.equals(name)) {
        addListener("publishMoveRightArm", actualName, "onMoveArm");
      }

      ServiceInterface si = Runtime.getService(name);
      if ("Servo".equals(si.getSimpleName())) {
        log.info("sending setAutoDisable true to {}", name);
        send(name, "setAutoDisable", true);
        // ServoControl sc = (ServoControl)Runtime.getService(name);
      }
    } catch (Exception e) {
      log.error("onStarted threw", e);
    }
  }

  @Override
  public void onStopped(String name) {
    // using release peer for peer releasing
  }

  @Override
  public void onText(String text) {
    // FIXME - we should be able to "re"-publish text but text is coming from
    // different sources
    // some might be coming from the ear - some from the mouth ... - there has
    // to be a distinction
    log.info("onText - {}", text);
    invoke("publishText", text);
  }

  // TODO FIX/CHECK this, migrate from python land
  public void powerDown() {

    rest();
    purgeTasks();
    disable();

    if (ear != null) {
      ear.lockOutAllGrammarExcept("power up");
    }

    // FIXME - DO NOT DO THIS !!!! SIMPLY PUBLISH A POWER DOWN EVENT AND PYTHON
    // CAN SUBSCRIBE
    // AND MAINTAIN A SET OF onPowerDown: callback methods
    python.execMethod("power_down");
  }

  // TODO FIX/CHECK this, migrate from python land
  // FIXME - defaultPowerUp switchable + override
  public void powerUp() {
    enable();
    rest();

    if (ear != null) {
      ear.clearLock();
    }

    beginCheckingOnInactivity();

    python.execMethod("power_up");
  }

  /**
   * easy utility to publishMessage
   * 
   * @param name
   * @param method
   * @param data
   */
  public void publish(String name, String method, Object... data) {
    Message msg = Message.createMessage(getName(), name, method, data);
    invoke("publishMessage", msg);
  }

  /**
   * "re"-publishing runtime config list, because I don't want to fix the js
   * subscribeTo :P
   * 
   * @return list of config names
   */
  public List<String> publishConfigList() {
    return configList;
  }

  /**
   * event publisher for the fsm - although other services potentially can
   * consume and filter this event channel
   * 
   * @param event
   * @return
   */
  public String publishEvent(String event) {
    return String.format("SYSTEM_EVENT %s", event);
  }

  /**
   * used to configure a flashing event - could use configuration to signal
   * different colors and states
   * 
   * @return
   */
  public LedDisplayData publishFlash() {
    return led;
  }

  public String publishHeartbeat() {
    led.action = "flash";
    led.red = 180;
    led.green = 10;
    led.blue = 30;
    led.count = 1;
    led.interval = 50;
    invoke("publishFlash");
    return getName();
  }

  /**
   * A more extensible interface point than publishEvent
   * 
   * @param msg
   * @return
   */
  public Message publishMessage(Message msg) {
    return msg;
  }

  public HashMap<String, Double> publishMoveArm(String which, Double bicep, Double rotate, Double shoulder, Double omoplate) {
    HashMap<String, Double> map = new HashMap<>();
    map.put("bicep", bicep);
    map.put("rotate", rotate);
    map.put("shoulder", shoulder);
    map.put("omoplate", omoplate);
    if ("left".equals(which)) {
      invoke("publishMoveLeftArm", bicep, rotate, shoulder, omoplate);
    } else {
      invoke("publishMoveRightArm", bicep, rotate, shoulder, omoplate);
    }
    return map;
  }

  public HashMap<String, Object> publishMoveHand(String which, Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("which", which);
    map.put("thumb", thumb);
    map.put("index", index);
    map.put("majeure", majeure);
    map.put("ringFinger", ringFinger);
    map.put("pinky", pinky);
    map.put("wrist", wrist);
    if ("left".equals(which)) {
      invoke("publishMoveLeftHand", thumb, index, majeure, ringFinger, pinky, wrist);
    } else {
      invoke("publishMoveRightHand", thumb, index, majeure, ringFinger, pinky, wrist);
    }
    return map;
  }

  public HashMap<String, Double> publishMoveHead(Double neck, Double rothead, Double eyeX, Double eyeY, Double jaw, Double rollNeck) {
    HashMap<String, Double> map = new HashMap<>();
    map.put("neck", neck);
    map.put("rothead", rothead);
    map.put("eyeX", eyeX);
    map.put("eyeY", eyeY);
    map.put("jaw", jaw);
    map.put("rollNeck", rollNeck);
    return map;
  }

  public HashMap<String, Double> publishMoveLeftArm(Double bicep, Double rotate, Double shoulder, Double omoplate) {
    HashMap<String, Double> map = new HashMap<>();
    map.put("bicep", bicep);
    map.put("rotate", rotate);
    map.put("shoulder", shoulder);
    map.put("omoplate", omoplate);
    return map;
  }

  public HashMap<String, Double> publishMoveLeftHand(Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    HashMap<String, Double> map = new HashMap<>();
    map.put("thumb", thumb);
    map.put("index", index);
    map.put("majeure", majeure);
    map.put("ringFinger", ringFinger);
    map.put("pinky", pinky);
    map.put("wrist", wrist);
    return map;
  }

  public HashMap<String, Double> publishMoveRightArm(Double bicep, Double rotate, Double shoulder, Double omoplate) {
    HashMap<String, Double> map = new HashMap<>();
    map.put("bicep", bicep);
    map.put("rotate", rotate);
    map.put("shoulder", shoulder);
    map.put("omoplate", omoplate);
    return map;
  }

  public HashMap<String, Double> publishMoveRightHand(Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    HashMap<String, Double> map = new HashMap<>();
    map.put("thumb", thumb);
    map.put("index", index);
    map.put("majeure", majeure);
    map.put("ringFinger", ringFinger);
    map.put("pinky", pinky);
    map.put("wrist", wrist);
    return map;
  }

  public HashMap<String, Double> publishMoveTorso(Double topStom, Double midStom, Double lowStom) {
    HashMap<String, Double> map = new HashMap<>();
    map.put("topStom", topStom);
    map.put("midStom", midStom);
    map.put("lowStom", lowStom);
    return map;
  }

  /**
   * all published text from InMoov2 - including ProgramAB
   */
  @Override
  public String publishText(String text) {
    return text;
  }

  @Override
  public void releasePeer(String peerKey) {
    super.releasePeer(peerKey);
    if (peerKey != null) {
      invoke("publishEvent","STOPPED " + peerKey);
    }
  }

  @Override
  public void releaseService() {
    try {
      disable();
      super.releaseService();
    } catch (Exception e) {
      error(e);
    }
  }

  public void rest() {
    // FIXME publishRest / pub/sub way
    sendToPeer("head", "rest");
    sendToPeer("rightHand", "rest");
    sendToPeer("leftHand", "rest");
    sendToPeer("rightArm", "rest");
    sendToPeer("leftArm", "rest");
    sendToPeer("torso", "rest");
  }

  public boolean setAllVirtual(boolean virtual) {
    Runtime.setAllVirtual(virtual);
    speakBlocking(get("STARTINGVIRTUALHARD"));
    return virtual;
  }

  public void setArmSpeed(String which, Double bicep, Double rotate, Double shoulder, Double omoplate) {
    InMoov2Arm arm = getArm(which);
    if (arm == null) {
      warn("%s arm not started", which);
      return;
    }
    arm.setSpeed(bicep, rotate, shoulder, omoplate);
  }

  @Deprecated
  public void setArmVelocity(String which, Double bicep, Double rotate, Double shoulder, Double omoplate) {
    setArmSpeed(which, bicep, rotate, shoulder, omoplate);
  }

  public void setAutoDisable(Boolean param) {

    sendToPeer("head", "setAutoDisable", param);
    sendToPeer("rightHand", "setAutoDisable", param);
    sendToPeer("leftHand", "setAutoDisable", param);
    sendToPeer("rightArm", "setAutoDisable", param);
    sendToPeer("leftArm", "setAutoDisable", param);
    sendToPeer("torso", "setAutoDisable", param);
  }

  public void setHandSpeed(String which, Double thumb, Double index, Double majeure, Double ringFinger, Double pinky) {
    setHandSpeed(which, thumb, index, majeure, ringFinger, pinky, null);
  }

  public void setHandSpeed(String which, Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    InMoov2Hand hand = getHand(which);
    if (hand == null) {
      warn("%s hand not started", which);
      return;
    }
    hand.setSpeed(thumb, index, majeure, ringFinger, pinky, wrist);
  }

  @Deprecated
  public void setHandVelocity(String which, Double thumb, Double index, Double majeure, Double ringFinger, Double pinky) {
    setHandSpeed(which, thumb, index, majeure, ringFinger, pinky, null);
  }

  @Deprecated
  public void setHandVelocity(String which, Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    setHandSpeed(which, thumb, index, majeure, ringFinger, pinky, wrist);
  }

  public void setHeadSpeed(Double rothead, Double neck) {
    setHeadSpeed(rothead, neck, null, null, null);
  }

  public void setHeadSpeed(Double rothead, Double neck, Double rollNeck) {
    setHeadSpeed(rothead, neck, null, null, null, rollNeck);
  }

  public void setHeadSpeed(Double rothead, Double neck, Double eyeXSpeed, Double eyeYSpeed, Double jawSpeed) {
    setHeadSpeed(rothead, neck, eyeXSpeed, eyeYSpeed, jawSpeed, null);
  }

  public void setHeadSpeed(Double rothead, Double neck, Double eyeXSpeed, Double eyeYSpeed, Double jawSpeed, Double rollNeckSpeed) {
    sendToPeer("head", "setSpeed", neck, eyeXSpeed, eyeYSpeed, jawSpeed, rollNeckSpeed);
  }

  public void setHeadSpeed(Integer rothead, Integer neck, Integer rollNeck) {
    setHeadSpeed((double) rothead, (double) neck, null, null, null, (double) rollNeck);
  }

  @Deprecated
  public void setHeadVelocity(Double rothead, Double neck) {
    setHeadSpeed(rothead, neck, null, null, null, null);
  }

  @Deprecated
  public void setHeadVelocity(Double rothead, Double neck, Double rollNeck) {
    setHeadSpeed(rothead, neck, null, null, null, rollNeck);
  }

  @Deprecated
  public void setHeadVelocity(Double rothead, Double neck, Double eyeXSpeed, Double eyeYSpeed, Double jawSpeed) {
    setHeadSpeed(rothead, neck, eyeXSpeed, eyeYSpeed, jawSpeed, null);
  }

  @Deprecated
  public void setHeadVelocity(Double rothead, Double neck, Double eyeXSpeed, Double eyeYSpeed, Double jawSpeed, Double rollNeckSpeed) {
    setHeadSpeed(rothead, neck, eyeXSpeed, eyeYSpeed, jawSpeed, rollNeckSpeed);
  }

  public void setLeftArmSpeed(Double bicep, Double rotate, Double shoulder, Double omoplate) {
    setArmSpeed("left", bicep, rotate, shoulder, omoplate);
  }

  public void setLeftArmSpeed(Integer bicep, Integer rotate, Integer shoulder, Integer omoplate) {
    setArmSpeed("left", (double) bicep, (double) rotate, (double) shoulder, (double) omoplate);
  }

  public void setLeftHandSpeed(Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    setHandSpeed("left", thumb, index, majeure, ringFinger, pinky, wrist);
  }

  public void setLeftHandSpeed(Integer thumb, Integer index, Integer majeure, Integer ringFinger, Integer pinky, Integer wrist) {
    setHandSpeed("left", (double) thumb, (double) index, (double) majeure, (double) ringFinger, (double) pinky, (double) wrist);
  }

  @Override
  public void setLocale(String code) {

    if (code == null) {
      log.warn("setLocale null");
      return;
    }

    // filter of the set of supported locales
    if (!Locale.hasLanguage(locales, code)) {
      error("InMoov does not support %s only %s", code, locales.keySet());
      return;
    }

    super.setLocale(code);

    // super.setLocale(code);
    for (ServiceInterface si : Runtime.getLocalServices().values()) {
      if (!si.equals(this)) {
        si.setLocale(code);
      }
    }
  }

  public void setMute(boolean mute) {
    info("Set mute to %s", mute);
    this.mute = mute;
    sendToPeer("mouth", "setMute", mute);
    broadcastState();
  }

  public void setNeopixelAnimation(String animation, Integer red, Integer green, Integer blue, Integer speed) {
    sendToPeer("neopixel", "animation", red, green, blue, speed);
  }

  public void setOpenCV(OpenCV opencv) {
    this.opencv = opencv;
  }

  public boolean setPirPlaySounds(boolean b) {
    getTypedConfig().pirPlaySounds = b;
    return b;
  }

  public Object setPredicate(String key, Object data) {
    ProgramAB chatBot = (ProgramAB) getPeer("chatBot");
    if (chatBot != null) {
      if (data == null) {
        chatBot.setPredicate(key, null); // "unknown" "null" other sillyness ?
      } else {
        chatBot.setPredicate(key, data.toString());
      }
    } else {
      error("no chatBot available");
    }
    return data;
  }

  public void setRightArmSpeed(Double bicep, Double rotate, Double shoulder, Double omoplate) {
    setArmSpeed("right", bicep, rotate, shoulder, omoplate);
  }

  public void setRightArmSpeed(Integer bicep, Integer rotate, Integer shoulder, Integer omoplate) {
    setArmSpeed("right", (double) bicep, (double) rotate, (double) shoulder, (double) omoplate);
  }

  public void setRightHandSpeed(Double thumb, Double index, Double majeure, Double ringFinger, Double pinky, Double wrist) {
    setHandSpeed("right", thumb, index, majeure, ringFinger, pinky, wrist);
  }

  public void setRightHandSpeed(Integer thumb, Integer index, Integer majeure, Integer ringFinger, Integer pinky, Integer wrist) {
    setHandSpeed("right", (double) thumb, (double) index, (double) majeure, (double) ringFinger, (double) pinky, (double) wrist);
  }

  public String setSpeechType(String speechType) {
    updatePeerType("mouth" /* getPeerName("mouth") */, speechType);
    return speechType;
  }

  public void setTorsoSpeed(Double topStom, Double midStom, Double lowStom) {
    sendToPeer("torso", "setSpeed", topStom, midStom, lowStom);
  }

  public void setTorsoSpeed(Integer topStom, Integer midStom, Integer lowStom) {
    setTorsoSpeed((double) topStom, (double) midStom, (double) lowStom);
  }

  @Deprecated
  public void setTorsoVelocity(Double topStom, Double midStom, Double lowStom) {
    setTorsoSpeed(topStom, midStom, lowStom);
  }

  public void setVoice(String name) {
    if (mouth != null) {
      mouth.setVoice(name);
      voiceSelected = name;
      speakBlocking(String.format("%s %s", get("SETLANG"), name));
    }
  }

  // -----------------------------------------------------------------------------
  // These are methods added that were in InMoov1 that we no longer had in
  // InMoov2.
  // From original InMoov1 so we don't loose the

  public void sleeping() {
    log.error("sleeping");
  }

  public void speak(String toSpeak) {
    sendToPeer("mouth", "speak", toSpeak);
  }

  public void speakAlert(String toSpeak) {
    speakBlocking(get("ALERT"));
    speakBlocking(toSpeak);
  }

  public void speakBlocking(String speak) {
    speakBlocking(speak, (Object[]) null);
  }

  // FIXME - publish text regardless if mouth exists ...
  public void speakBlocking(String format, Object... args) {

    if (format == null) {
      return;
    }

    String toSpeak = format;
    if (args != null) {
      toSpeak = String.format(format, args);
    }

    // FIXME - publish onText when listening
    invoke("publishText", toSpeak);

    // FIXME - mute is not normalized
    if (!mute && isPeerStarted("mouth")) {
      // sendToPeer("mouth", "speakBlocking", toSpeak);
      // invokePeer("mouth", "speakBlocking", toSpeak);
      // HEH, CANNOT DO THIS !! ITS NOT BLOCKING - NEED BLOCKING
      // BECAUSE A GAZILLION GESTURES DEPEND ON BLOCKING SPEECH !!!
      // sendToPeer("mouth", "speakBlocking", toSpeak);
      AbstractSpeechSynthesis mouth = (AbstractSpeechSynthesis) getPeer("mouth");
      if (mouth != null) {
        mouth.speak(toSpeak);
      }
    }
  }

  public void startAll() throws Exception {
    startAll(null, null);
  }

  public void startAll(String leftPort, String rightPort) throws Exception {
    startMouth();
    startChatBot();

    // startHeadTracking();
    // startEyesTracking();
    // startOpenCV();
    startEar();

    startServos();
    // startMouthControl(head.jaw, mouth);

    speakBlocking(get("STARTINGSEQUENCE"));
  }

  public void startBrain() {
    startChatBot();
  }

  public ProgramAB startChatBot() {

    try {
      chatBot = (ProgramAB) startPeer("chatBot");

      if (locale != null) {
        chatBot.setCurrentBotName(locale.getTag());
      }

      // FIXME remove get en.properties stuff
      speakBlocking(get("CHATBOTACTIVATED"));

      chatBot.attachTextPublisher(ear);

      // this.attach(chatBot); FIXME - attach as a TextPublisher - then
      // re-publish
      // FIXME - deal with language
      // speakBlocking(get("CHATBOTACTIVATED"));
      chatBot.repetitionCount(10);
      // chatBot.setPath(getResourceDir() + fs + "chatbot");
      // chatBot.setPath(getDataDir() + "ProgramAB");
      chatBot.startSession("default", locale.getTag());
      // reset some parameters to default...
      chatBot.setPredicate("topic", "default");
      chatBot.setPredicate("questionfirstinit", "");
      chatBot.setPredicate("tmpname", "");
      chatBot.setPredicate("null", "");
      // load last user session
      if (!chatBot.getPredicate("name").isEmpty()) {
        if (chatBot.getPredicate("lastUsername").isEmpty() || chatBot.getPredicate("lastUsername").equals("unknown") || chatBot.getPredicate("lastUsername").equals("default")) {
          chatBot.setPredicate("lastUsername", chatBot.getPredicate("name"));
        }
      }
      chatBot.setPredicate("parameterHowDoYouDo", "");
      try {
        chatBot.savePredicates();
      } catch (IOException e) {
        log.error("saving predicates threw", e);
      }
      htmlFilter = (HtmlFilter) startPeer("htmlFilter");// Runtime.start("htmlFilter",
      // "HtmlFilter");
      chatBot.attachTextListener(htmlFilter);
      htmlFilter.attachTextListener((TextListener) getPeer("mouth"));
      chatBot.attachTextListener(this);
      // start session based on last recognized person
      // if (!chatBot.getPredicate("default", "lastUsername").isEmpty() &&
      // !chatBot.getPredicate("default", "lastUsername").equals("unknown")) {
      // chatBot.startSession(chatBot.getPredicate("lastUsername"));
      // }
      if (chatBot.getPredicate("default", "firstinit").isEmpty() || chatBot.getPredicate("default", "firstinit").equals("unknown")
          || chatBot.getPredicate("default", "firstinit").equals("started")) {
        chatBot.startSession(chatBot.getPredicate("default", "lastUsername"));
        invoke("publishEvent", "FIRST INIT");
      } else {
        chatBot.startSession(chatBot.getPredicate("default", "lastUsername"));
        invoke("publishEvent", "WAKE UP");
      }
    } catch (Exception e) {
      speak("could not load chatBot");
      error(e.getMessage());
      speak(e.getMessage());
    }
    broadcastState();
    return chatBot;
  }

  public SpeechRecognizer startEar() {

    ear = (SpeechRecognizer) startPeer("ear");
    ear.attachSpeechSynthesis((SpeechSynthesis) getPeer("mouth"));
    ear.attachTextListener(chatBot);
    broadcastState();
    return ear;
  }

  public void startedGesture() {
    startedGesture("unknown");
  }

  public void startedGesture(String nameOfGesture) {
    if (gestureAlreadyStarted) {
      warn("Warning 1 gesture already running, this can break spacetime and lot of things");
    } else {
      log.info("Starting gesture : {}", nameOfGesture);
      gestureAlreadyStarted = true;
      // RobotCanMoveRandom = false;
    }
  }

  public void startHeartbeat() {
    addTask(1000, "publishHeartbeat");
  }

  // TODO - general objective "might" be to reduce peers down to something
  // that does not need a reference - where type can be switched before creation
  // and the only thing needed is pubs/subs that are not handled in abstracts
  public SpeechSynthesis startMouth() {

    // FIXME - set type ??? - maybe a good product of InMoov
    // if "new" type cannot necessarily grab yml file
    // setMouthType

    // FIXME - bad to have a reference, should only need the "name" of the
    // service !!!
    mouth = (SpeechSynthesis) startPeer("mouth");

    // voices = mouth.getVoices();
    // Voice voice = mouth.getVoice();
    // if (voice != null) {
    // voiceSelected = voice.getName();
    // }

    if (mute) {
      mouth.setMute(true);
    }

    mouth.attachSpeechRecognizer(ear);
    // mouth.attach(htmlFilter); // same as chatBot not needed

    // this.attach((Attachable) mouth);
    // if (ear != null) ....

    broadcastState();

    speakBlocking(get("STARTINGMOUTH"));
    if (Platform.isVirtual()) {
      speakBlocking(get("STARTINGVIRTUALHARD"));
    }
    speakBlocking(get("WHATISTHISLANGUAGE"));

    return mouth;
  }

  public void startMouthControl() {
    speakBlocking(get("STARTINGMOUTHCONTROL"));
    mouthControl = (MouthControl) startPeer("mouthControl");
    InMoov2Head head = getHead();
    if (head != null) {
      mouthControl.attach(head.getPeer("jaw"));
    }
    mouthControl.attach(getPeer("mouth"));
  }

  // FIXME - universal (good) way of handling all exceptions - ie - reporting
  // back to the user the problem in a short concise way but have
  // expandable detail in appropriate places
  public OpenCV startOpenCV() {
    speakBlocking(get("STARTINGOPENCV"));
    opencv = (OpenCV) startPeer("opencv");
    subscribeTo(opencv.getName(), "publishOpenCVData");
    return opencv;
  }

  @Override
  public ServiceInterface startPeer(String peer) {
    ServiceInterface si = super.startPeer(peer);
    return si;
  }

  @Override
  public void startService() {
    super.startService();
    InMoov2Config c = (InMoov2Config) config;
    Runtime runtime = Runtime.getInstance();

    // InMoov2 has a huge amount of peers

    // by default all servos will auto-disable
    // Servo.setAutoDisableDefault(true); //until peer servo services for
    // InMoov2 have the auto disable behavior, we should keep this

    // same as created in runtime - send asyc message to all
    // registered services, this service has started
    // find all servos - set them all to autoDisable(true)
    // onStarted(name) will handle all future created servos
    List<ServiceInterface> services = Runtime.getServices();
    for (ServiceInterface si : services) {
      if ("Servo".equals(si.getSimpleName())) {
        send(si.getFullName(), "setAutoDisable", true);
      }
    }

    // REALLY NEEDS TO BE CLEANED UP - no direct references
    // "publish" scripts which should be executed :(
    // python = (Python) startPeer("python");
    // python = (Python) Runtime.start("python", "Python"); <- BAD !!!!
    // load(locale.getTag()); WTH ?

    // get events of new services and shutdown
    Runtime r = Runtime.getInstance();
    subscribe(r.getName(), "shutdown");
    subscribe(r.getName(), "publishConfigList");

    // FIXME - Framework should auto-magically auto-start peers AFTER
    // construction - unless explicitly told not to
    // peers to start on construction
    // imageDisplay = (ImageDisplay) startPeer("imageDisplay");

    if (runtime.isProcessingConfig()) {
      invoke("publishEvent", "configStarted");
    }

    // power up loopback subscription
    addListener(getName(), "powerUp");

    addListener("publishEvent", getPeerName("chatBot"), "getResponse");
    
    // for begin and end of processing config ?
    // subscribe("runtime", "publishStartConfig");
    subscribe("runtime", "publishFinishedConfig");

    try {
      // copy config if it doesn't already exist
      String resourceBotDir = FileIO.gluePaths(getResourceDir(), "config");
      List<File> files = FileIO.getFileList(resourceBotDir);
      for (File f : files) {
        String botDir = "data/config/" + f.getName();
        File bDir = new File(botDir);
        if (bDir.exists() || !f.isDirectory()) {
          log.info("skipping data/config/{}", botDir);
        } else {
          log.info("will copy new data/config/{}", botDir);
          try {
            FileIO.copy(f.getAbsolutePath(), botDir);
          } catch (Exception e) {
            error(e);
          }
        }
      }

      // copy (if they don't already exist) the chatbots which came with InMoov2
      resourceBotDir = FileIO.gluePaths(getResourceDir(), "chatbot/bots");
      files = FileIO.getFileList(resourceBotDir);
      for (File f : files) {
        String botDir = "data/ProgramAB/" + f.getName();
        if (new File(botDir).exists()) {
          log.info("found data/ProgramAB/{} not copying", botDir);
        } else {
          log.info("will copy new data/ProgramAB/{}", botDir);
          try {
            FileIO.copy(f.getAbsolutePath(), botDir);
          } catch (Exception e) {
            error(e);
          }
        }
      }

    } catch (Exception e) {
      error(e);
    }
    runtime.invoke("publishConfigList");
  }

  public void startServos() {
    startPeer("head");
    startPeer("leftArm");
    startPeer("leftHand");
    startPeer("rightArm");
    startPeer("rightHand");
    startPeer("torso");
  }

  // FIXME .. externalize in a json file included in InMoov2
  public Simulator startSimulator() throws Exception {
    Simulator si = (Simulator) startPeer("simulator");
    return si;
  }

  public void stop() {
    sendToPeer("head", "stop");
    sendToPeer("rightHand", "stop");
    sendToPeer("leftHand", "stop");
    sendToPeer("rightArm", "stop");
    sendToPeer("leftArm", "stop");
    sendToPeer("torso", "stop");
  }

  public void stopGesture() {
    Python p = (Python) Runtime.getService("python");
    p.stop();
  }

  public void stopHeartbeat() {
    purgeTask("publishHeartbeat");
  }

  public void stopNeopixelAnimation() {
    sendToPeer("neopixel", "clear");
  }

  public void systemCheck() {
    log.error("systemCheck()");
    Runtime runtime = Runtime.getInstance();
    int servoCount = 0;
    int servoAttachedCount = 0;
    for (ServiceInterface si : Runtime.getServices()) {
      if (si.getClass().getSimpleName().equals("Servo")) {
        servoCount++;
        if (((Servo) si).getController() != null) {
          servoAttachedCount++;
        }
      }
    }

    setPredicate("systemServoCount", servoCount);
    setPredicate("systemAttachedServoCount", servoAttachedCount);
    setPredicate("systemFreeMemory", Runtime.getFreeMemory());
    Platform platform = Runtime.getPlatform();
    setPredicate("system version", platform.getVersion());
    // ERROR buffer !!!
    invoke("publishEvent", "systemCheckFinished");
  }

  // FIXME - if this is really desired it will drive local references for all
  // servos
  public void waitTargetPos() {
    // FIXME - consider actual reference for this
    sendToPeer("head", "waitTargetPos");
    sendToPeer("rightHand", "waitTargetPos");
    sendToPeer("leftHand", "waitTargetPos");
    sendToPeer("rightArm", "waitTargetPos");
    sendToPeer("leftArm", "waitTargetPos");
    sendToPeer("torso", "waitTargetPos");
  }

}
