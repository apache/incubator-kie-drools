package org.drools.mvel;

import java.util.HashMap;
import java.util.Map;

public class SampleBean {
  private Map<String, Object> map = new HashMap<String, Object>();
  private Map<String, Integer> map2 = new HashMap<String, Integer>();

  public SampleBean() {
    map.put("bar", new Bar());
  }

  public Object getProperty(String name) {
    return map.get(name);
  }

  public Object setProperty(String name, Object value) {
    map.put(name, value);
    return value;
  }

  public Map<String, Integer> getMap2() {
    return map2;
  }

  public void setMap2(Map<String, Integer> map2) {
    this.map2 = map2;
  }
}
