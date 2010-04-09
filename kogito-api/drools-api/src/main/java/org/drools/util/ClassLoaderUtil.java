package org.drools.util;

public class ClassLoaderUtil {
    public static CompositeClassLoader getClassLoader(final ClassLoader classLoader,
                                                      final Class< ? > cls,
                                                      final boolean enableCache) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader currentClassLoader = (cls != null) ? cls.getClassLoader() : ClassLoaderUtil.class.getClassLoader();
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        CompositeClassLoader cl = new CompositeClassLoader( null );

        if ( classLoader != null ) {
            // the user specified classloader
            cl.addClassLoader( classLoader );
        }

        if ( currentClassLoader != null ) {
            // the current classloader, typically from a drools-core or drools-compiler class
            cl.addClassLoader( currentClassLoader );
        }

        if ( contextClassLoader != null ) {
            // context classloader
            cl.addClassLoader( contextClassLoader );
        }

        if ( systemClassLoader != null ) {
            // system classloader
            cl.addClassLoader( systemClassLoader );
        }

        cl.setCachingEnabled( enableCache );

        return cl;
    }
}
