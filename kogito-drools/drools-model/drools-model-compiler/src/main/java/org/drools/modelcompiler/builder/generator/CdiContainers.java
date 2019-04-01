package org.drools.modelcompiler.builder.generator;

public class CdiContainers {

    public static boolean isRunningInContainer() {
        try {
            Class.forName("javax.enterprise.context.ApplicationScoped");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
