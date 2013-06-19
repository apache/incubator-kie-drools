package org.kie.api;

import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.KieCommands;
import org.kie.api.io.KieResources;
import org.kie.api.logger.KieLoggers;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;

import java.io.File;
import java.util.Properties;

/**
 * <p>
 * The KieServices is a thread-safe singleton acting as a hub giving access to the other
 * Services provided by Kie. As general rule a getX() method just returns a reference to another
 * singleton while a newX() one creates a new instance.
 * </p>
 * <p>
 * It is possible to obtain a KieServices reference via its Factory as it follows
 * </p>
 * <pre>
 * KieServices kieServices = KieServices.Factory.get();
 * </pre>
 */
public interface KieServices {

    /**
     * Returns the KieResources, a factory that provides Resource implementations for the desired IO resource
     */
    KieResources getResources();

    /**
     * Returns the KieRepository, a singleton acting as a repository for all the available KieModules
     */
    KieRepository getRepository();

    /**
     * Returns the KieCommands, a factory for Commands
     */
    KieCommands getCommands();

    /**
     * Returns the KieMarshallers service
     */
    KieMarshallers getMarshallers();

    /**
     * Returns KieLoggers, a factory for KieRuntimeLogger
     */
    KieLoggers getLoggers();
    
    /**
     * Returns KieStoreServices
     */
    KieStoreServices getStoreServices();
    
    /**
     * Returns KieContainer for the classpath, this a global singleton
     */
    KieContainer getKieClasspathContainer();

    /**
     * Creates a new KieContainer wrapping the KieModule with the given ReleaseId
     */
    KieContainer newKieContainer(ReleaseId releaseId);

    /**
     * Creates a KieScanner to automatically discover if there are new releases of the KieModule
     * (and its dependencies) wrapped by the given KieContainer
     */
    KieScanner newKieScanner(KieContainer kieContainer);

    /**
     * Creates a new KieBuilder to build the KieModule contained in the given folder
     */
    KieBuilder newKieBuilder(File rootFolder);

    /**
     * Creates a new KieBuilder to build the KieModule contained in the given KieFileSystem
     */
    KieBuilder newKieBuilder(KieFileSystem kieFileSystem);

    /**
     * Creates a new ReleaseId with the given groupId, artifactId and version
     */
    ReleaseId newReleaseId(String groupId, String artifactId, String version);

    /**
     * Creates a new KieFileSystem used to programmatically define the resources composing a KieModule
     */
    KieFileSystem newKieFileSystem( );

    /**
     * Creates a new KieModuleModel to programmatically define a KieModule
     */
    KieModuleModel newKieModuleModel();

    /**
     * Create a KieBaseConfiguration on which properties can be set.
     */
    KieBaseConfiguration newKnowledgeBaseConfiguration();

    /**
     * Create a KieBaseConfiguration on which properties can be set. Use
     * the given properties file and ClassLoader - either of which can be null.
     */
    KieBaseConfiguration newKnowledgeBaseConfiguration(Properties properties, ClassLoader classLoader);

    /**
     * Create a KieSessionConfiguration on which properties can be set.
     */
    KieSessionConfiguration newKnowledgeSessionConfiguration();

    /**
     * Create a KieSessionConfiguration on which properties can be set.
     */
    KieSessionConfiguration newKnowledgeSessionConfiguration(Properties properties);

    /**
     * A Factory for this KieServices
     */
    public static class Factory {
        private static KieServices INSTANCE;

        static {
            try {                
                INSTANCE = ( KieServices ) Class.forName( "org.drools.compiler.kie.builder.impl.KieServicesImpl" ).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to instance KieServices", e);
            }
        }

        /**
         * Returns a reference to the KieServices singleton
         */
        public static KieServices get() {
            return INSTANCE;
        }
    }
}
