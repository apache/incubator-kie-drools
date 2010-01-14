package org.drools.util;

import java.util.concurrent.Callable;

import org.apache.poi.hssf.record.formula.functions.T;

public interface ServiceRegistry {

    public <T> void registerLocator(Class<T> cls,
                                  Callable<Class<T>> cal);

    public void unregisterLocator(Class<T> cls);
    
    public <T> T get(Class<T> cls);

}