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

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.kie.scanner.MavenRepositoryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MavenSettings {

    private static final Logger log = LoggerFactory.getLogger(MavenSettings.class);

    private static final String CUSTOM_SETTINGS_PROPERTY = "kie.maven.settings.custom";

    private static class SettingsHolder {
        private static final File userSettingsFile = initUserSettingsFile();
        private static final Settings settings = initSettings(userSettingsFile);
        private static final MavenRepositoryConfiguration mavenConf = new MavenRepositoryConfiguration(settings);
    }

    public static File getUserSettingsFile() {
        return SettingsHolder.userSettingsFile;
    }

    public static Settings getSettings() {
        return SettingsHolder.settings;
    }

    public static MavenRepositoryConfiguration getMavenRepositoryConfiguration() {
        return SettingsHolder.mavenConf;
    }

    private static Settings initSettings(File userSettingsFile) {
        SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();
        DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();

        if (userSettingsFile != null) {
            request.setUserSettingsFile( userSettingsFile );
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

        return settings;
    }

    private static File initUserSettingsFile() {
        String customSettings = System.getProperty( CUSTOM_SETTINGS_PROPERTY );
        if (customSettings != null) {
            File customSettingsFile = new File( customSettings );
            if (customSettingsFile.exists()) {
                return customSettingsFile;
            } else {
                log.warn("Cannot find custom maven settings file: " + customSettings);
            }
        }

        String userHome = System.getProperty( "user.home" );
        if (userHome != null) {
            File userSettingsFile = new File( userHome + "/.m2/settings.xml" );
            if (userSettingsFile.exists()) {
                return userSettingsFile;
            }
        } else {
            log.warn("User home is not set");
        }

        return null;
    }
}
