package org.kie.internal.utils;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

import org.kie.api.builder.KieModule;


public class NoDepsClassLoaderResolver implements ClassLoaderResolver {
    private static final ProtectionDomain  PROTECTION_DOMAIN;

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {

            public Object run() {
                return NoDepsClassLoaderResolver.class.getProtectionDomain();
            }
        } );
    }
    

    @Override
    public ClassLoader getClassLoader(KieModule kmodule) {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        if (parent == null) {
            parent = NoDepsClassLoaderResolver.class.getClassLoader();
        }
        return parent;
    }

}
