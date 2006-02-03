package org.drools.util.proxy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

public class ProxyGenerator {

    //cache the generated classes, and lists of indexable "fields" on each pojo class
    private static Map cache = new HashMap();
    
    /** 
     * Generate a proxy that adds in a field index accessor, and an accessor to get to the target.
     * Shadow and non shadow options are available.
     * 
     * @throws IOException If unable to load the class data to proxy. 
     */
    public static Object generateProxy(Object pojo, boolean shadow) throws IOException {
        Class cls = pojo.getClass();
        
        GeneratedEntry holder = getEntryFor(cls);
        
        Factory factory = holder.instanceFactory;      
        Method[] fieldMethods = holder.fieldMethods;
        FactInterceptor interceptor;
        if (shadow) {
            interceptor = new ShadowedFactInterceptor(pojo, fieldMethods);
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
            entry.instanceFactory = createInstanceFactory(pojoClass);
            entry.fieldMethods = calcFieldMethodsInOrder(pojoClass);
            cache.put(pojoClass, entry);
        }
        return entry;
    }

    /**
     * Create a instance of the CGLib factory, which will then be used to quickly create new instances
     * in the future.
     */
    private static Factory createInstanceFactory(Class pojoClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(pojoClass);
        enhancer.setInterfaces(new Class[] {FieldIndexAccessor.class, TargetAccessor.class});
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
    public static Method[] calcFieldMethodsInOrder(Class pojoClass) throws IOException {
        FieldOrderInspector ext = new FieldOrderInspector(pojoClass);
        return (Method[]) ext.getPropertyGetters().toArray(new Method[] {});
        
    }
    

    /** used for the cache */
    static class GeneratedEntry {
        Factory instanceFactory;
        Method[] fieldMethods;
    }
    
}
