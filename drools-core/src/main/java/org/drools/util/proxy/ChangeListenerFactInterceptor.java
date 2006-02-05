package org.drools.util.proxy;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodProxy;

/**
 * This version of an interceptor enables property change listening, 
 * which the rule engine supports to notify changes to facts.
 * 
 * @author Michael Neale
 */
public class ChangeListenerFactInterceptor
    extends
    FactInterceptor {

    
    protected static final Method addListener = getAddListener();
    protected static final Method removeListenerMethod = getRemoveListener();    
    //to cache property info, keyed on class
    protected static final Map propertyInfoCache = new HashMap();
    
    private final PropertyChangeSupport changes;
    
    //a map of properties, keyed on setter method
    protected final Map properties;
    
    
    public ChangeListenerFactInterceptor(Object target, Method[] fieldMethods) {
        super(target, fieldMethods);
        try{ 
            properties = loadPropertyInfo( target );
            changes = new PropertyChangeSupport(target);            
        } catch (IntrospectionException e) {
            throw new IllegalStateException(e);
        }
    }

    private Map loadPropertyInfo(Object target) throws IntrospectionException {
        Map props = (Map) propertyInfoCache.get(target.getClass());
        if (props == null) {
            props = new HashMap();
            BeanInfo info = Introspector.getBeanInfo(target.getClass());
            PropertyDescriptor[] desc = info.getPropertyDescriptors();
            for ( int i = 0; i < desc.length; i++ ) {
                PropertyDescriptor d = desc[i];
                PropertyTuple tup = new PropertyTuple();
                if (d.getReadMethod() != null && 
                        d.getReadMethod().getParameterTypes().length == 0 && 
                        d.getWriteMethod() != null && 
                        d.getWriteMethod().getParameterTypes().length == 1) {
                    tup.name = d.getName();
                    tup.getter = d.getReadMethod();
                    tup.setter = d.getWriteMethod();
                    props.put(tup.setter, tup);
                }
            }
            propertyInfoCache.put(target.getClass(), props);
        }
        return props;
    }    
    
    public Object intercept(Object obj,
                            Method method,
                            Object[] args,
                            MethodProxy proxy) throws Throwable {
        Class decClass = method.getDeclaringClass();
        if (decClass == FieldIndexAccessor.class ) {
            Integer arg = (Integer) args[0];
            return targetFields[arg.intValue() - 1].invoke(target, null);
        } else if (decClass == TargetAccessor.class ) {
            return target;
        } else if (method.equals(addListener)) {
            addListener(args[0]);
            return null;
        } else if (method.equals(removeListenerMethod)) {
            removeListener(args[0]);
            return null;
        } else {

            PropertyTuple prop = (PropertyTuple) properties.get(method);
            if (prop != null) { //we have a property change
                Object old = prop.getter.invoke(target, null);
                changes.firePropertyChange(prop.name, old, args[0]);
            }
            return method.invoke(target, args);
        }
        
        
    }
    

    private void removeListener(Object object) {
        changes.removePropertyChangeListener((PropertyChangeListener) object);
    }

    private void addListener(Object object) {
        changes.addPropertyChangeListener((PropertyChangeListener)object);
        
    }

    private static Method getAddListener() {
        try {
            return ChangeListener.class.getMethod("addPropertyChangeListener", new Class[] {PropertyChangeListener.class});
        }
        catch ( Exception e ) {
            throw new IllegalStateException( e );
        }
    }       
    
    
    private static Method getRemoveListener() {
        try {
            return ChangeListener.class.getMethod("removePropertyChangeListener", new Class[] {PropertyChangeListener.class});
        }
        catch ( Exception e ) {
            throw new IllegalStateException( e );
        }
    }       
    
    
    static class PropertyTuple {
        String name;
        Method setter;
        Method getter;
    }

}
