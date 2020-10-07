package org.drools.ancompiler;

public class ClazzUtils {

    public static CompiledNetwork newCompiledNetworkInstance(Class<?> aClass) {
        try {
            return (CompiledNetwork) aClass.getDeclaredConstructor(org.drools.core.spi.InternalReadAccessor.class)
                    .newInstance(null);
        } catch (Exception e) { // TODO LUCA is indexable constraint needed?
            throw new RuntimeException(e);
        }
    }

}
