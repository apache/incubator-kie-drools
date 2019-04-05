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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.FileSettingsSource;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsSource;
import org.apache.maven.settings.building.StringSettingsSource;
import org.apache.maven.settings.building.UrlSettingsSource;
import org.kie.scanner.Aether;
import org.kie.scanner.MavenRepository;
import org.kie.scanner.MavenRepositoryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.components.cipher.DefaultPlexusCipher;
import org.sonatype.plexus.components.cipher.PlexusCipherException;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

public class MavenSettings {

    private static final Logger log = LoggerFactory.getLogger(MavenSettings.class);

    public static final String CUSTOM_SETTINGS_PROPERTY = "kie.maven.settings.custom";


    private static class SettingsHolder {
        private static SettingsSource userSettingsSource = initUserSettingsSource();
        private static Settings settings = initSettings(userSettingsSource);
        private static MavenRepositoryConfiguration mavenConf = new MavenRepositoryConfiguration(settings);

        private static void reinitSettings() {
            userSettingsSource = initUserSettingsSource();
            internalInit();
        }

        private static void reinitSettingsFromString(String settings) {
            userSettingsSource = new StringSettingsSource( settings );
            internalInit();
        }

        private static void internalInit() {
            settings = initSettings(userSettingsSource);
            mavenConf = new MavenRepositoryConfiguration( settings);
            Aether.instance = null;
            MavenProjectLoader.mavenProject = null;
            MavenRepository.defaultMavenRepository = null;
        }
    }

    // USE ONLY FOR TESTING PURPOSES
    public static void reinitSettings() {
        SettingsHolder.reinitSettings();
    }

    // USE ONLY FOR TESTING PURPOSES
    public static void reinitSettingsFromString(String settings) {
        SettingsHolder.reinitSettingsFromString(settings);
    }

    public static SettingsSource getUserSettingsSource() {
        return SettingsHolder.userSettingsSource;
    }

    public static Settings getSettings() {
        return SettingsHolder.settings;
    }

    public static MavenRepositoryConfiguration getMavenRepositoryConfiguration() {
        return SettingsHolder.mavenConf;
    }

    private static Settings initSettings(SettingsSource userSettingsSource) {
        SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();
        DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();

        if (userSettingsSource != null) {
            request.setUserSettingsSource( userSettingsSource );
        }

        String mavenHome = System.getenv( "M2_HOME" );
        if (mavenHome != null) {
            File globalSettingsFile = new File( mavenHome + "/conf/settings.xml" );
            if (globalSettingsFile.exists()) {
                request.setGlobalSettingsFile( globalSettingsFile );
            }
        } else {
            log.warn("Environment variable M2_HOME is not set");
        }

        request.setSystemProperties( System.getProperties() );

        Settings settings = null;
        try {
            settings = settingsBuilder.build( request ).getEffectiveSettings();
        } catch ( SettingsBuildingException e ) {
            throw new RuntimeException(e);
        }

        if (settings.getLocalRepository() == null) {
            String userHome = System.getProperty( "user.home" );
            if (userHome != null) {
                settings.setLocalRepository( userHome + "/.m2/repository" );
            } else {
                log.error("Cannot find maven local repository");
            }
        }

        return decryptPasswords(settings);
    }

    static class CustomDefaultSecDispatcher extends DefaultSecDispatcher {

        CustomDefaultSecDispatcher() {
            try {
                this._cipher = new DefaultPlexusCipher();
                String userHome = System.getProperty("user.home");
                final String DEFAULT_SETTINGS_SECURITY_PATH = userHome == null ? ".settings-security.xml" : userHome.concat("/.m2/settings-security.xml").replace('/', File.separatorChar);
                this.setConfigurationFile(DEFAULT_SETTINGS_SECURITY_PATH);
            } catch (PlexusCipherException e) {
            }
        }

    }
    private static Settings decryptPasswords(Settings settings) {
        try {
            SecDispatcher securityDispatcher = new CustomDefaultSecDispatcher();
            for (Server server : settings.getServers()) {
                if (server.getPassword() != null) {
                    server.setPassword(securityDispatcher.decrypt(server.getPassword()));
                }
            }
        } catch (SecDispatcherException e) {
            return settings;
        }

        return settings;
    }

    private static SettingsSource initUserSettingsSource() {
        String customSettings = System.getProperty( CUSTOM_SETTINGS_PROPERTY );
        if (customSettings != null) {
            File customSettingsFile = new File( customSettings );
            if (customSettingsFile.exists()) {
                return new FileSettingsSource( customSettingsFile );
            } else {
                try {
                    return new UrlSettingsSource( new URL( customSettings ) );
                } catch (MalformedURLException e) {
                    // Ignore
                }
                log.warn("Cannot find custom maven settings: " + customSettings);
            }
        }

        String userHome = System.getProperty( "user.home" );
        if (userHome != null) {
            File userSettingsFile = new File( userHome + "/.m2/settings.xml" );
            if (userSettingsFile.exists()) {
                return new FileSettingsSource( userSettingsFile );
            }
        } else {
            log.warn("User home is not set");
        }

        return null;
    }
}
