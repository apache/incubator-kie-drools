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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static org.drools.codegen.common.DroolsModelBuildContext.APPLICATION_PROPERTIES_YAML_FILE_NAME;
import static org.drools.codegen.common.DroolsModelBuildContext.APPLICATION_PROPERTIES_YML_FILE_NAME;
import static org.junit.jupiter.api.Assertions.*;

class AbstractDroolsModelBuildContextTest {

    @MethodSource("testData")
    @ParameterizedTest
    void loadYmlProperties(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        Properties properties = new Properties();
        AbstractDroolsModelBuildContext.loadYmlProperties(ymlFile, properties);
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
    void loadYmlStringMap(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        Map<String, String> retrieved = AbstractDroolsModelBuildContext.loadYmlStringMap(ymlFile);
        assertNotNull(retrieved);
        commonCheck(retrieved);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void loadYmlMap(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        TreeMap<String, Object> retrieved = AbstractDroolsModelBuildContext.loadYmlMap(ymlFile);
        assertNotNull(retrieved);
        assertTrue(retrieved.containsKey("this"));
    }


    @MethodSource("testData")
    @ParameterizedTest
    void convertYamlObjectToMap(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        TreeMap<String, Object> ymlMap = AbstractDroolsModelBuildContext.loadYmlMap(ymlFile);
        assertNotNull(ymlMap);

        Map<String, String> retrieved = AbstractDroolsModelBuildContext.convertYamlObjectToMap(ymlMap);
        commonCheck(retrieved);
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