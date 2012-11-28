package org.kie.builder;

import org.kie.io.ResourceFactory;

public interface KieServices {

    KieFileSystem newKieFileSystem();

    KieBuilder newKieBuilder();

    ResourceFactory getResourceFactory();

    KieProject newKieProject();

    KieRepository getKieRepository();

    KieContainer getKieContainer(GAV gav);

    KieScanner newKieScanner(KieContainer kieContainer);

    GAV newGav(String groupId, String artifactId, String version);

    public static class Factory {
        public static KieServices get() {
            return null;
        }
    }
}
