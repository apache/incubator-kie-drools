package org.kie.builder.impl;

import static org.drools.compiler.io.memory.MemoryFileSystem.readFromJar;

import java.io.File;

import org.drools.audit.KnowledgeRuntimeLoggerProviderImpl;
import org.drools.command.impl.CommandFactoryServiceImpl;
import org.drools.concurrent.ExecutorProviderImpl;
import org.drools.io.impl.ResourceFactoryServiceImpl;
import org.drools.marshalling.impl.MarshallerProviderImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieRepository;
import org.kie.builder.KieScanner;
import org.kie.builder.KieServices;
import org.kie.command.KieCommands;
import org.kie.concurrent.KieExecutors;
import org.kie.io.KieResources;
import org.kie.io.ResourceFactoryService;
import org.kie.logger.KieLoggers;
import org.kie.marshalling.KieMarshallers;
import org.kie.persistence.jpa.KieStoreServices;
import org.kie.util.ServiceRegistryImpl;

public class KieServicesImpl implements KieServices {
    private ResourceFactoryService resourceFactory;
    
    public ResourceFactoryService getResourceFactory() {
        if ( resourceFactory == null ) {
            this.resourceFactory = new ResourceFactoryServiceImpl();
        }
        return resourceFactory;
    }

    public KieRepository getKieRepository() {
        return KieRepositoryImpl.INSTANCE;
    }

    /**
     * Returns KieContainer for the classpath
     */
    public KieContainer getKieContainer() {
        return new KieContainerImpl( new ClasspathKieProject( getKieRepository() ), getKieRepository() ); 
    }
    
    public KieContainer getKieContainer(GAV gav) {
        return new KieContainerImpl( (KieProject ) getKieRepository().getKieModule(gav), getKieRepository() );
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
    
}

