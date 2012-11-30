package org.kie.builder;

import java.io.File;

import org.kie.io.ResourceFactory;
import org.kie.util.ServiceRegistryImpl;

public interface KieServices {
    
    ResourceFactory getResourceFactory();

    KieRepository getKieRepository();

    /**
     * Returns a KieContainer for the default GAV, as defined by KieRepository. 
     * 
     * @return
     *     KieContainer
     */
    KieContainer getKieContainer();
    
    KieContainer getKieContainer(GAV gav);
    
    KieScanner newKieScanner(KieContainer kieContainer);    
    
    KieBuilder newKieBuilder(File rootFolder);
    
    KieBuilder newKieBuilder(KieFileSystem kieFileSystem);    

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
