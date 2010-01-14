package org.drools.util;

import java.util.concurrent.Callable;

import org.apache.poi.hssf.record.formula.functions.T;

public interface ServiceRegistry {

    public void registerLocator(Class cls,
                                  Callable cal);

    public void unregisterLocator(Class cls);
    
    public <T> T get(Class<T> cls);

}