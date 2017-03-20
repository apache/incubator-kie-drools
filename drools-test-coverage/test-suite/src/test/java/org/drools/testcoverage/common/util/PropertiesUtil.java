/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common.util;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Provides utility methods to obtain relevant properties and directories for a test.
 */
public class PropertiesUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * The maximum number of parent directories of baseDir to be searched for build.properties file.
     */
    private static final int BUILD_PROPS_SEARCH_PARENT_DIR_LIMIT = 2;

    // Don't use these properties directly, they are lazily initialized on-demand. Use getters instead.
    private static Properties properties;
    private static File basedir;

    private PropertiesUtil() {
    }

    /**
     * Gets the base directory of the test execution that was specified by System property <code>basedir</code>.
     * Method expects the property to be set.
     *
     * @return the basedir directory
     */
    public static synchronized File getBasedir() {
        if (basedir == null) {
            String basedirProp = System.getProperty("basedir");
            Validate.notNull(basedirProp, "System property for basedir not set!");
            basedir = new File(basedirProp);
            Validate.isTrue(basedir.exists(), "Basedir '%s' does not exist! Check value of 'basedir' system property.",
                    basedir.getAbsolutePath());
        }
        return basedir;
    }

    /**
     * Returns location of directory for temporary files
     *
     * @return directory to save temporary files to
     */
    public static synchronized File getTempDir() {
        File tempDir;
        try {
            tempDir = new File(getBasedir(), getProperty("temp.dir", "target/tmp"));
        } catch (Exception ex) {
            LOGGER.error("Error occured when attempting to retrieve the 'temp.dir' property", ex);
            // if the above failed for what ever the reason - return
            // default java system directory
            tempDir = new File(System.getProperty("java.io.tmpdir"), "brms-tests");
        }

        if (!tempDir.exists() && !tempDir.mkdir()) {
            throw new IllegalStateException("Cannot create temp.dir at '" + tempDir.getAbsolutePath() + "'!");
        }

        return tempDir;
    }

    /**
     * Returns the directory to store log files.
     *
     * @return directory to store log files to
     */
    public static synchronized File getLogDir() {
        return new File(getBasedir(), getProperty("log.dir", "target/log"));
    }

    /**
     * Retrieves the property value.
     *
     * @param key name of the property
     * @return Property value or <code>null</code> if undefined.
     */
    public static String getProperty(String key) {
        return getProperties().getProperty(key);
    }

    /**
     * Retrieves the property value.
     *
     * @param key name of the property
     * @param defaultValue value to return if property is undefined
     * @return Property value or <code>defaultValue</code> if undefined.
     */
    public static String getProperty(String key, String defaultValue) {
        // Should not fail on build.properties file not found! Return defaultValue instead.
        try {
            return getProperties().getProperty(key, defaultValue);
        } catch (Exception ex) {
            LOGGER.error("Error occured when attempting to retrieve the '" + key + "' property", ex);
            return defaultValue;
        }
    }

    /**
     * Get all properties defined in build.properties file.
     *
     * @throws IllegalStateException when reading the property fails
     * @return properties from build.properties
     */
    public static synchronized Properties getProperties() {
        return (properties==null) ? createProperties() : (Properties) properties.clone();
    }

    private static synchronized Properties createProperties() {
        // lazy initialization of properties
        File propFile = getPropertiesFile(getBasedir().getAbsoluteFile(), BUILD_PROPS_SEARCH_PARENT_DIR_LIMIT);
        Validate.isTrue(propFile.exists(), "Couldn't find build.properties at '%s'!", propFile.getAbsolutePath());
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(propFile)) {
            properties.load(fis);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Properties file not found. At this point, this is impossible.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Properties file cannot be read!", e);
        }
        return (Properties) properties.clone();
    }

    private static File getPropertiesFile(final File dir, final int parentDirLimit) {
        LOGGER.debug("Searching for 'build.properties', parent search limit is {}.", parentDirLimit);
        Validate.isTrue(parentDirLimit >= 0 && dir != null,
                "build.properties file not found before reaching the limit for searching parent directories.");
        LOGGER.debug("- searching dir: {}", dir.getAbsolutePath());

        final File propFile = new File(dir, "build.properties");
        if (!propFile.exists()) {
            return getPropertiesFile(dir.getParentFile(), parentDirLimit - 1);
        } else {
            return propFile;
        }
    }
}
