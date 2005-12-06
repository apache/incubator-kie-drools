package org.drools.util.concurrent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.drools.WorkingMemory;

/**
 * Helper class that synchronizes all public methods of WorkingMemory via a
 * <code>java.lang.reflect.Proxy</code>
 */
public class WorkingMemorySynchronizedProxy {

    /**
     * Returns an instance of a proxy class for the specified WorkingMemory in which
     * all method calls are synchronized.
     * @param workingMemory
     * @return A proxy that invokes workingMemory methods within a synchronized block.
     */
    public static WorkingMemory createProxy(final WorkingMemory workingMemory) {
        return (WorkingMemory) Proxy.newProxyInstance(
                WorkingMemory.class.getClassLoader(),
                new Class[]{WorkingMemory.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        synchronized(workingMemory) {
                            return method.invoke(workingMemory, args);
                        }
                    }
                });
    }
}
