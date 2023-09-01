package org.kie.dmn.feel.util;

public class ClassLoaderUtil {

    public static final boolean CAN_PLATFORM_CLASSLOAD = System.getProperty("org.graalvm.nativeimage.imagecode") == null;

    public static ClassLoader findDefaultClassLoader() {
        return org.kie.internal.utils.ClassLoaderUtil.getClassLoader(null, null, true);
    }

    private ClassLoaderUtil() {
        // not allowed for util class.
    }
}
