package org.myrobotlab.service;

import org.myrobotlab.framework.Service;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.service.config.ServiceConfig;
import org.myrobotlab.service.config.ServoConfig;
import org.slf4j.Logger;

public class SpotMicro extends Service {

  private static final long serialVersionUID = 1L;

  public final static Logger log = LoggerFactory.getLogger(SpotMicro.class);

  public SpotMicro(String n, String id) {
    super(n, id);
  }

  @Override
  public ServiceConfig apply(ServiceConfig c) {
    return c;
  }

  @Override
  public ServiceConfig getConfig() {
    return config;
  }

  public static void main(String[] args) {
    try {

      LoggingFactory.init(Level.INFO);

      Runtime.start("spot", "SpotMicro");
      Runtime.start("webgui", "WebGui");

    } catch (Exception e) {
      log.error("main threw", e);
    }
  }
}
