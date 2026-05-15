/*
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
package org.drools.codegen.common.context;

import org.drools.util.FileUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static org.drools.codegen.common.DroolsModelBuildContext.APPLICATION_PROPERTIES_YAML_FILE_NAME;
import static org.drools.codegen.common.DroolsModelBuildContext.APPLICATION_PROPERTIES_YML_FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ModelBuildContextUtilsTest {

    @MethodSource("testData")
    @ParameterizedTest
    void loadYmlPropertiesByFile(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        Properties properties = new Properties();
        ModelBuildContextUtils.loadYmlProperties(ymlFile, properties);
        assertEquals("test", properties.getProperty("this.is.a"));
        assertEquals("notNull", properties.getProperty("this.is.b"));
        assertEquals("test", properties.getProperty("this.was.a"));
        assertEquals("notNull", properties.getProperty("this.was.b"));
        assertEquals("test", properties.getProperty("that.is.a"));
        assertEquals("notNull", properties.getProperty("that.is.b"));
        assertEquals("test", properties.getProperty("that.was.a"));
        assertEquals("notNull", properties.getProperty("that.was.b"));
    }

    @MethodSource("testData")
    @ParameterizedTest
    void loadYmlPropertiesByInputStream(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        Properties properties = new Properties();
        try (InputStream ymlResourceStream = new FileInputStream(ymlFile)) {
            ModelBuildContextUtils.loadYmlProperties(ymlResourceStream, properties);
            assertEquals("test", properties.getProperty("this.is.a"));
            assertEquals("notNull", properties.getProperty("this.is.b"));
            assertEquals("test", properties.getProperty("this.was.a"));
            assertEquals("notNull", properties.getProperty("this.was.b"));
            assertEquals("test", properties.getProperty("that.is.a"));
            assertEquals("notNull", properties.getProperty("that.is.b"));
            assertEquals("test", properties.getProperty("that.was.a"));
            assertEquals("notNull", properties.getProperty("that.was.b"));
        } catch (Exception e) {
            fail("Unexpected exception while loading yml string map from file: " + fileName, e);
        }

    }

    @MethodSource("testData")
    @ParameterizedTest
    void loadYmlStringMap(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        try (InputStream ymlResourceStream = new FileInputStream(ymlFile)) {
            Map<String, String> retrieved = ModelBuildContextUtils.loadYmlStringMap(ymlResourceStream);
            assertNotNull(retrieved);
            commonCheck(retrieved);
        } catch (Exception e) {
            fail("Unexpected exception while loading yml string map from file: " + fileName, e);
        }
    }

    @MethodSource("testData")
    @ParameterizedTest
    void loadYmlMap(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        try (InputStream ymlResourceStream = new FileInputStream(ymlFile)) {
            TreeMap<String, Object> retrieved = ModelBuildContextUtils.loadYmlMap(ymlResourceStream);
            assertNotNull(retrieved);
            assertTrue(retrieved.containsKey("this"));
        } catch (Exception e) {
            fail("Unexpected exception while loading yml string map from file: " + fileName, e);
        }
    }


    @MethodSource("testData")
    @ParameterizedTest
    void convertYamlObjectToMap(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        try (InputStream ymlResourceStream = new FileInputStream(ymlFile)) {
            TreeMap<String, Object> ymlMap = ModelBuildContextUtils.loadYmlMap(ymlResourceStream);
            assertNotNull(ymlMap);
            Map<String, String> retrieved = ModelBuildContextUtils.convertYamlObjectToMap(ymlMap);
            commonCheck(retrieved);
        } catch (Exception e) {
            fail("Unexpected exception while loading yml string map from file: " + fileName, e);
        }
    }

    private static Collection<String> testData() {
        return java.util.List.of(APPLICATION_PROPERTIES_YML_FILE_NAME, APPLICATION_PROPERTIES_YAML_FILE_NAME);
    }

    private void commonCheck(Map<String, String> retrieved) {
        assertNotNull(retrieved);
        assertTrue(retrieved.containsKey("this.is.a"));
        assertEquals("test", retrieved.get("this.is.a"));
        assertTrue(retrieved.containsKey("this.is.b"));
        assertEquals("notNull", retrieved.get("this.is.b"));
        assertTrue(retrieved.containsKey("this.was.a"));
        assertEquals("test", retrieved.get("this.was.a"));
        assertTrue(retrieved.containsKey("this.was.b"));
        assertEquals("notNull", retrieved.get("this.was.b"));

        assertTrue(retrieved.containsKey("that.is.a"));
        assertEquals("test", retrieved.get("that.is.a"));
        assertTrue(retrieved.containsKey("that.is.b"));
        assertEquals("notNull", retrieved.get("that.is.b"));
        assertTrue(retrieved.containsKey("that.was.a"));
        assertEquals("test", retrieved.get("that.was.a"));
        assertTrue(retrieved.containsKey("that.was.b"));
        assertEquals("notNull", retrieved.get("that.was.b"));
    }

}