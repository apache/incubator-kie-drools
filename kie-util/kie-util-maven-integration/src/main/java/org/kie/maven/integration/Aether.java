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
package org.kie.maven.integration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.http.HttpWagon;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.ConfigurationProperties;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.transport.wagon.WagonProvider;
import org.eclipse.aether.transport.wagon.WagonTransporterFactory;
import org.eclipse.aether.util.repository.DefaultProxySelector;
import org.kie.maven.integration.embedder.MavenProjectLoader;
import org.kie.maven.integration.embedder.MavenSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.maven.integration.embedder.MavenProjectLoader.loadMavenProject;

public class Aether {
    public static final String S3_WAGON_CLASS = "kie.maven.s3.wagon.class";

    private static final Logger log = LoggerFactory.getLogger( Aether.class );

    private String localRepoDir;
    private final boolean offline;

    public static Aether instance;

    private final RepositorySystem system;
    private RepositorySystemSession session;
    private final Collection<RemoteRepository> repositories;

    private RemoteRepository localRepository;

    public Aether( MavenProject mavenProject ) {
        this( MavenSettings.getSettings(), mavenProject );
    }

    public static synchronized Aether getAether() {
        if ( instance == null ) {
            Settings settings = MavenSettings.getSettings();
            instance = new Aether( settings, loadMavenProject( settings.isOffline() ) );
        }
        return instance;
    }

    private Aether( Settings settings, MavenProject mavenProject ) {
        this.localRepoDir = settings.getLocalRepository();
        this.offline = settings.isOffline();
        system = newRepositorySystem();
        session = newRepositorySystemSession( settings, system );
        repositories = initRepositories( mavenProject );
    }

    private Collection<RemoteRepository> initRepositories( MavenProject mavenProject ) {
        Collection<RemoteRepository> reps = new HashSet<>();
        if (!isForcedOffline()){
            reps.add( newCentralRepository() );
            if ( mavenProject != null ) {
                reps.addAll( mavenProject.getRemoteProjectRepositories() );
            }
        }

        RemoteRepository localRepo = newLocalRepository();
        if ( localRepo != null ) {
            localRepository = localRepo;
        }
        return reps;
    }

    boolean isForcedOffline() {
        return MavenProjectLoader.isOffline();
    }

    private RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
        locator.addService( TransporterFactory.class, FileTransporterFactory.class );
        locator.addService( TransporterFactory.class, HttpTransporterFactory.class );
        locator.addService( TransporterFactory.class, WagonTransporterFactory.class );
        locator.setServices( WagonProvider.class, new ManualWagonProvider() );

        return locator.getService( RepositorySystem.class );
    }

    private RepositorySystemSession newRepositorySystemSession( Settings settings, RepositorySystem system ) {
        LocalRepository localRepo = new LocalRepository( localRepoDir );
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );
        session.setOffline( offline );
        configureProxiesOnSession( settings, session );
        configureHttpHeadersOnSession( settings, session );
        return session;
    }

    private void configureProxiesOnSession( Settings settings, DefaultRepositorySystemSession session ) {
        List<org.apache.maven.settings.Proxy> proxies = settings.getProxies();
        if (proxies == null || proxies.isEmpty()) {
            return;
        }

        DefaultProxySelector proxySelector = new DefaultProxySelector();
        for (org.apache.maven.settings.Proxy proxy : proxies) {
            proxySelector.add( new Proxy( proxy.getProtocol(), proxy.getHost(), proxy.getPort()), proxy.getNonProxyHosts() );
        }
        session.setProxySelector( proxySelector );
    }

    private void configureHttpHeadersOnSession( Settings settings, DefaultRepositorySystemSession session ) {
        List<Server> servers = settings.getServers();
        if (servers == null || servers.isEmpty()) {
            return;
        }

        for (Server server : servers) {
            if (server.getConfiguration() instanceof Xpp3Dom ) {
                Xpp3Dom configDom = (Xpp3Dom) server.getConfiguration();
                if (configDom != null) {
                    Xpp3Dom headersConfiguration = configDom.getChild("httpHeaders");
                    if (headersConfiguration != null) {
                        Xpp3Dom[] properties = headersConfiguration.getChildren();
                        if (properties != null && properties.length > 0) {
                            HashMap<String, String> httpHeaders = new HashMap<>();
                            for (Xpp3Dom property : properties) {
                                httpHeaders.put(property.getChild("name").getValue(), property.getChild("value").getValue());
                            }
                            session.setConfigProperty( ConfigurationProperties.HTTP_HEADERS + "." + server.getId(), httpHeaders );
                        }
                    }
                }
            }
        }
    }

    RemoteRepository newCentralRepository() {
        return new RemoteRepository.Builder( "central", "default", "https://repo1.maven.org/maven2/" ).build();
    }

    private RemoteRepository newLocalRepository() {
        File m2RepoDir = new File( localRepoDir );
        try {
            if ( !m2RepoDir.exists() ) {
                log.info( "The local repository directory " + localRepoDir + " doesn't exist. Creating it." );
                m2RepoDir.mkdirs();
            }
            String localRepositoryUrl = m2RepoDir.toURI().toURL().toExternalForm();
            return new RemoteRepository.Builder( "local", "default", localRepositoryUrl ).build();
        } catch ( Exception e ) {
            try {
                log.warn( "Cannot use directory " + localRepoDir + " as local repository.", e );
                localRepoDir = IoUtils.getTmpDirectory().getAbsolutePath();
                log.warn( "Using the temporary directory " + localRepoDir + " as local repository" );
                m2RepoDir = new File( localRepoDir );
                String localRepositoryUrl = m2RepoDir.toURI().toURL().toExternalForm();
                return new RemoteRepository.Builder( "local", "default", localRepositoryUrl ).build();
            } catch ( Exception e1 ) {
                log.warn( "Cannot create a local repository in " + localRepoDir, e1 );
            }
        }
        return null;
    }

    public RepositorySystem getSystem() {
        return system;
    }

    public RepositorySystemSession getSession() {
        return session;
    }

    public Collection<RemoteRepository> getRepositories() {
        return repositories;
    }

    public RemoteRepository getLocalRepository() {
        return localRepository;
    }

    private static class ManualWagonProvider implements WagonProvider {

        public Wagon lookup( String roleHint ) throws Exception {
            if ( "http".equals( roleHint ) || "https".equals( roleHint ) ) {
                return new HttpWagon();
            }
            if ( "sramp".equals( roleHint ) ) {
                try {
                    return (Wagon) Class.forName( "org.overlord.dtgov.jbpm.util.SrampWagonProxy" ).newInstance();
                } catch ( ClassNotFoundException cnfe ) {
                    log.warn( "Cannot find sramp wagon implementation class", cnfe );
                }
            }
            final String s3WagonClassName = System.getProperty( S3_WAGON_CLASS );
            if ( "s3".equals( roleHint ) && s3WagonClassName != null && s3WagonClassName.trim().length() > 0 ) {
                try {
                    return (Wagon) Class.forName( s3WagonClassName ).newInstance();
                } catch ( ClassNotFoundException cnfe ) {
                    log.warn( "Cannot find s3 wagon implementation class", cnfe );
                }
            }
            return null;
        }

        public void release( Wagon wagon ) {
        }
    }
}
