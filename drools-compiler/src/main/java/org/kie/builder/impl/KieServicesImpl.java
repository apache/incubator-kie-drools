package org.kie.builder.impl;

import org.drools.audit.KnowledgeRuntimeLoggerProviderImpl;
import org.drools.command.impl.CommandFactoryServiceImpl;
import org.drools.concurrent.ExecutorProviderImpl;
import org.drools.io.impl.ResourceFactoryServiceImpl;
import org.drools.kproject.ReleaseIdImpl;
import org.drools.kproject.models.KieModuleModelImpl;
import org.drools.marshalling.impl.MarshallerProviderImpl;
import org.kie.KieServices;
import org.kie.builder.ReleaseId;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieRepository;
import org.kie.builder.KieScanner;
import org.kie.command.KieCommands;
import org.kie.concurrent.KieExecutors;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.kie.io.KieResources;
import org.kie.io.ResourceFactoryService;
import org.kie.logger.KieLoggers;
import org.kie.marshalling.KieMarshallers;
import org.kie.persistence.jpa.KieStoreServices;
import org.kie.runtime.KieContainer;

import java.io.File;

import static org.drools.compiler.io.memory.MemoryFileSystem.readFromJar;

public class KieServicesImpl implements KieServices {
    private ResourceFactoryService resourceFactory;
    
    private volatile KieContainerImpl classpathKContainer;
    
    private final Object lock = new Object();
    
    public ResourceFactoryService getResourceFactory() {
        if ( resourceFactory == null ) {
            this.resourceFactory = new ResourceFactoryServiceImpl();
        }
        return resourceFactory;
    }

    public KieRepository getRepository() {
        return KieRepositoryImpl.INSTANCE;
    }

    /**
     * Returns KieContainer for the classpath
     */
    public KieContainer getKieClasspathContainer() {
        if ( classpathKContainer == null ) {
            // these are heavy to create, don't want to end up with two
            synchronized ( lock ) {
                if ( classpathKContainer == null ) {
                    classpathKContainer =  new KieContainerImpl(new ClasspathKieProject(), null);
                }
            }        
        }

        return classpathKContainer;
    }
    
    public void nullKieClasspathContainer() {
        // used for testing only
        synchronized ( lock ) {
            classpathKContainer = null;
        }  
    }
    
    public KieContainer newKieContainer(ReleaseId releaseId) {
        InternalKieModule kieModule = (InternalKieModule) getRepository().getKieModule(releaseId);
        if (kieModule == null) {
            throw new RuntimeException("Cannot find KieModule: " + releaseId);
        }
        KieProject kProject = new KieModuleKieProject( kieModule, getRepository() );
        return new KieContainerImpl( kProject, getRepository() );
    }
    

    public KieBuilder newKieBuilder(File file) {
        return file.isDirectory() ? new KieBuilderImpl(file) : newKieBuilder(new KieFileSystemImpl(readFromJar(file)));
    }
    
    public KieBuilder newKieBuilder(KieFileSystem kieFileSystem) {
        return new KieBuilderImpl(kieFileSystem);
    }    

    public KieScanner newKieScanner(KieContainer kieContainer) {
        InternalKieScanner scanner = (InternalKieScanner)ServiceRegistryImpl.getInstance().get( KieScanner.class );
        scanner.setKieContainer(kieContainer);
        return scanner;
    }

    public KieResources getResources() {
        // instantiating directly, but we might want to use the service registry instead
        return new ResourceFactoryServiceImpl();
    }

    public KieCommands getCommands() {
        // instantiating directly, but we might want to use the service registry instead
        return new CommandFactoryServiceImpl();
    }

    public KieMarshallers getMarshallers() {
        // instantiating directly, but we might want to use the service registry instead
        return new MarshallerProviderImpl();
    }

    public KieLoggers getLoggers() {
        // instantiating directly, but we might want to use the service registry instead
        return new KnowledgeRuntimeLoggerProviderImpl();
    }

    public KieExecutors getExecutors() {
        // instantiating directly, but we might want to use the service registry instead
        return new ExecutorProviderImpl();
    }
    
    public KieStoreServices getStoreServices() {
        return ServiceRegistryImpl.getInstance().get( KieStoreServices.class );
    }

    public ReleaseId newReleaseId(String groupId, String artifactId, String version) {
        return new ReleaseIdImpl(groupId, artifactId, version);
    }

    public KieModuleModel newKieModuleModel() {
        return new KieModuleModelImpl();
    }

    public KieFileSystem newKieFileSystem() {
        return new KieFileSystemImpl();
    }
}

