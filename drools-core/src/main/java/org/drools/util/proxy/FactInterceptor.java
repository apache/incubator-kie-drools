package org.drools.util.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * This implements the stuff used by the dynamic proxies that are wrapped around facts.
 * This version is a simple non-shadow one.
 * 
 * This is where all the field handling is done.
 * 
 * @author Michael Neale
 */
public class FactInterceptor
    implements
    MethodInterceptor {

    protected final Object target;
    protected static final Method indexAccessor = getIndexAccessor();
    protected static final Method targetAccessor = getTargetAccessor();
    protected final Method[] targetFields;
    
    public FactInterceptor(Object target, Method[] fieldMethods) {
        this.target = target;
        this.targetFields = fieldMethods;
    }    
    
    public Object intercept(Object obj,
                            Method method,
                            Object[] args,
                            MethodProxy proxy) throws Throwable {
        if (method.getDeclaringClass() == indexAccessor.getDeclaringClass() ) {
            Integer arg = (Integer) args[0];
            return targetFields[arg.intValue() - 1].invoke(target, null);
        } else if (method.getDeclaringClass() == targetAccessor.getDeclaringClass() ) {
            return target;
        }
        return method.invoke(target, args);
        
    }
    
    private static Method getIndexAccessor() {
        try {
            return FieldIndexAccessor.class.getMethod("getField", new Class[]{int.class});
        }
        catch ( Exception e ) {
            throw new IllegalStateException( e );
        }
    }
    
    private static Method getTargetAccessor() {
        try {
            return TargetAccessor.class.getMethod("getTarget", null);
        }
        catch ( Exception e ) {
            throw new IllegalStateException( e );
        }
    }    
    

}
