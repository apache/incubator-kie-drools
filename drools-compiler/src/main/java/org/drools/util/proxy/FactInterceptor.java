package org.drools.util.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/** Dummy parent class */
public class FactInterceptor implements MethodInterceptor {

    public Object intercept(Object arg0,
                            Method arg1,
                            Object[] arg2,
                            MethodProxy arg3) throws Throwable {
        return null;
    }

}
