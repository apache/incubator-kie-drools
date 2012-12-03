package org.kie.builder;

import java.io.File;

import org.kie.command.KieCommands;
import org.kie.concurrent.KieExecutors;
import org.kie.io.KieResources;
import org.kie.logger.KieLoggers;
import org.kie.marshalling.KieMarshallers;
import org.kie.persistence.jpa.KieStoreServices;
import org.kie.util.ServiceRegistryImpl;

public interface KieServices {
    
    KieResources getResources();

    KieRepository getKieRepository();

    /**
     * Returns KieContainer for the classpath
     */
    KieContainer getKieContainer();
    
    KieContainer getKieContainer(GAV gav);
    
    KieCommands getCommands();
    
    KieMarshallers getMarshallers();
    
    KieLoggers getLoggers();
    
    KieExecutors getExecutors();
    
    KieStoreServices getStoreServices();
    
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
