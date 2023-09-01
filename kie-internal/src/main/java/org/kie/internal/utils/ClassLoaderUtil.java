package org.kie.internal.utils;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

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
