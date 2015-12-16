/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java Tip 98: Reflect on the Visitor design pattern. Implement visitors in
 * Java, using reflection.
 * http://www.javaworld.com/javaworld/javatips/jw-javatip98.html
 *
 * Michael Neale says: I really hate this code. Not really a helpful use
 * of reflection, always annoys me.
 */
public abstract class ReflectiveVisitor
    implements
        Visitor {

    protected static final transient Logger logger = LoggerFactory.getLogger(ReflectiveVisitor.class);

    static final String newline = System.getProperty( "line.separator" );
    private Map<Class<?>, Method> methodCache = new HashMap<Class<?>, Method>();
    
    public void visit(final Object object) {
        Method method = null;
        try {
            if ( object != null ) {
                method = getMethod( object.getClass() );
                method.invoke( this,
                               new Object[]{object} );
            } else {
                method = getClass().getMethod( "visitNull",
                                               (Class[]) null );
                method.invoke( this,
                               (Object[]) null );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e.toString() + " : " + object, e);
        }
    }

    private Method getMethod(final Class<?> clazz) {
        if (methodCacheContains(clazz)) {
            return getMethodFromCache(clazz);
        }

        Class<?> newClazz = clazz;
        Method method = null;
        
        // Try the superclasses
        while ( method == null && newClazz != Object.class ) {
            String methodName = newClazz.getName();
            methodName = "visit" + methodName.substring( methodName.lastIndexOf( '.' ) + 1 );
            try {
                method = getClass().getMethod(methodName,
                        new Class[] { newClazz });
            } catch (final NoSuchMethodException e) {
                newClazz = newClazz.getSuperclass();
            }
        }

        // Try the interfaces.
        if ( newClazz == Object.class ) {
            final Class<?>[] interfaces = clazz.getInterfaces();
            for ( int i = 0; i < interfaces.length && method == null; i++ ) {
                String methodName = interfaces[i].getName();
                methodName = "visit" + methodName.substring( methodName.lastIndexOf( '.' ) + 1 );
                try {
                    method = getClass().getMethod(methodName,
                            new Class[] { interfaces[i] });
                } catch (final NoSuchMethodException e) {
                    // swallow
                }
            }
        }
        if ( method == null ) {
            try {
                method = getClass().getMethod( "visitObject",
                                               new Class[]{Object.class} );
            } catch ( final Exception e ) {
                // Shouldn't happen as long as all Visitors extend this class
                // and this class continues to implement visitObject(Object).
                throw new RuntimeException( e.toString() + " : " + clazz, e.getCause() );
            }
        }
        addMethodToCache(clazz, method);
        return method;
    }

    public void visitObject(final Object object) {
        logger.error("no visitor implementation for : " + object.getClass() + " : " + object);
    }
    
    private void addMethodToCache(Class<?> clazz, Method m) {
        methodCache.put(clazz, m);
    }
    
    private Method getMethodFromCache(Class<?> clazz) {
        return methodCache.get(clazz);
    }
    
    private boolean methodCacheContains(Class<?> clazz) {
        return methodCache.containsKey(clazz);
    }
}
