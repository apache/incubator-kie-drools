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

package org.kie.scanner.embedder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsSource;
import org.junit.Test;
import org.kie.scanner.MavenRepositoryConfiguration;

import static org.drools.core.util.IoUtils.readFileAsString;
import static org.junit.Assert.*;
import static org.kie.scanner.embedder.MavenSettings.CUSTOM_SETTINGS_PROPERTY;

public class MavenEmbedderTest {

    private final String EMPTY_SETTINGS = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\n" +
                                          "      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                                          "      xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0\n" +
                                          "                          http://maven.apache.org/xsd/settings-1.0.0.xsd\"/>\n";

    @Test
    public void testExternalRepositories() {
        String oldSettingsXmlPath = System.getProperty( CUSTOM_SETTINGS_PROPERTY );
        try {
            if (oldSettingsXmlPath != null) {
                System.clearProperty( CUSTOM_SETTINGS_PROPERTY );
            }
            MavenSettings.reinitSettingsFromString(EMPTY_SETTINGS);

            final MavenRequest mavenRequest = createMavenRequest(null);
            final MavenEmbedder embedder = new MavenEmbedderWithRepoMock( mavenRequest );
            final MavenExecutionRequest request = embedder.getMavenExecutionRequest();

            assertNotNull( request );

            final List<ArtifactRepository> remoteRepositories = request.getRemoteRepositories();
            assertEquals( 2,
                          remoteRepositories.size() );
            for ( ArtifactRepository remoteRepository : remoteRepositories ) {
                assertTrue( remoteRepository.getId().equals( "central" )
                                    || remoteRepository.getId().equals( "kie-wb-m2-repo" ) );
            }

        } catch ( MavenEmbedderException mee ) {
            fail( mee.getMessage() );
        } finally {
            if (oldSettingsXmlPath != null) {
                System.setProperty( CUSTOM_SETTINGS_PROPERTY, oldSettingsXmlPath );
            }
            MavenSettings.reinitSettings();
        }
    }

    @Test
    public void testCustomSettingSource() {
        try {
            final MavenRequest mavenRequest = createMavenRequest(new SettingsSourceMock( "<settings/>" ) );
            final MavenEmbedder embedder = new MavenEmbedderWithRepoMock( mavenRequest );
            final MavenExecutionRequest request = embedder.getMavenExecutionRequest();

            assertNotNull( request );

            assertEquals( "<settings/>", readFileAsString( request.getUserSettingsFile() ).trim() );

        } catch ( MavenEmbedderException mee ) {
            fail( mee.getMessage() );
        }
    }

    @Test
    public void testProxies() {
        String oldSettingsXmlPath = System.getProperty( CUSTOM_SETTINGS_PROPERTY );
        try {
            if (oldSettingsXmlPath != null) {
                System.clearProperty( CUSTOM_SETTINGS_PROPERTY );
            }
            MavenSettings.reinitSettingsFromString(EMPTY_SETTINGS);

            final MavenRequest mavenRequest = createMavenRequest(null);
            final MavenEmbedder embedder = new MavenEmbedderWithProxyMock( mavenRequest );
            final MavenExecutionRequest request = embedder.getMavenExecutionRequest();

            assertNotNull( request );

            final List<Proxy> proxies = request.getProxies();
            assertEquals( 1, proxies.size() );
            assertEquals( "MyProxy", proxies.get(0).getId() );

        } catch ( MavenEmbedderException mee ) {
            fail( mee.getMessage() );
        } finally {
            if (oldSettingsXmlPath != null) {
                System.setProperty( CUSTOM_SETTINGS_PROPERTY, oldSettingsXmlPath );
            }
            MavenSettings.reinitSettings();
        }
    }

    public static abstract class MavenEmbedderMock extends MavenEmbedder {

        public MavenEmbedderMock( MavenRequest mavenRequest ) throws MavenEmbedderException {
            super( mavenRequest );
        }

        @Override
        protected MavenRepositoryConfiguration getMavenRepositoryConfiguration() {
            return new MavenRepositoryConfiguration(getMavenSettings());
        }

        private Settings getMavenSettings() {
            String path = getClass().getResource( "." ).toString().substring( "file:".length() );
            File testSettingsFile = new File( path + getSettingsFile() );
            assertTrue( testSettingsFile.exists() );

            SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();
            DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
            request.setUserSettingsFile( testSettingsFile );

            try {
                return settingsBuilder.build( request ).getEffectiveSettings();
            } catch ( SettingsBuildingException e ) {
                throw new RuntimeException( e );
            }
        }

        protected abstract String getSettingsFile();
    }

    public static class MavenEmbedderWithRepoMock extends MavenEmbedderMock {

        public MavenEmbedderWithRepoMock( MavenRequest mavenRequest ) throws MavenEmbedderException {
            super( mavenRequest );
        }

        @Override
        protected String getSettingsFile() {
            return "settings_with_repositories.xml";
        }
    }

    public static class MavenEmbedderWithProxyMock extends MavenEmbedderMock {

        public MavenEmbedderWithProxyMock( MavenRequest mavenRequest ) throws MavenEmbedderException {
            super( mavenRequest );
        }

        @Override
        protected String getSettingsFile() {
            return "settings_with_proxies.xml";
        }
    }

    private static MavenRequest createMavenRequest(SettingsSource settingsSource) {
        MavenRequest mavenRequest = new MavenRequest();
        mavenRequest.setLocalRepositoryPath( MavenSettings.getSettings().getLocalRepository() );
        mavenRequest.setUserSettingsSource(settingsSource != null ? settingsSource : MavenSettings.getUserSettingsSource());
        mavenRequest.setResolveDependencies( true );
        mavenRequest.setOffline( true );
        return mavenRequest;
    }

    public static class SettingsSourceMock implements SettingsSource {

        private final String settings;

        public SettingsSourceMock( String settings ) {
            this.settings = settings;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream( settings.getBytes( "UTF-8" ) );
        }

        @Override
        public String getLocation() {
            return "test";
        }
    }
}
