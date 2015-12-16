/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.kie.builder.impl.event.KieServicesEventListerner;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.audit.KnowledgeRuntimeLoggerProviderImpl;
import org.drools.core.command.impl.CommandFactoryServiceImpl;
import org.drools.core.concurrent.ExecutorProviderImpl;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.io.impl.ResourceFactoryServiceImpl;
import org.drools.core.marshalling.impl.MarshallerProviderImpl;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.KieScannerFactoryService;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.command.KieCommands;
import org.kie.api.concurrent.KieExecutors;
import org.kie.api.io.KieResources;
import org.kie.api.logger.KieLoggers;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.utils.ServiceRegistryImpl;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Properties;

import static org.drools.compiler.compiler.io.memory.MemoryFileSystem.readFromJar;
import static org.drools.core.common.ProjectClassLoader.findParentClassLoader;

public class KieServicesImpl implements InternalKieServices {
    private volatile KieContainer classpathKContainer;

    private volatile ClassLoader classpathClassLoader;

    private final Object lock = new Object();

    private WeakReference<KieServicesEventListerner> listener;

    public KieRepository getRepository() {
        return KieRepositoryImpl.INSTANCE;
    }

    /**
     * Returns KieContainer for the classpath
     */
    public KieContainer getKieClasspathContainer() {
        return getKieClasspathContainer( findParentClassLoader() );
    }

    public KieContainer getKieClasspathContainer(ClassLoader classLoader) {
        if ( classpathKContainer == null ) {
            // these are heavy to create, don't want to end up with two
            synchronized ( lock ) {
                if ( classpathKContainer == null ) {
                    classpathClassLoader = classLoader;
                    classpathKContainer = newKieClasspathContainer(classLoader);
                } else if (classLoader != classpathClassLoader) {
                    throw new IllegalStateException("There's already another KieContainer created from a different ClassLoader");
                }
            }
        } else if (classLoader != classpathClassLoader) {
            throw new IllegalStateException("There's already another KieContainer created from a different ClassLoader");
        }

        return classpathKContainer;
    }

    public KieContainer newKieClasspathContainer() {
        return newKieClasspathContainer( findParentClassLoader() );
    }

    public KieContainer newKieClasspathContainer(ClassLoader classLoader) {
        return new KieContainerImpl(new ClasspathKieProject(classLoader, listener), null);
    }

    public void nullKieClasspathContainer() {
        // used for testing only
        synchronized ( lock ) {
            classpathKContainer = null;
            classpathClassLoader = null;
        }  
    }
    
    public KieContainer newKieContainer(ReleaseId releaseId) {
        return newKieContainer(releaseId, null);
    }

    public KieContainer newKieContainer(ReleaseId releaseId, ClassLoader classLoader) {
        InternalKieModule kieModule = (InternalKieModule) getRepository().getKieModule(releaseId);
        if (kieModule == null) {
            throw new RuntimeException("Cannot find KieModule: " + releaseId);
        }
        KieProject kProject = new KieModuleKieProject( kieModule, classLoader );
        return new KieContainerImpl( kProject, getRepository(), releaseId );
    }
    

    public KieBuilder newKieBuilder(File file) {
        return file.isDirectory() ? new KieBuilderImpl(file) : newKieBuilder(new KieFileSystemImpl(readFromJar(file)));
    }
    
    public KieBuilder newKieBuilder(KieFileSystem kieFileSystem) {
        return new KieBuilderImpl(kieFileSystem);
    }    

    public KieBuilder newKieBuilder(KieFileSystem kieFileSystem, ClassLoader classLoader) {
        return new KieBuilderImpl(kieFileSystem, classLoader);
    }

    public KieScanner newKieScanner(KieContainer kieContainer) {
        KieScannerFactoryService scannerFactoryService = ServiceRegistryImpl.getInstance().get( KieScannerFactoryService.class );
        InternalKieScanner scanner = (InternalKieScanner)scannerFactoryService.newKieScanner();
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

    public KieBaseConfiguration newKieBaseConfiguration() {
        return new RuleBaseConfiguration();
    }

    public KieBaseConfiguration newKieBaseConfiguration(Properties properties) {
        return new RuleBaseConfiguration(properties, null);
    }

    public KieBaseConfiguration newKieBaseConfiguration(Properties properties, ClassLoader classLoader) {
        return new RuleBaseConfiguration(properties, classLoader);
    }

    public KieSessionConfiguration newKieSessionConfiguration() {
        return SessionConfiguration.newInstance();
    }

    public KieSessionConfiguration newKieSessionConfiguration(Properties properties) {
        return new SessionConfigurationImpl(properties);
    }

    public KieSessionConfiguration newKieSessionConfiguration(Properties properties, ClassLoader classLoader) {
        return new SessionConfigurationImpl(properties, classLoader);
    }

    public Environment newEnvironment() {
        return EnvironmentFactory.newEnvironment();
    }

    @Override
    public void registerListener(KieServicesEventListerner listener) {
        this.listener = new WeakReference<KieServicesEventListerner>(listener);
    }
}

