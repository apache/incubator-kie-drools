/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.scanner.embedder;

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.handler.manager.DefaultArtifactHandlerManager;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.repository.layout.FlatRepositoryLayout;
import org.apache.maven.bridge.MavenRepositorySystem;
import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.eventspy.internal.EventSpyDispatcher;
import org.apache.maven.execution.DefaultMavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.internal.aether.DefaultRepositorySystemSessionFactory;
import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.DefaultModelProcessor;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.model.composition.DefaultDependencyManagementImporter;
import org.apache.maven.model.composition.DependencyManagementImporter;
import org.apache.maven.model.inheritance.DefaultInheritanceAssembler;
import org.apache.maven.model.inheritance.InheritanceAssembler;
import org.apache.maven.model.interpolation.ModelInterpolator;
import org.apache.maven.model.interpolation.StringSearchModelInterpolator;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.management.DefaultDependencyManagementInjector;
import org.apache.maven.model.management.DefaultPluginManagementInjector;
import org.apache.maven.model.management.DependencyManagementInjector;
import org.apache.maven.model.management.PluginManagementInjector;
import org.apache.maven.model.normalization.DefaultModelNormalizer;
import org.apache.maven.model.normalization.ModelNormalizer;
import org.apache.maven.model.path.DefaultModelPathTranslator;
import org.apache.maven.model.path.DefaultModelUrlNormalizer;
import org.apache.maven.model.path.DefaultPathTranslator;
import org.apache.maven.model.path.DefaultUrlNormalizer;
import org.apache.maven.model.path.ModelPathTranslator;
import org.apache.maven.model.path.ModelUrlNormalizer;
import org.apache.maven.model.path.PathTranslator;
import org.apache.maven.model.path.UrlNormalizer;
import org.apache.maven.model.profile.DefaultProfileInjector;
import org.apache.maven.model.profile.DefaultProfileSelector;
import org.apache.maven.model.profile.ProfileInjector;
import org.apache.maven.model.profile.ProfileSelector;
import org.apache.maven.model.superpom.DefaultSuperPomProvider;
import org.apache.maven.model.superpom.SuperPomProvider;
import org.apache.maven.model.validation.DefaultModelValidator;
import org.apache.maven.model.validation.ModelValidator;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.plugin.internal.DefaultLegacySupport;
import org.apache.maven.project.DefaultProjectBuilder;
import org.apache.maven.project.DefaultProjectBuildingHelper;
import org.apache.maven.project.DefaultProjectDependenciesResolver;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingHelper;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.apache.maven.project.RepositorySessionDecorator;
import org.apache.maven.repository.DefaultMirrorSelector;
import org.apache.maven.repository.MirrorSelector;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.repository.legacy.LegacyRepositorySystem;
import org.apache.maven.repository.legacy.repository.ArtifactRepositoryFactory;
import org.apache.maven.repository.legacy.repository.DefaultArtifactRepositoryFactory;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.crypto.DefaultSettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.internal.impl.DefaultArtifactResolver;
import org.eclipse.aether.internal.impl.DefaultRemoteRepositoryManager;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.aether.internal.impl.DefaultTransporterProvider;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.transport.wagon.WagonTransporterFactory;
import org.sonatype.plexus.components.cipher.DefaultPlexusCipher;
import org.sonatype.plexus.components.cipher.PlexusCipher;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WiredComponentProvider implements ComponentProvider {

    private final DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

    public WiredComponentProvider() {
        initServiceLocator();
    }

    private void initServiceLocator() {
        locator.addService( TransporterFactory.class, FileTransporterFactory.class );
        locator.addService( TransporterFactory.class, HttpTransporterFactory.class );
        locator.addService( TransporterFactory.class, WagonTransporterFactory.class );

        locator.setServices( SettingsBuilder.class, new DefaultSettingsBuilderFactory().newInstance() );
        locator.addService( RepositorySystem.class, LegacyRepositorySystem.class );
        locator.addService( MavenRepositorySystem.class, MavenRepositorySystem.class );
        locator.addService (DefaultRepositorySystemSessionFactory.class, DefaultRepositorySystemSessionFactory.class);

        locator.addService( org.eclipse.aether.RepositorySystem.class, DefaultRepositorySystem.class );
        locator.addService( PlexusCipher.class, DefaultPlexusCipher.class );
        locator.addService( SecDispatcher.class, DefaultSecDispatcher.class );
        locator.addService( SettingsDecrypter.class, DefaultSettingsDecrypter.class );
        locator.addService( ArtifactRepositoryFactory.class, DefaultArtifactRepositoryFactory.class );
        locator.addService( MirrorSelector.class, DefaultMirrorSelector.class );
        locator.addService( Logger.class, ConsoleLogger.class );
        locator.addService( Maven.class, DefaultMaven.class );
        locator.addService( LegacySupport.class, DefaultLegacySupport.class );
        locator.addService( ProjectBuilder.class, DefaultProjectBuilder.class );
        locator.addService( ProjectBuildingHelper.class, DefaultProjectBuildingHelper.class );
        locator.addService( ProfileSelector.class, DefaultProfileSelector.class );
        locator.addService( ModelProcessor.class, DefaultModelProcessor.class );
        locator.addService( ModelReader.class, DefaultModelReader.class );
        locator.addService( ModelValidator.class, DefaultModelValidator.class );
        locator.addService( SuperPomProvider.class, DefaultSuperPomProvider.class );
        locator.addService( ModelNormalizer.class, DefaultModelNormalizer.class );
        locator.addService( ProfileInjector.class, DefaultProfileInjector.class );
        locator.addService( RemoteRepositoryManager.class, DefaultRemoteRepositoryManager.class );
        locator.addService( InheritanceAssembler.class, DefaultInheritanceAssembler.class );
        locator.addService( ModelInterpolator.class, StringSearchModelInterpolator.class );
        locator.addService( ModelUrlNormalizer.class, DefaultModelUrlNormalizer.class );
        locator.addService( UrlNormalizer.class, DefaultUrlNormalizer.class );
        locator.addService( ModelPathTranslator.class, DefaultModelPathTranslator.class );
        locator.addService( PluginManagementInjector.class, DefaultPluginManagementInjector.class );
        locator.addService( DependencyManagementInjector.class, DefaultDependencyManagementInjector.class );
        locator.addService( DependencyManagementImporter.class, DefaultDependencyManagementImporter.class );
        locator.addService( ArtifactFactory.class, DefaultArtifactFactory.class );
        locator.addService( ArtifactHandlerManager.class, DefaultArtifactHandlerManager.class );
        locator.addService( ProjectDependenciesResolver.class, DefaultProjectDependenciesResolver.class );
        locator.addService( PathTranslator.class, DefaultPathTranslator.class );
        locator.addService( TransporterProvider.class, DefaultTransporterProvider.class );
        locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
        locator.addService( ModelBuilder.class, DefaultModelBuilder.class );

        // DefaultMavenExecutionRequestPopulator does not have non-arg constructor so we need to create new instance
        // manually and inform the locator about the new instance
        MavenRepositorySystem system = locator.getService(MavenRepositorySystem.class);
        locator.setServices( MavenExecutionRequestPopulator.class, new DefaultMavenExecutionRequestPopulator(system) );

        Map<String, ArtifactRepositoryLayout> layouts = new HashMap<String, ArtifactRepositoryLayout>(  );
        layouts.put("default", new DefaultRepositoryLayout());
        layouts.put("flat", new FlatRepositoryLayout());
        inject( RepositorySystem.class, layouts, "layouts" );
        inject( RepositorySystem.class, ArtifactRepositoryFactory.class, "artifactRepositoryFactory" );
        inject( RepositorySystem.class, MirrorSelector.class, "mirrorSelector" );
        inject( RepositorySystem.class, ArtifactFactory.class, "artifactFactory" );

        inject( ArtifactRepositoryFactory.class, layouts, "repositoryLayouts" );
        inject( ArtifactHandlerManager.class, buildArtifactHandlers(), "artifactHandlers" );
        inject( ArtifactFactory.class, ArtifactHandlerManager.class, "artifactHandlerManager" );

        inject( SecDispatcher.class, PlexusCipher.class, "_cipher" );
        inject( SettingsDecrypter.class, SecDispatcher.class, "securityDispatcher" );

        EventSpyDispatcher eventSpyDispatcher = new EventSpyDispatcher();
        eventSpyDispatcher.setEventSpies( new ArrayList<EventSpy>() );
        inject( DefaultRepositorySystemSessionFactory.class, Logger.class, "logger" );
        inject( DefaultRepositorySystemSessionFactory.class, ArtifactHandlerManager.class, "artifactHandlerManager" );
        inject( DefaultRepositorySystemSessionFactory.class, org.eclipse.aether.RepositorySystem.class, "repoSystem" );
        inject( DefaultRepositorySystemSessionFactory.class, SettingsDecrypter.class, "settingsDecrypter" );
        inject( DefaultRepositorySystemSessionFactory.class, eventSpyDispatcher, "eventSpyDispatcher" );
        inject( DefaultRepositorySystemSessionFactory.class, MavenRepositorySystem.class, "mavenRepositorySystem" );

        inject( Maven.class, Logger.class, "logger" );
        inject( Maven.class, DefaultRepositorySystemSessionFactory.class, "repositorySessionFactory" );

        inject( ProjectBuilder.class, ProjectBuildingHelper.class, "projectBuildingHelper" );
        inject( ProjectBuildingHelper.class, RepositorySystem.class, "repositorySystem" );

        inject( MavenRepositorySystem.class, ArtifactHandlerManager.class, "artifactHandlerManager" );
        inject( MavenRepositorySystem.class, layouts, "layouts" );

        inject( ProjectBuilder.class, MavenRepositorySystem.class, "repositorySystem" );
        inject( ProjectBuilder.class, ProjectBuildingHelper.class, "projectBuildingHelper" );
        inject( ProjectBuilder.class, ModelBuilder.class, "modelBuilder" );
        inject( ProjectBuilder.class, org.eclipse.aether.RepositorySystem.class, "repoSystem" );
        inject( ProjectBuilder.class, RemoteRepositoryManager.class, "repositoryManager" );
        inject( ProjectBuilder.class, ProjectDependenciesResolver.class, "dependencyResolver" );

        inject( ProjectDependenciesResolver.class, Logger.class, "logger" );
        inject( ProjectDependenciesResolver.class, org.eclipse.aether.RepositorySystem.class, "repoSystem" );
        List<RepositorySessionDecorator> decorators = new ArrayList<RepositorySessionDecorator>();
        inject( ProjectDependenciesResolver.class, decorators, "decorators" );

        inject( SuperPomProvider.class, ModelProcessor.class, "modelProcessor" );
        inject( ModelUrlNormalizer.class, UrlNormalizer.class, "urlNormalizer" );
        inject( ModelProcessor.class, ModelReader.class, "reader" );
        inject( ModelInterpolator.class, UrlNormalizer.class, "urlNormalizer" );
        inject( ModelInterpolator.class, PathTranslator.class, "pathTranslator" );
        inject( ModelPathTranslator.class, PathTranslator.class, "pathTranslator" );

        inject( ModelBuilder.class, ProfileSelector.class, "profileSelector" );
        inject( ModelBuilder.class, ModelProcessor.class, "modelProcessor" );
        inject( ModelBuilder.class, ModelValidator.class, "modelValidator" );
        inject( ModelBuilder.class, SuperPomProvider.class, "superPomProvider" );
        inject( ModelBuilder.class, ModelNormalizer.class, "modelNormalizer" );
        inject( ModelBuilder.class, ProfileInjector.class, "profileInjector" );
        inject( ModelBuilder.class, InheritanceAssembler.class, "inheritanceAssembler" );
        inject( ModelBuilder.class, ModelInterpolator.class, "modelInterpolator" );
        inject( ModelBuilder.class, ModelUrlNormalizer.class, "modelUrlNormalizer" );
        inject( ModelBuilder.class, ModelPathTranslator.class, "modelPathTranslator" );
        inject( ModelBuilder.class, PluginManagementInjector.class, "pluginManagementInjector" );
        inject( ModelBuilder.class, DependencyManagementInjector.class, "dependencyManagementInjector" );
        inject( ModelBuilder.class, DependencyManagementImporter.class, "dependencyManagementImporter" );
    }

    private Map<String, ArtifactHandler> buildArtifactHandlers() {
        Map<String, ArtifactHandler> handlerMap = new HashMap<String, ArtifactHandler>();
        handlerMap.put("jar", buildArtifactHandler( "jar" ));
        return handlerMap;
    }

    private ArtifactHandler buildArtifactHandler(String type) {
        DefaultArtifactHandler artifactHandler = new DefaultArtifactHandler( type );
        artifactHandler.setLanguage( "java" );
        artifactHandler.setAddedToClasspath( true );
        return artifactHandler;
    }

    private void inject(Class<?> bean, Class<?> requirement, String fieldName) {
        inject(bean, locator.getService( requirement ), fieldName);
    }

    private void inject(Class<?> bean, Object requirement, String fieldName) {
        inject( locator.getService( bean ), requirement, fieldName );
    }

    private void inject( Object instance, Class<?> requirement, String fieldName ) {
        inject(instance, locator.getService( requirement ), fieldName);
    }

    private void inject( Object instance, Object requirement, String fieldName ) {
        inject( instance.getClass(), instance, requirement, fieldName );
    }

    private void inject( Class<?> clazz, Object instance, Object requirement, String fieldName ) {
        try {
            Field field = clazz.getDeclaredField( fieldName );
            field.setAccessible( true );
            field.set( instance, requirement );
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                inject( superClass, instance, requirement, fieldName );
            } else {
                throw new RuntimeException( e );
            }
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public <T> T lookup( Class<T> clazz ) throws ComponentLookupException {
        return locator.getService( clazz );
    }

    @Override
    public RepositorySystemSession getRepositorySystemSession( MavenExecutionRequest mavenExecutionRequest ) throws ComponentLookupException {
        DefaultMaven defaultMaven = (DefaultMaven) lookup( Maven.class );
        RepositorySystemSession session = defaultMaven.newRepositorySession( mavenExecutionRequest );
        inject(session.getArtifactTypeRegistry(), ArtifactHandlerManager.class, "handlerManager" );
        return session;
    }

    @Override
    public PlexusContainer getPlexusContainer() {
        return null;
    }

    @Override
    public ClassLoader getSystemClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
