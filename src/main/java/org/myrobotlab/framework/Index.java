package org.myrobotlab.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.slf4j.Logger;

public class Index<T> {

  public final static Logger log = LoggerFactory.getLogger(IndexNode.class);

  private IndexNode<T> root = new IndexNode<T>();

  public static void main(String[] args) throws IOException {
    LoggingFactory.init(Level.DEBUG);

    Index<ServiceReservation> reservations = new Index<ServiceReservation>();

    ArrayList<ServiceReservation> ret = reservations.crawlForDataStartingWith("RightArm");
    log.info("size {} from query of RightArm", ret.size());
    log.info(reservations.toString());
    log.info("here");

    // reservations.put("arm.database.config", new
    // ServiceReservation("Rotate","Servo","a servo"));
    /*
     * reservations.put("global.database.qa.server", "Db2udb04");
     * reservations.put("global.database.qa.port", "50005");
     * reservations.put("global.database.qa.schema", "TESTDB2");
     * reservations.put("global.database.qa.userid", "odcad01");
     * reservations.put("global.database.qa.password", "3dglasez");
     * 
     * reservations.put("global.database.dev.server", "Db2udb03");
     * reservations.put("global.database.dev.port", "50004");
     * reservations.put("global.database.dev.schema", "FIM");
     * reservations.put("global.database.dev.userid", "odcad00");
     * reservations.put("global.database.dev.password", "3dglases");
     */
    /*
     * 
     * String test = reservations.get("global.database.dev.userid", "zod");
     * log.info(test);
     * 
     * String dbConfigKey = reservations.get("global.database.config");
     * IndexNode dbConfigNode =
     * reservations.getNode(String.format("global.database.%s" ,dbConfigKey));
     * HashMap<String,IndexNode> dbConfig = dbConfigNode.getBranches();
     * 
     * log.info(dbConfig.get("server").getValue().toString());
     * log.info(dbConfig.get("port").getValue().toString());
     * log.info(dbConfig.get("schema").getValue().toString());
     * log.info(dbConfig.get("userid").getValue().toString());
     * log.info(dbConfig.get("password").getValue().toString());
     * 
     * // reservations.put("global.database.config","qa"); dbConfigKey =
     * reservations.get("global.database.config"); dbConfigNode = reservations
     * .getNode(String.format("global.database.%s",dbConfigKey)); dbConfig =
     * dbConfigNode.getBranches();
     * 
     * log.info(dbConfig.get("server").getValue().toString());
     * log.info(dbConfig.get("port").getValue().toString());
     * log.info(dbConfig.get("schema").getValue().toString());
     * log.info(dbConfig.get("userid").getValue().toString());
     * log.info(dbConfig.get("password").getValue().toString());
     */
    HashMap<String, String> hash = new HashMap<String, String>();
    ArrayList<String> array = new ArrayList<String>();
    hash.put("hash1", "hashValue1");
    hash.put("hash2", "hashValue2");
    hash.put("hash3", "hashValue3");
    array.add("array1");
    array.add("array2");
    array.add("array3");
    array.add("array4");
    // config.put("arrayList", array);
    // config.put("hashMap", hash);

    // File outfile = new File("out.xml");

    // FileInputStream in = new FileInputStream(outfile);
    // Index config2 = new Index();
    // config2.loadFromXML(in);
  }

  public Index() {
    /*
     * if (properties == null) { properties = new Properties(); }
     */
  }

  public void buildTree(String key, T value) {
    root.buildTree(root, key, value);
  }

  /*
   * 
   * 
   * public void load() { load((String) null); }
   * 
   * public void load(String path) { load(path, null); }
   * 
   * /* public void load(String path, String propFileName) { if (path == null) {
   * path = System.getProperty("user.dir"); }
   * 
   * String configPath = propFileName;
   * 
   * try { Properties properties = new Properties(); log.info(
   * "loading config file " + configPath); properties.load(new
   * FileInputStream(configPath));
   * 
   * for (String key : properties.stringPropertyNames()) { root.buildTree(root,
   * key, properties.getProperty(key)); //IndexNode.buildTree(root, key,
   * properties.getProperty(key)); } } catch (Exception e) { log.error(
   * "config did not load from location " + configPath);
   * Logging.logException(e); } }
   */

