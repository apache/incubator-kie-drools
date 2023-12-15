/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.kie.builder.impl;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.drools.compiler.kie.builder.impl.event.KieServicesEventListerner;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.BaseConfigurationFactories;
import org.drools.core.CompositeSessionConfiguration;
import org.drools.core.SessionConfigurationFactories;
import org.drools.core.concurrent.ExecutorProviderImpl;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.io.ResourceFactoryServiceImpl;
import org.drools.kiesession.audit.KnowledgeRuntimeLoggerProviderImpl;
import org.drools.wiring.api.classloader.ProjectClassLoader;
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
import org.kie.api.internal.utils.KieService;
import org.kie.api.io.KieResources;
import org.kie.api.logger.KieLoggers;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.conf.CompositeBaseConfiguration;
import org.kie.internal.utils.ChainedProperties;
import org.kie.util.maven.support.ReleaseIdImpl;

import static org.drools.compiler.compiler.io.memory.MemoryFileSystem.readFromJar;
import static org.drools.util.ClassUtils.findParentClassLoader;

public class KieServicesImpl implements InternalKieServices {

    private volatile KieContainer classpathKContainer;
    private volatile String classpathKContainerId;
    
    private volatile ClassLoader classpathClassLoader;

    private final Object lock = new Object();

    private WeakReference<KieServicesEventListerner> listener;
    
    private final ConcurrentMap<String, KieContainer> kContainers = new ConcurrentHashMap<>();

    public KieRepository getRepository() {
        return KieRepositoryImpl.INSTANCE;
    }

    /**
     * Returns KieContainer for the classpath
     */
    public KieContainer getKieClasspathContainer() {
        return getKieClasspathContainer( null, findParentClassLoader(getClass()) );
    }
    
    public KieContainer getKieClasspathContainer(ClassLoader classLoader) {
        return getKieClasspathContainer( null, classLoader );
    }
    
    public KieContainer getKieClasspathContainer(String containerId) {
        return getKieClasspathContainer( containerId, findParentClassLoader(getClass()) );
    }

    public KieContainer getKieClasspathContainer(String containerId, ClassLoader classLoader) {
        if ( classpathKContainer == null ) {
            // these are heavy to create, don't want to end up with two
            synchronized ( lock ) {
                if ( classpathKContainer == null ) {
                    classpathClassLoader = classLoader;
                    if (containerId == null) {
                        classpathKContainerId = UUID.randomUUID().toString();
                    } else {
                        classpathKContainerId = containerId;
                    }
                    classpathKContainer = newKieClasspathContainer(classpathKContainerId, classLoader);
                } else if (classLoader != classpathClassLoader) {
                    throw new IllegalStateException("There's already another KieContainer created from a different ClassLoader");
                }
            }
        } else if (classLoader != classpathClassLoader) {
            throw new IllegalStateException("There's already another KieContainer created from a different ClassLoader");
        }

        if (containerId != null && !classpathKContainerId.equals(containerId)) {
            throw new IllegalStateException("The default global singleton KieClasspathContainer was already created with id "+classpathKContainerId);
        }
        
        return classpathKContainer;
    }

    public KieContainer newKieClasspathContainer() {
        return newKieClasspathContainer( null, findParentClassLoader(getClass()) );
    }
    
    public KieContainer newKieClasspathContainer(ClassLoader classLoader) {
        return newKieClasspathContainer( null, classLoader );
    }
    
    public KieContainer newKieClasspathContainer(String containerId) {
        return newKieClasspathContainer( containerId, findParentClassLoader(getClass()) );
    }

    public KieContainer newKieClasspathContainer(String containerId, ClassLoader classLoader) {
        return newKieClasspathContainer(containerId, classLoader, null);
    }

    @Override
    public KieContainer newKieClasspathContainer(String containerId, ClassLoader classLoader, ReleaseId releaseId) {
        if (containerId == null) {
            return new KieContainerImpl(UUID.randomUUID().toString(), new ClasspathKieProject(classLoader, listener, releaseId), null);
        }
        if ( kContainers.get(containerId) == null ) {
            KieContainerImpl newContainer = new KieContainerImpl(containerId, new ClasspathKieProject(classLoader, listener, releaseId), null, releaseId);
            KieContainer check = kContainers.putIfAbsent(containerId, newContainer);
            if (check == null) {
                return newContainer;
            } else {
                newContainer.dispose();
                throw new IllegalStateException("There's already another KieContainer created with the id "+containerId);
            }
        } else {
            throw new IllegalStateException("There's already another KieContainer created with the id "+containerId);
        }
    }

    public void nullKieClasspathContainer() {
        // used for testing only
        synchronized ( lock ) {
            classpathKContainer = null;
            classpathKContainerId = null;
            classpathClassLoader = null;
        }  
    }
    
    /**
     * Voids the internal map of containerId (s) used for handling reference and unique checks. This method is intended for use in unit test only.
     */
    public void nullAllContainerIds() {
        synchronized ( lock ) {
            kContainers.clear();
        }
    }
    
    @Override
    public void clearRefToContainerId(String containerId, KieContainer containerRef) {
        kContainers.remove(containerId, containerRef);
    }

