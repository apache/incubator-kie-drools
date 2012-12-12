package org.kie;

import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieRepository;
import org.kie.builder.KieScanner;
import org.kie.command.KieCommands;
import org.kie.concurrent.KieExecutors;
import org.kie.io.KieResources;
import org.kie.logger.KieLoggers;
import org.kie.marshalling.KieMarshallers;
import org.kie.persistence.jpa.KieStoreServices;
import org.kie.runtime.KieContainer;

import java.io.File;

public interface KieServices {
    
    KieResources getResources();

    KieRepository getRepository();
    
    KieCommands getCommands();
    
    KieMarshallers getMarshallers();
    
    KieLoggers getLoggers();
    
    KieExecutors getExecutors();
    
    KieStoreServices getStoreServices();
    
    /**
     * Returns KieContainer for the classpath, this a global singleton
     */
    KieContainer getKieClasspathContainer();
    
    KieContainer newKieContainer(GAV gav);
    
    KieScanner newKieScanner(KieContainer kieContainer);    
    
    KieBuilder newKieBuilder(File rootFolder);
    
    KieBuilder newKieBuilder(KieFileSystem kieFileSystem);

    GAV newGav(String groupId, String artifactId, String version);

    KieFileSystem newKieFileSystem( );

    KieModuleModel newKieModuleModel();

    public static class Factory {
        private static KieServices INSTANCE;

        static {
            try {                
                INSTANCE = ( KieServices ) Class.forName( KieServices.class.getName() + "impl" ).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to instance KieServices", e);
            }
        }

        public static KieServices get() {
            return INSTANCE;
        }
    }
}
