package org.kie.builder;

import org.kie.io.ResourceFactory;

public interface KieServices {

    KieFileSystem newKieFileSystem();

    KieBuilder newKieBuilder(KieFileSystem kieFileSystem);

    ResourceFactory getResourceFactory();

    KieProject newKieProject();

    KieRepository getKieRepository();

    KieContainer getKieContainer(GAV gav);

    KieScanner newKieScanner(KieContainer kieContainer);

    GAV newGav(String groupId, String artifactId, String version);

    public static class Factory {
        private static KieServices INSTANCE;

        static {
            try {
                INSTANCE = (KieServices) Class.forName("org.kie.builder.impl.KieServicesImpl").newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to instance KieServices", e);
            }
        }

        public static KieServices get() {
            return INSTANCE;
        }
    }
}