    public KieContainer newKieContainer(ReleaseId releaseId) {
        return newKieContainer(null, releaseId, null);
    }
    
    public KieContainer newKieContainer(String containerId, ReleaseId releaseId) {
        return newKieContainer(containerId, releaseId, null);
    }
    
    public KieContainer newKieContainer(ReleaseId releaseId, ClassLoader classLoader) {
        return newKieContainer(null, releaseId, classLoader);
    }

    public KieContainer newKieContainer(String containerId, ReleaseId releaseId, ClassLoader classLoader) {
        InternalKieModule kieModule = (InternalKieModule) getRepository().getKieModule(releaseId);
        if (kieModule == null) {
            throw new RuntimeException("Cannot find KieModule: " + releaseId);
        }
        if (classLoader == null) {
            classLoader = kieModule.getModuleClassLoader();
        }
        KieProject kProject = new KieModuleKieProject( kieModule, classLoader );
        if (classLoader != kProject.getClassLoader()) {
            // if the new kproject has a different classloader than the original one it has to be initialized
            kProject.init();
        }

        if (containerId == null) {
            return new KieContainerImpl( UUID.randomUUID().toString(), kProject, getRepository(), releaseId );
        }

        if ( kContainers.get(containerId) == null ) {
            KieContainerImpl newContainer = new KieContainerImpl( containerId, kProject, getRepository(), releaseId );
            KieContainer check = kContainers.putIfAbsent(containerId, newContainer);
            if (check == null) {
                return newContainer;
            } else {
                newContainer.dispose();
                throw new IllegalStateException("There's already another KieContainer created with the id "+containerId);
            }
        } else {
            throw new IllegalStateException("There's already another KieContainer created with the id "+containerId);
        }
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
        KieScannerFactoryService scannerFactoryService = KieService.load(KieScannerFactoryService.class);
        if (scannerFactoryService == null) {
            throw new RuntimeException( "Cannot instance a maven based KieScanner, is kie-ci on the classpath?" );
        }
        InternalKieScanner scanner = (InternalKieScanner)scannerFactoryService.newKieScanner();
        scanner.setKieContainer(kieContainer);
        return scanner;
    }

    public KieScanner newKieScanner(KieContainer kieContainer, String repositoryFolder) {
        return new KieFileSystemScannerImpl( kieContainer, repositoryFolder );
    }

    public KieResources getResources() {
        // instantiating directly, but we might want to use the service registry instead
        return new ResourceFactoryServiceImpl();
    }

    public KieCommands getCommands() {
        return KieCommandsHolder.KIE_COMMANDS;
    }

    private static class KieCommandsHolder {
        private static final KieCommands KIE_COMMANDS = KieService.load( KieCommands.class );
    }

    public KieMarshallers getMarshallers() {
        KieMarshallers kieMarshallers = KieService.load( KieMarshallers.class );
        if (kieMarshallers == null) {
            throw new RuntimeException("Marshaller not available, please add the module org.drools:drools-serialization-protobuf to your classpath.");
        }
        return kieMarshallers;
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
        return KieService.load( KieStoreServices.class );
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
        return newKieBaseConfiguration(null, null);
    }

    public KieBaseConfiguration newKieBaseConfiguration(Properties properties) {
        return newKieBaseConfiguration(properties, null);
    }

    public KieBaseConfiguration newKieBaseConfiguration(Properties properties, ClassLoader classLoader) {
        ClassLoader projClassLoader = getClassLoader(classLoader);

        ChainedProperties chained = ChainedProperties.getChainedProperties(projClassLoader);

        if ( properties != null ) {
            chained.addProperties( properties );
        }
        return new CompositeBaseConfiguration(chained, projClassLoader,
                                              BaseConfigurationFactories.baseConf, BaseConfigurationFactories.ruleConf, BaseConfigurationFactories.flowConf);
    }

    public KieSessionConfiguration newKieSessionConfiguration() {
        return newKieSessionConfiguration(null, null);
    }

    public KieSessionConfiguration newKieSessionConfiguration(Properties properties) {
        return newKieSessionConfiguration(properties, null);
    }

    public KieSessionConfiguration newKieSessionConfiguration(Properties properties, ClassLoader classLoader) {
        ClassLoader projClassLoader = getClassLoader(classLoader);

        ChainedProperties chained = ChainedProperties.getChainedProperties(projClassLoader);

        if ( properties != null ) {
            chained.addProperties( properties );
        }
        return new CompositeSessionConfiguration(chained, projClassLoader,
                                                 SessionConfigurationFactories.baseConf, SessionConfigurationFactories.ruleConf, SessionConfigurationFactories.flowConf);
    }

    private ClassLoader getClassLoader(ClassLoader classLoader) {
        return classLoader instanceof ProjectClassLoader ? classLoader : ProjectClassLoader.getClassLoader(classLoader, getClass());
    }

    public Environment newEnvironment() {
        return EnvironmentFactory.newEnvironment();
    }

    @Override
    public void registerListener(KieServicesEventListerner listener) {
        this.listener = new WeakReference<>(listener);
    }
}

