package org.kie.scanner;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.junit.Test;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.*;

public class MavenRepositoryTest {

    @Test
    public void testMirrors() {
        MavenRepository repo = new MavenRepositoryMock(Aether.getAether());
        Collection<RemoteRepository> remoteRepos = repo.getRemoteRepositoriesForRequest();
        assertEquals(3, remoteRepos.size());
        for (RemoteRepository remoteRepo : remoteRepos) {
            assertTrue(remoteRepo.getId().equals("local") ||
                       remoteRepo.getId().equals("qa") ||
                       remoteRepo.getId().equals("foo"));
        }
    }

    public static class MavenRepositoryMock extends MavenRepository {
        protected MavenRepositoryMock(Aether aether) {
            super(aether);
        }

        @Override
        protected Settings getSettings() {
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
