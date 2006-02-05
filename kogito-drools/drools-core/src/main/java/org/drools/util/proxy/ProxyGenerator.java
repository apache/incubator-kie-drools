package org.drools.util.proxy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

/**
 * Generates proxies for shadow facts, field access and property change listeners.
 * Classes with final methods will not work too well (will not be able to call the final methods).
 * 
 * Classes need to have a visible empty constructor for this to work (can be package protected).
 * Classes that are final can also not be proxied.
 * 
 * These are the limitations placed on by using objects in rules.
 * 
 * However, the extractFieldMethodsInOrder method will work on any class. This provides an array 
 * of method objects which can be used to provide ordered "field" access to objects. This 
 * can be used in cases where the proxy will not work.
 * 
 * @author Michael Neale
 *
 */
public class ProxyGenerator {

    //cache the generated classes, and lists of indexable "fields" on each pojo class
    static Map cache = new HashMap();
    
    /** 
     * Generate a proxy that adds in a field index accessor, and an accessor to get to the target.
     * Shadow and non shadow options are available etc.
     * 
     * @param pojo The end user object which is to be proxied. Must have empty constructor, be non final etc.
     * @param shadow If true, accessible "fields" (indicated by "getters") will be shallow copied, and accessed when reading.
     * @param propertyChangeListener If true, this will add in property change support. So all setters notify of changes.
     * This can be used with the property change support in the rule engine. This option is mutually exclusive with shadow,
     * as they are both ways to solve the same problem. 
     * 
     * @throws IOException If unable to load the class data to proxy. 
     */
    public static Object generateProxy(Object pojo, boolean shadow, boolean propertyChangeListener) throws IOException {
        Class cls = pojo.getClass();
        
        GeneratedEntry holder = getEntryFor(cls);
        
        Factory factory = holder.instanceFactory;      
        Method[] fieldMethods = holder.fieldMethods;
        FactInterceptor interceptor;
        if (shadow) {
            interceptor = new ShadowedFactInterceptor(pojo, fieldMethods);
        } else if (propertyChangeListener) {
            interceptor = new ChangeListenerFactInterceptor(pojo, fieldMethods);
        } else {
            interceptor = new FactInterceptor(pojo, fieldMethods);
        }

        return factory.newInstance(interceptor);
    }   
    
    
    /**
     * Keep a cache of generated classes (actually keeps a prototype instance)
     * and lists of methods for accessing fields.
     */
    private static GeneratedEntry getEntryFor(Class pojoClass) throws IOException {
        GeneratedEntry entry = (GeneratedEntry) cache.get(pojoClass);
        if (entry == null) {
            entry = new GeneratedEntry();
            entry.instanceFactory = createInstanceFactory(pojoClass, new Class[] {FieldIndexAccessor.class, 
                                                                                  TargetAccessor.class, 
                                                                                  ChangeListener.class});
            entry.fieldMethods = extractFieldMethodsInOrder(pojoClass);
            cache.put(pojoClass, entry);
        }
        return entry;
    }

    /**
     * Create a instance of the CGLib factory, which will then be used to quickly create new instances
     * in the future.
     */
    private static Factory createInstanceFactory(Class pojoClass, Class[] interfaces) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(pojoClass);
        enhancer.setInterfaces(interfaces);
        //just need some callback of the correct type to keep it happy, gets replaced when instantiating anyway.
        enhancer.setCallback(new FactInterceptor(null, null));
        return (Factory) enhancer.create();
    }
    
    
    /** 
     * Get an array of methods that are indexable as fields.
     * This is also what may be shadowed.
     * 
     * This is generally useful even outside of the proxy. 
     * To access field 1, you do
     * <code>method[1].invoke(yourObject, null);</code>  - Simple !
     * 
     * This uses ASM to work out the innards of the class.
     */
    public static Method[] extractFieldMethodsInOrder(Class pojoClass) throws IOException {
        FieldOrderInspector ext = new FieldOrderInspector(pojoClass);
        return (Method[]) ext.getPropertyGetters().toArray(new Method[] {});
        
    }
    

    /** used for the cache */
    static class GeneratedEntry {
        Factory instanceFactory;
        Method[] fieldMethods;
    }
    
}
