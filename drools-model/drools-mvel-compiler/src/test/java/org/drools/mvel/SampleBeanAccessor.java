package org.drools.mvel;

public class SampleBeanAccessor {
  public Object getProperty(String name, Object contextObj, Object variableFactory) {
    return contextObj;
  }

  public Object setProperty(String name, Object contextObj, Object variableFactory, Object value) {
    return contextObj;
  }

}
