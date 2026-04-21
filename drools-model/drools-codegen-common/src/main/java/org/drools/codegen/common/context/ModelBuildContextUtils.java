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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class ModelBuildContextUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelBuildContextUtils.class);

    public static void loadYmlProperties(File ymlFile, Properties applicationProperties) {
        if (ymlFile.exists() && ymlFile.isFile() && ymlFile.canRead()) {
            try (InputStream ymlResourceStream = new FileInputStream(ymlFile)) {
                loadYmlProperties(ymlResourceStream, applicationProperties);
            } catch (IOException e) {
                LOGGER.debug("Unable to load '{}'.", ymlFile.getName(), e);
            }
        } else {
            LOGGER.debug("Unable to load '{}'.", ymlFile.getName());
        }
    }

    public static void loadYmlProperties(InputStream ymlStream, Properties applicationProperties) {
        Map<String, String> ymlMap  = loadYmlStringMap(ymlStream);
        if (ymlMap != null) {
            applicationProperties.putAll(ymlMap);
        }
    }

    static Map<String, String> loadYmlStringMap(InputStream ymlStream) {
        TreeMap<String, Object> ymlMap = loadYmlMap(ymlStream);
        if (ymlMap != null) {
            return convertYamlObjectToMap(ymlMap);
        } else {
            return null;
        }
    }

    static TreeMap<String, Object> loadYmlMap(InputStream ymlStream) {
        if (ymlStream != null) {
            Yaml yaml = new Yaml();
            try (Reader yamlFileReader = new InputStreamReader(ymlStream, StandardCharsets.UTF_8)){
                return yaml.loadAs(yamlFileReader, TreeMap.class);
            } catch (IOException e) {
                LOGGER.debug("Unable to load YaML file.", e);
            }
        } else {
            LOGGER.debug("Unable to load YaML file.");
        }
        return null;
    }

    static Map<String, String> convertYamlObjectToMap(TreeMap<String, Object> toConvert) {
        Map<String, String> toReturn = new HashMap<>();
        convertYamlObjectToMap(toConvert, new StringBuilder(), toReturn);
        return toReturn;
    }

    static void convertYamlObjectToMap(Map<String, Object> toRead, StringBuilder builder, Map<String, String> toPopulate) {
        toRead.forEach((key, value) -> {
            if (value instanceof Map) {
                StringBuilder newBuilder = new StringBuilder(builder);
                convertYamlObjectToMap((Map<String, Object>) value, newBuilder.append(key).append("."), toPopulate);
            } else {
                String property = builder.toString() + key;
                String propertyValue = value != null ? value.toString() : "";
                toPopulate.put(property, propertyValue);
            }
        });
    }
}   