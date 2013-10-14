package org.kie.scanner.embedder;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MavenSettings {

    private static final Logger log = LoggerFactory.getLogger(MavenSettings.class);

    private static final Settings settings = initSettings();

    public static Settings getSettings() {
        return settings;
    }

    private static Settings initSettings() {
        SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();

        DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();

        String userHome = System.getProperty( "user.home" );
        if (userHome != null) {
            File userSettingsFile = new File( userHome + "/.m2/settings.xml" );
            if (userSettingsFile.exists()) {
                request.setUserSettingsFile( userSettingsFile );
            }
        } else {
            log.warn("User home is not set");
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
            new RuntimeException(e);
        }

        if (settings.getLocalRepository() == null) {
            if (userHome != null) {
                settings.setLocalRepository( userHome + "/.m2/repository" );
            } else {
                log.error("Cannot find maven local repository");
            }
        }

        return settings;
    }

}
