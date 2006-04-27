package org.drools.util;

/*
 * Copyright 2005 JBoss Inc
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

import java.lang.reflect.Method;

import org.drools.Visitor;

/**
 * Java Tip 98: Reflect on the Visitor design pattern. Implement visitors in
 * Java, using reflection.
 * http://www.javaworld.com/javaworld/javatips/jw-javatip98.html
 * 
 * @author Jeremy Blosser
 */
public abstract class ReflectiveVisitor
    implements
    Visitor {
    static final String newline = System.getProperty( "line.separator" );

    public void visit(Object object) {
        try {
            if ( object != null ) {
                Method method = getMethod( object.getClass() );
                method.invoke( this,
                               new Object[]{object} );
            } else {
                Method method = getClass().getMethod( "visitNull",
                                                      (Class[]) null );
                method.invoke( this,
                               (Object[]) null );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private Method getMethod(Class clazz) {
        Class newClazz = clazz;
        Method method = null;

        // Try the superclasses
        while ( method == null && newClazz != Object.class ) {
            String methodName = newClazz.getName();
            methodName = "visit" + methodName.substring( methodName.lastIndexOf( '.' ) + 1 );
            try {
                method = getClass().getMethod( methodName,
                                               new Class[]{newClazz} );
            } catch ( NoSuchMethodException e ) {
                newClazz = newClazz.getSuperclass();
            }
        }

        // Try the interfaces.
        if ( newClazz == Object.class ) {
            Class[] interfaces = clazz.getInterfaces();
            for ( int i = 0; i < interfaces.length; i++ ) {
                String methodName = interfaces[i].getName();
                methodName = "visit" + methodName.substring( methodName.lastIndexOf( '.' ) + 1 );
                try {
                    method = getClass().getMethod( methodName,
                                                   new Class[]{interfaces[i]} );
                } catch ( NoSuchMethodException e ) {
                    // swallow
                }
            }
        }
        if ( method == null ) {
            try {
                method = getClass().getMethod( "visitObject",
                                               new Class[]{Object.class} );
            } catch ( Exception e ) {
                // Shouldn't happen as long as all Visitors extend this class
                // and this class continues to implement visitObject(Object).
                e.printStackTrace();
            }
        }
        return method;
    }

    public void visitObject(Object object) {
        System.err.println( "no visitor implementation for : " + object.getClass() + " : " + object );
    }
}
