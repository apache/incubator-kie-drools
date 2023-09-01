package org.drools.model.functions;

public class NativeImageTestUtil {
    // Used only for test purposed, do not call this as it simulates the code path for native image
    public static void setNativeImage() {
        IntrospectableLambda.IS_NATIVE_IMAGE = true;
    }

    // Used only for test purposed, do not call this as it simulates the code path for native image
    public static void unsetNativeImage() {
        IntrospectableLambda.IS_NATIVE_IMAGE = false;
    }

}
