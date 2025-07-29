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
package org.kie.dmn.core.util;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNVersion;
import org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller;
import org.kie.dmn.core.compiler.DMNTypeRegistry;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNTestUtil {
    private static Logger logger = LoggerFactory.getLogger(XStreamMarshaller.class);
    private DMNTestUtil() {
        // No constructor for util class.
    }

    public static DMNModel getAndAssertModelNoErrors(final DMNRuntime runtime, final String namespace, final String modelName) {
        DMNModel dmnModel = runtime.getModel(namespace, modelName);
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        return dmnModel;
    }

    public static Function<String, Reader> getRelativeResolver(String key, String content) {
        return s -> s.equals(key) ? new StringReader(content) : null;
    }

    /**
     * Dynamically loads and instantiates the latest version of DMNTypeRegistry
     * @return DMNTypeRegistry
     */
    public static DMNTypeRegistry getLatestDMNTypeRegistry() {
        try {
            String dMNTypeRegistryClassName = String.format("org.kie.dmn.core.compiler.DMNTypeRegistryV%s", DMNVersion.getLatest().getDmnVersion());
            logger.warn("DMNTypeRegistry class is {}", dMNTypeRegistryClassName);
            Class<? extends DMNTypeRegistry> registryClass = (Class<?
                    extends DMNTypeRegistry>) Class.forName(dMNTypeRegistryClassName);
            Constructor<? extends DMNTypeRegistry> constructor =
                    registryClass.getConstructor(Map.class);

            return constructor.newInstance(Collections.emptyMap());
        } catch (ClassNotFoundException | IllegalAccessException | ClassCastException | NoSuchMethodException |
                 InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Dynamically loads and instantiates the latest URI_FEEL value reflexively
     * @return string URI_FEEL
     */
    public static String getLatestFeelReflectively() {
        try {
            String latestVersion = convertClassNameVersionCase(String.valueOf(DMNVersion.getLatest()));
            String kieDMNModelInstrumentedBaseClassName = String.format("org.kie.dmn.model.%s" +
                            ".KieDMNModelInstrumentedBase",
                    latestVersion);
            Class<? extends DMNModelInstrumentedBase> kieDMNModelInstrumentedBaseClass = (Class<?
                    extends DMNModelInstrumentedBase>) Class.forName(kieDMNModelInstrumentedBaseClassName);
            Field uriFeelField = kieDMNModelInstrumentedBaseClass.getField("URI_FEEL");
            return (String) uriFeelField.get(null);
        } catch (NoSuchFieldException e) {
            logger.warn("Failed to retrieve URI_FEEL reflexively");
            return null;
        } catch (ClassNotFoundException | IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a version suffix in the package name from uppercase "V" to lowercase "v".
     * @param version
     * @return modified string in lowercase
     */
    public static String convertClassNameVersionCase(String version) {
        if (version == null || version.isEmpty())
            return version;
        return version.replaceAll("\\V(\\d+_\\d+)", "v$1");
    }

}
