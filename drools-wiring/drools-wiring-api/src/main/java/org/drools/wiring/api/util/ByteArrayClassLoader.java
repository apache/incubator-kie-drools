package org.drools.wiring.api.util;

import java.security.ProtectionDomain;

public interface ByteArrayClassLoader {
    Class< ? > defineClass(final String name,
                           final byte[] bytes,
                           final ProtectionDomain domain);
}
