package org.kie.scanner;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

import org.kie.internal.utils.KieTypeResolver;


public class KieURLClassLoader extends URLClassLoader implements KieTypeResolver {

    public KieURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public KieURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public KieURLClassLoader(URL[] urls) {
        super(urls);
    }

}