  public void clear() {
    root.clear();
  }

  /*
   * public static Index getInstance() { return getInstance((String) null); }
   * 
   * public static Index getInstance(String path) { if (instance == null) {
   * log.debug("new config"); instance = new Index(); } if (path != null) {
   * load(path); } return instance; }
   */

  public ArrayList<T> crawlForDataStartingWith(String inkey) {
    return crawlForDataStartingWith(inkey, 20);
  }

  public ArrayList<T> crawlForDataStartingWith(String inkey, int maxItemsToReturn) {

    // TODO - start with a TreeMap ?
    ArrayList<T> ret = new ArrayList<T>();

    // get the limb in the tree which is keyed for this inkey
    return root.crawlForData(ret, maxItemsToReturn, inkey);

  }

  public ArrayList<T> flatten() {
    return flatten(null);
  }

  /* FULL IndexNode + key Set<Entry<String, IndexNode<ServiceReservation>>> */
  public ArrayList<T> flatten(String key) {
    ArrayList<T> ret = new ArrayList<T>();
    return root.crawlForData(ret, 0, key);
  }

  // --------------- config / property extensions begin --------------
  public T get(String name) {
    return get(name, null);
  }

  public T get(String name, T defaultValue) {

    IndexNode<T> n = root.getNode(name);
    if (n == null) {
      return defaultValue;
    } else {
      return n.getValue(defaultValue);
    }
  }

  public Set<String> getKeySetFromNode(String key) {

    IndexNode<T> n = root.getNode(key);
    if (n != null) {
      return n.getBranches().keySet();
    }

    return null;
  }

  public IndexNode<T> getNode(String key) {
    return root.getNode(key);
  }

  /**
   * find a node key, by browsing the whole tree.. ( wanted to use
   * crawlForDataStartingWith but seem nok )
   * 
   * @param parent
   *          - parent node key
   * @param key
   *          - this key
   * @return - result node found
   */
  public String findNode(String parent, String key) {

    Set<String> childs = null;
    if (parent == null) {
      childs = getRootNode().getBranches().keySet();
    } else {
      if (getNode(parent) != null) {
        childs = getNode(parent).getBranches().keySet();
      } else {
        return null;
      }
    }

    for (String entrie : childs) {
      String nextNode = "";
      if (!(parent == null)) {
        nextNode = parent + "." + entrie;
      } else {
        nextNode = entrie;
      }

      // log.info(key + " key " + nextNode);
      if (nextNode.toLowerCase().contains(key.toLowerCase())) {
        return nextNode;
      }
      String nodeIterated = findNode(nextNode, key);
      if (nodeIterated != null) {
        return nodeIterated;
      }

    }

    return null;

  }

  public String findNode(String key) {
    return findNode(null, key);
  }

  public IndexNode<T> getOrCreateNode(String key) {
    IndexNode<T> node = getNode(key);
    if (node == null) {
      // IndexNode<T> newNode = new IndexNode<T>();
      return root.putNode(key, null);
    }
    return node;
  }

  public IndexNode<T> getRootNode() {
    return root;
  }

  /**
   * check if selected node have leafs and return them as key list
   * 
   * @param node
   *          - node to get leafs from
   * @return all leaves
   */
  public ArrayList<String> getLeafs(String node) {

    ArrayList<String> leafs = new ArrayList<String>();
    log.debug("getNodeLeafs for {}", node);
    if (root.getNode(node) != null) {

      Set<String> entries = root.getNode(node).getBranches().keySet();
      // log.info(entries+"entries");

      for (String key : entries) {
        if (!(root.getNode(node + "." + key) == null) && root.getNode(node + "." + key).size() == 0) {

          leafs.add(node + "." + key);
        }

      }
    }
    return leafs;
  }

  public Enumeration<String> propertyNames() {
    return root.propertyNames();
  }

  public IndexNode<T> put(String name, T value) {
    return root.putNode(name, value);
  }

  @Override
  public String toString() {
    return root.toString();
  }

}
