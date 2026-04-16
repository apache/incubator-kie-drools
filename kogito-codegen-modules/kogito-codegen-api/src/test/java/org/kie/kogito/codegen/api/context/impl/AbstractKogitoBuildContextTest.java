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
package org.kie.kogito.codegen.api.context.impl;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.drools.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.codegen.common.DroolsModelBuildContext.APPLICATION_PROPERTIES_YAML_FILE_NAME;
import static org.drools.codegen.common.DroolsModelBuildContext.APPLICATION_PROPERTIES_YML_FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractKogitoBuildContextTest {

    protected KogitoBuildContext.Builder builder;

    @BeforeEach
    public void init() {
        builder = MockKogitoBuildContext.builder().withAddonsConfig(AddonsConfig.DEFAULT);
    }

    @Test
    public void packageNameValidation() {
        assertThat(builder.build().getPackageName()).isEqualTo(KogitoBuildContext.DEFAULT_PACKAGE_NAME);
        assertThatThrownBy(() -> builder.withPackageName(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> builder.withPackageName("i.am.an-invalid.package-name.sorry")).isInstanceOf(IllegalArgumentException.class);
        KogitoBuildContext context = builder.withPackageName(AbstractKogitoBuildContext.DEFAULT_GROUP_ID).build();
        assertThat(context.getPackageName()).isNotEqualTo(AbstractKogitoBuildContext.DEFAULT_GROUP_ID);
    }

    @Test
    public void applicationPropertiesValidation() {
        assertThat(builder.build().getApplicationProperties()).isNotNull();
        assertThatThrownBy(() -> builder.withApplicationProperties((Properties) null)).isInstanceOf(NullPointerException.class).hasMessageContaining("applicationProperties");
    }

    @Test
    public void withAddonsConfig() {
        assertThat(builder.withAddonsConfig(null).build().getAddonsConfig()).isNotNull().isNotEqualTo(AddonsConfig.DEFAULT);
        assertThat(builder.withAddonsConfig(AddonsConfig.DEFAULT).build().getAddonsConfig()).isEqualTo(AddonsConfig.DEFAULT);
    }

    @Test
    public void withClassAvailabilityResolver() {
        assertThatThrownBy(() -> builder.withClassAvailabilityResolver(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("classAvailabilityResolver");
    }

    @Test
    public void withClassSubTypeAvailabilityResolver() {
        assertThatThrownBy(() -> builder.withClassSubTypeAvailabilityResolver(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("classSubTypeAvailabilityResolver");
    }

    @Test
    public void withClassLoader() {
        assertThatThrownBy(() -> builder.withClassLoader(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("classLoader");
    }

    @Test
    public void withAppPaths() {
        assertThat(builder.build().getAppPaths()).isNotNull();
        assertThatThrownBy(() -> builder.withAppPaths(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("appPaths");
    }

    @Test
    public void withGAV() {
        assertThat(builder.build().getGAV()).isEmpty();
        assertThatThrownBy(() -> builder.withGAV(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("gav");
        assertThat(builder.withGAV(KogitoGAV.EMPTY_GAV).build().getGAV()).hasValue(KogitoGAV.EMPTY_GAV);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void loadYmlProperties(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        Properties properties = new Properties();
        AbstractKogitoBuildContext.loadYmlProperties(ymlFile, properties);
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
        Map<String, String> retrieved = AbstractKogitoBuildContext.loadYmlStringMap(ymlFile);
        assertNotNull(retrieved);
        commonCheck(retrieved);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void loadYmlMap(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        TreeMap<String, Object> retrieved = AbstractKogitoBuildContext.loadYmlMap(ymlFile);
        assertNotNull(retrieved);
        assertTrue(retrieved.containsKey("this"));
    }

    @MethodSource("testData")
    @ParameterizedTest
    void convertYamlObjectToMap(String fileName) {
        File ymlFile = FileUtils.getFile(fileName);
        assertTrue(ymlFile.exists());
        TreeMap<String, Object> ymlMap = AbstractKogitoBuildContext.loadYmlMap(ymlFile);
        assertNotNull(ymlMap);

        Map<String, String> retrieved = AbstractKogitoBuildContext.convertYamlObjectToMap(ymlMap);
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

    static class MockKogitoBuildContext extends AbstractKogitoBuildContext {

        public static Builder builder() {
            return new MockKogiotBuildContextBuilder();
        }

        protected MockKogitoBuildContext(MockKogiotBuildContextBuilder builder) {
            super(builder, null, null, "Mock");
        }

        @Override
        public boolean hasRest() {
            return false;
        }

        public static class MockKogiotBuildContextBuilder extends AbstractKogitoBuildContext.AbstractBuilder {

            protected MockKogiotBuildContextBuilder() {
            }

            @Override
            public KogitoBuildContext build() {
                return new MockKogitoBuildContext(this);
            }
        }
    }

}
