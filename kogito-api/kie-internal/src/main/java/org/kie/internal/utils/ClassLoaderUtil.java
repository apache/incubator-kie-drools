/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class ClassLoaderUtil {
    private static final ProtectionDomain  PROTECTION_DOMAIN;


    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {

            public Object run() {
                return ClassLoaderUtil.class.getProtectionDomain();
            }
        } );
    }
    
    public static CompositeClassLoader getClassLoader(final ClassLoader[] classLoaders,
                                                      final Class< ? > cls,
                                                      final boolean enableCache) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader currentClassLoader = (cls != null) ? cls.getClassLoader() : ClassLoaderUtil.class.getClassLoader();
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        CompositeClassLoader cl = new CompositeClassLoader( );

        // ClassLoaders are added to the head of the list, so add in reverse
        if ( systemClassLoader != null ) {
            // system classloader
            cl.addClassLoader( systemClassLoader );
        }
        
        if ( currentClassLoader != null ) {
            // the current classloader, typically from a drools-core or drools-compiler class
            cl.addClassLoader( currentClassLoader );
        }
        

        if ( contextClassLoader != null ) {
            // context classloader
            cl.addClassLoader( contextClassLoader );
        }
                
        
        if ( classLoaders != null && classLoaders.length > 0) {
            // the user specified classloaders
            for (ClassLoader classLoader : classLoaders ) {
                if ( classLoader != null ) {
                    cl.addClassLoader( classLoader );
                }
            }
        }

        cl.setCachingEnabled( enableCache );

        return cl;
    }
    
}
