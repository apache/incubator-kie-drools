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

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsSource;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Test;
import org.kie.scanner.MavenRepositoryConfiguration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.drools.core.util.IoUtils.readFileAsString;
import static org.junit.Assert.*;
import static org.kie.scanner.embedder.MavenSettings.CUSTOM_SETTINGS_PROPERTY;

public class MavenEmbedderTest {

    @Test
    public void testExternalRepositories() {
        String oldSettingsXmlPath = System.getProperty( CUSTOM_SETTINGS_PROPERTY );
        try {
            if (oldSettingsXmlPath != null) {
                System.clearProperty( CUSTOM_SETTINGS_PROPERTY );
                MavenSettings.reinitSettings();
            }
            final MavenRequest mavenRequest = createMavenRequest(null);
            final MavenEmbedder embedder = new MavenEmbedderMock( mavenRequest );
            final MavenExecutionRequest request = embedder.buildMavenExecutionRequest( mavenRequest );

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

        } catch ( ComponentLookupException cle ) {
            fail( cle.getMessage() );
        } finally {
            if (oldSettingsXmlPath != null) {
                System.setProperty( CUSTOM_SETTINGS_PROPERTY, oldSettingsXmlPath );
                MavenSettings.reinitSettings();
            }
        }
    }

    @Test
    public void testCustomSettingSource() {
        try {
            final MavenRequest mavenRequest = createMavenRequest(new SettingsSourceMock( "<settings/>" ) );
            final MavenEmbedder embedder = new MavenEmbedderMock( mavenRequest );
            final MavenExecutionRequest request = embedder.buildMavenExecutionRequest( mavenRequest );

            assertNotNull( request );

            assertEquals( "<settings/>", readFileAsString( request.getUserSettingsFile() ).trim() );

        } catch ( MavenEmbedderException mee ) {
            fail( mee.getMessage() );

        } catch ( ComponentLookupException cle ) {
            fail( cle.getMessage() );
        }
    }

    public static class MavenEmbedderMock extends MavenEmbedder {

        public MavenEmbedderMock( final MavenRequest mavenRequest ) throws MavenEmbedderException {
            super( mavenRequest );
        }

        @Override
        protected MavenRepositoryConfiguration getMavenRepositoryConfiguration() {
            return new MavenRepositoryConfiguration(getMavenSettings());
        }

        private Settings getMavenSettings() {
            String path = getClass().getResource( "." ).toString().substring( "file:".length() );
            File testSettingsFile = new File( path + "settings_with_repositories.xml" );
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
