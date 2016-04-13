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

package org.kie.scanner;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MavenRepositoryTest extends AbstractKieCiTest {

    @Test
    public void testMirrors() {
        MavenRepository repo = new MavenRepositoryMock(Aether.getAether());
        Collection<RemoteRepository> remoteRepos = repo.getRemoteRepositoriesForRequest();
        assertEquals(2, remoteRepos.size());
        for (RemoteRepository remoteRepo : remoteRepos) {
            assertTrue(remoteRepo.getId().equals("qa") ||
                       remoteRepo.getId().equals("foo"));
        }
    }

    public static class MavenRepositoryMock extends MavenRepository {
        protected MavenRepositoryMock(Aether aether) {
            super(aether);
        }

        @Override
        protected MavenRepositoryConfiguration getMavenRepositoryConfiguration() {
            return new MavenRepositoryConfiguration(getMavenSettings());
        }

        private Settings getMavenSettings() {
            String path = getClass().getResource(".").toString().substring("file:".length());
            File testSettingsFile = new File(path + "settings_with_mirror.xml");
            assertTrue(testSettingsFile.exists());

            SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();
            DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
            request.setUserSettingsFile( testSettingsFile );

            try {
                return settingsBuilder.build( request ).getEffectiveSettings();
            } catch ( SettingsBuildingException e ) {
                throw new RuntimeException(e);
            }
        }
    }
}
