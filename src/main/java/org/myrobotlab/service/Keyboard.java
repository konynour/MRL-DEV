package org.myrobotlab.service;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;
import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.interfaces.ServiceInterface;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.math.geometry.Point2df;
import org.slf4j.Logger;

/**
 * Keyboard - The keyboard service will track keys that are pressed so they can
 * be used as input to other services via the addKeyListener(Service) call.
 * 
 *
 */
public class Keyboard extends Service {

  private static final long serialVersionUID = 1L;

  public final static Logger log = LoggerFactory.getLogger(Keyboard.class);

  String lastKeyPressed;

  transient final NativeKeyboard keyboard;
  transient final MouseEvent mouseEvent;

  static public class MouseEvent {
    public Point2df pos = new Point2df();
  }

  public class NativeKeyboard implements NativeKeyListener, NativeMouseInputListener, NativeMouseWheelListener {

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
      int code = nativeKeyEvent.getKeyCode();
      String key = NativeKeyEvent.getKeyText(code);
      invoke("publishKeyCode", code);
      invoke("publishKey", key);
      lastKeyPressed = key;
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
      int code = nativeKeyEvent.getKeyCode();
      String key = NativeKeyEvent.getKeyText(code);
      invoke("publishKeyReleased", key);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
      int code = nativeKeyEvent.getKeyCode();
      String key = NativeKeyEvent.getKeyText(code);
      invoke("publishKeyTyped", key);
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent me) {
      log.debug("nativeMouseClicked {}", me);
      invoke("publishMouseClicked", me);
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent me) {
      log.debug("nativeMousePressed {}", me);
      invoke("publishMousePressed", me);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent me) {
      log.debug("nativeMouseReleased {}", me);
      invoke("publishMouseReleased", me);
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent me) {
      log.debug("nativeMouseDragged {}", me);
      invoke("publishMouseDragged", me);
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent me) {
      log.debug("nativeMouseMoved {}", me);
      invoke("publishMouseMoved", me);
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent me) {
      log.debug("nativeMouseWheelMoved {}", me);
      invoke("nativeMouseWheelMoved", me);
    }
  }

  public Keyboard(String n, String id) {
    super(n, id);
    if (Service.isHeadless()) {
      log.warn("the Keyboard service requires a DISPLAY to function correctly");
      keyboard = null;
      mouseEvent = null;
      return;
    }

    keyboard = new NativeKeyboard();
    mouseEvent = new MouseEvent();
  }

  public void startListening() throws NativeHookException {
    GlobalScreen.registerNativeHook();
    GlobalScreen.addNativeKeyListener(keyboard);
    GlobalScreen.addNativeMouseListener(keyboard);
    GlobalScreen.addNativeMouseMotionListener(keyboard);
  }

  public void stopListening() throws NativeHookException {
    GlobalScreen.removeNativeKeyListener(keyboard);
    GlobalScreen.removeNativeMouseListener(keyboard);
    GlobalScreen.removeNativeMouseMotionListener(keyboard);
    GlobalScreen.unregisterNativeHook();
  }

  @Override
  public void startService() {
    super.startService();
    try {
      if (Service.isHeadless()) {
        log.warn("the Keyboard service requires a DISPLAY to function correctly - will not register hooks");
      } else {
        startListening();
      }
    } catch (Exception e) {
      log.error("could not register", e);
    }
  }

  @Override
  public void stopService() {
    super.stopService();
    try {
      if (Service.isHeadless()) {
        log.warn("the Keyboard service requires a DISPLAY to function correctly - will not un-register hooks");
      } else {
        stopListening();
      }
    } catch (Exception e) {
      log.error("could not unregister", e);
    }
  }

  /*
   * this method is what other services would use to subscribe to keyboard
   * events
   */
  public void addKeyListener(Service service) {
    addListener("publishKey", service.getName(), "onKey");
  }

  public void addKeyListener(String serviceName) {
    ServiceInterface s = Runtime.getService(serviceName);
    addKeyListener((Service) s);
  }

  /*
   * a onKey event handler for testing purposes only
   */
  public String onKey(String key) {
    log.info("onKey {}", key);
    return key;
  }

  /*
   * internal publishing point - private ?
   * 
   */
  public String publishKey(String key) {
    log.info("publishKey {}", key);
    return key;
  }

  public Integer publishKeyCode(Integer code) {
    log.info("publishKey {}", code);
    return code;
  }

  public String publishKeyTyped(String key) {
    log.debug("publishKeyTyped {}", key);
    return key;
  }

  public String publishKeyReleased(String key) {
    log.debug("publishKeyReleased {}", key);
    return key;
  }

  // ========== mouse events begin ===============
  public MouseEvent publishMouse() {
    return mouseEvent;
  }

  public MouseEvent publishMouseClicked(NativeMouseEvent me) {
    return mouseEvent;
  }

  public MouseEvent publishMousePressed(NativeMouseEvent me) {
    return mouseEvent;
  }

  public MouseEvent publishMouseReleased(NativeMouseEvent me) {
    return mouseEvent;
  }

  public MouseEvent publishMouseDragged(NativeMouseEvent me) {
    return mouseEvent;
  }

  public MouseEvent publishMouseMoved(NativeMouseEvent me) {
    mouseEvent.pos.x = me.getX();
    mouseEvent.pos.y = me.getY();
    return mouseEvent;
  }

  public MouseEvent publishMouseWheelMoved(NativeMouseWheelEvent me) {
    return mouseEvent;
  }
  // ========== mouse events begin ===============

  public String readKey() {
    return lastKeyPressed;
  }

  public static void main(String[] args) {
    LoggingFactory.init(Level.INFO);

    Runtime.start("gui", "SwingGui");
    Keyboard keyboard = (Keyboard) Runtime.start("keyboard", "Keyboard");
    // keyboard.stopService();

  }

}
