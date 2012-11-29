package org.kie.builder;

import org.kie.io.ResourceFactory;
import org.kie.util.ServiceRegistryImpl;

public interface KieServices {
    
    ResourceFactory getResourceFactory();

    KieRepository getKieRepository();

    KieContainer getKieContainer(GAV gav);


    public static class Factory {
        private static KieServices INSTANCE;

        static {
            try {
                INSTANCE = ServiceRegistryImpl.getInstance().get( KieServices.class );
            } catch (Exception e) {
                throw new RuntimeException("Unable to instance KieServices", e);
            }
        }

        public static KieServices get() {
            return INSTANCE;
        }
    }
}
