package org.drools.util;

import java.util.concurrent.Callable;

import org.drools.Service;

public interface ServiceRegistry extends Service {

    public void registerLocator(Class cls,
                                  Callable cal);

    public void unregisterLocator(Class cls);
    
//    public void registerInstance(Service service);
//
//  public void unregisterInstance(Service service);    
    
    public <T> T get(Class<T> cls);

}