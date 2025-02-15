package org.myrobotlab.framework.interfaces;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.myrobotlab.framework.Inbox;
import org.myrobotlab.framework.MRLListener;
import org.myrobotlab.framework.MethodEntry;
import org.myrobotlab.framework.Outbox;
import org.myrobotlab.framework.Peer;
import org.myrobotlab.framework.Service;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.service.config.ServiceConfig;
import org.myrobotlab.service.meta.abstracts.MetaData;
import org.slf4j.Logger;

public interface ServiceInterface extends ServiceQueue, LoggingSink, NameTypeProvider, MessageSubscriber, MessageSender, StateSaver, Invoker, StatePublisher, StatusPublisher, 
    ServiceStatus, TaskManager, Attachable, MessageInvoker, Comparable<ServiceInterface> {

  // does this work ?
  public final static Logger log = LoggerFactory.getLogger(Service.class);

  /**
   * When set service will attempt to provide services with no hardware
   * dependencies. Some services have the capablity to mock hardware such as the
   * Serial and Arduino services.
   * 
   * @param b
   *          true to set the virtual mode
   * @return the value
   * 
   */
  public boolean setVirtual(boolean b);

  /**
   * check to see if the service is running in a virtual mode
   * 
   * @return true if in virtual mode.
   * 
   */
  public boolean isVirtual();

  public String[] getDeclaredMethodNames();

  public Method[] getDeclaredMethods();

  public URI getInstanceId();

  public String[] getMethodNames();

  public Method[] getMethods();

  public List<MRLListener> getNotifyList(String key);

  public List<String> getNotifyListKeySet();

  public Inbox getInbox();

  public Outbox getOutbox();

  @Override
  public String getSimpleName();

  /**
   * equivalent to getClass().getCanonicalName()
   * 
   * @return
   */
  public String getType();

  /**
   * Keys to Peers - the keys are string constants the service uses to refer to a
   * Peer service. The key never changes. However, the Peer's name and type can.
   * This returns all peers for a service.
   * 
   * @return
   */
  public Map<String, Peer> getPeers();

  /**
   * Returns peers keys. Peer key is the hardcoded key a composite service
   * references its peers with - actual name may vary
   * 
   * @return
   */
  public Set<String> getPeerKeys();

  /**
   * Returns the peer key if a name is supplied and matches a peer name
   * 
   * @param name
   *          - service name
   * @return - coorisponding peer key if it exists
   */
  public String getPeerKey(String name);

  /**
   * Service life-cycle method: releaseService will call stopService, release
   * its peers, do any derived business logic to release resources, then
   * un-register itself
   */
  public void releaseService();

  /**
   * called by runtime when system is shutting down a service can use this
   * method when it has to do some "ordered" cleanup
   */
  public void preShutdown();

  /**
   * asked by the framework - to determine if the service needs to be secure
   * 
   * @return true/false
   */
  public boolean requiresSecurity();

  public void setInstanceId(URI uri);

  /**
   * Service life cycle method - calls create, and starts any necessary
   * resources to function
   */
  public void startService();

  /**
   * @return get a services current config
   *
   */
  public ServiceConfig getConfig();

  /**
   * sets config - just before apply
   * 
   * @param config
   */
  public void setConfig(ServiceConfig config);

  /**
   * Configure a service by merging in configuration
   * 
   * @param config
   *          the config to load
   * @return the loaded config.
   */
  public ServiceConfig apply(ServiceConfig config);

  /**
   * Service life-cycle method, stops the inbox and outbox threads - typically
   * does not release "custom" resources. It's purpose primarily is to stop
   * messaging from flowing in or out of this service - which is handled in the
   * base Service class. Most times this method will not need to be overriden
   */
  public void stopService();

  public String clearLastError();

  public boolean hasError();

  public void out(String method, Object retobj);

  public boolean isRuntime();

  public String getDescription();

  public Map<String, MethodEntry> getMethodMap();

  public boolean isReady();

  public boolean isRunning();

  /**
   * @param creationCount
   *          the order this service was created in relation to the other
   *          service
   */
  public void setOrder(int creationCount);

  public String getId();

  public String getFullName();

  public void loadLocalizations();

  public void setLocale(String code);

  public int getCreationOrder();

  public MetaData getMetaData();

  /**
   * start a peer using a peerKey E.g. inside InMoov service startPeer("head")
   * 
   * @param peerKey
   * @return
   */
  public ServiceInterface startPeer(String peerKey);

  /**
   * setting an instance id on the service - this represents the running
   * instance's identifier which would be the service's home
   * 
   * @param id
   */
  public void setId(String id);

  /**
   * Get a clone of config that is filtered based on service preference
   * @return
   */
  public ServiceConfig getFilteredConfig();

}
