/**
 *                    
 * @author grog (at) myrobotlab.org
 *  
 * This file is part of MyRobotLab (http://myrobotlab.org).
 *
 * MyRobotLab is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License 2.0 as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * MyRobotLab is distributed in the hope that it will be useful or fun,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License 2.0 for more details.
 *
 * All libraries in thirdParty bundle are subject to their own license
 * requirements - please refer to http://myrobotlab.org/libraries for 
 * details.
 * 
 * Enjoy !
 * 
 * */

package org.myrobotlab.service.interfaces;

import java.util.List;
import java.util.Map;

import org.myrobotlab.framework.Message;
import org.myrobotlab.framework.interfaces.NameProvider;
import org.myrobotlab.net.Connection;

public interface Gateway extends NameProvider {

  public void connect(String uri) throws Exception; // <-- FIXME invalid I
                                                    // assume ?

  public List<String> getClientIds();

  // FIXME - change to getConnections !!...
  // TODO getConnection() would be the context of a gateway which connections
  // its responsible for
  public Map<String, Connection> getClients();

  public void sendRemote(final Message msg) throws Exception;

  // FIXME - remove - not necessary - timeout implemented in waitForMsg
  // public Object sendBlockingRemote(final Message msg, Integer timeout) throws
  // Exception;

  public boolean isLocal(Message msg);

  public Message getDescribeMsg(String connId);

}
