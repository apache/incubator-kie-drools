package org.drools.mvelcompiler.context;

import java.lang.reflect.Method;

/* Represents static methods and external-defined functions */
public class StaticMethod {

    final String methodName;
    final Method method;

    public StaticMethod(String methodName, Method method) {
        this.methodName = methodName;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }
}
