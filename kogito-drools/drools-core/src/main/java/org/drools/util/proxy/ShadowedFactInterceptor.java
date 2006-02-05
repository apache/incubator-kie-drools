package org.drools.util.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodProxy;

/**
 * This keeps shadow copies of facts that are accessible as indexed fields.
 * 
 * Scalar values are copies, Objects are pointer copies (thus if they are
 * immutable types, then they are effectively copies).
 * 
 * Only values which are accessible as getField are shadowed. All other methods
 * are passed through to the target.
 * 
 * If a getter is called on a field which is shadowed, the shadow is returned.
 * If the getField method of the FieldIndexAccessor interface is used, the shadow is returned.
 * Otherwise its just normal, turtles all the way down.
 * 
 * @author Michael Neale
 */
public class ShadowedFactInterceptor extends FactInterceptor {

    private final Object[] values;
    private final Map   fieldToValue;
    
    public ShadowedFactInterceptor(Object target,
                                   Method[] fieldMethods) {

        super( target,
               fieldMethods );
        
        fieldToValue = new HashMap();
        int numOfFields = fieldMethods.length;
        values = new Object[numOfFields];
        try {
            for ( int i = 0; i < numOfFields; i++ ) {

                //read all the objects into a value array
                Method method = fieldMethods[i];
                Object val = method.invoke( target, null);
                values[i] = fieldMethods[i].invoke( target,
                                                    null );
                fieldToValue.put(method, val);
            }
        }
        catch ( Exception e ) {
            throw new IllegalStateException( e );
        }

    }

    public Object intercept(Object obj,
                            Method method,
                            Object[] args,
                            MethodProxy proxy) throws Throwable {
        
        //this one is a little different...
        Class methodClass = method.getDeclaringClass();
        
        if (methodClass == FieldIndexAccessor.class ) {
            Integer arg = (Integer) args[0];
            return values[arg.intValue() - 1];
        } else if (methodClass == TargetAccessor.class ) {
            return target;
        } else {
            //check if method is in targetFields
            Object val = fieldToValue.get(method);
            if (val != null) {
                return val;
            } else {
                //bugger it, lets pass through.
                return method.invoke(target, args);
            }
        }
    }

}
