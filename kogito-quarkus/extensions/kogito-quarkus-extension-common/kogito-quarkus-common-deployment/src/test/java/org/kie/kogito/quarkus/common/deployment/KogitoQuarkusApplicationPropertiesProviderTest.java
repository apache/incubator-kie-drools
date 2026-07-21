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
package org.kie.kogito.quarkus.common.deployment;

import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KogitoQuarkusApplicationPropertiesProviderTest {

    private KogitoQuarkusApplicationPropertiesProvider provider;

    private static final Map<String, String> DEFAULT_PROPERTIES = Map.of("this.is.a", "test",
            "this.is.b", "notNull",
            "this.was.a", "test",
            "this.was.b", "notNull",
            "that.is.a", "test",
            "that.is.b", "notNull",
            "that.was.a", "test",
            "that.was.b", "notNull");

    @BeforeEach
    public void setUp() {
        provider = new KogitoQuarkusApplicationPropertiesProvider();
    }

    @Test
    void getApplicationProperty() {
        DEFAULT_PROPERTIES.forEach((key, value) -> assertEquals(value, provider.getApplicationProperty(key).orElse(null)));
    }

    @Test
    void getApplicationPropertyOverride() {
        DEFAULT_PROPERTIES.keySet().forEach(key -> System.setProperty(key, "overridden"));
        KogitoQuarkusApplicationPropertiesProvider providerWithOverrides = new KogitoQuarkusApplicationPropertiesProvider();
        DEFAULT_PROPERTIES.keySet().forEach(key -> assertEquals("overridden", providerWithOverrides.getApplicationProperty(key).orElse(null)));
        DEFAULT_PROPERTIES.keySet().forEach(System::clearProperty);
    }

    @Test
    void getApplicationProperties() {
        Collection<String> retrieved = provider.getApplicationProperties();
        DEFAULT_PROPERTIES.forEach((key, value) -> assertTrue(retrieved.contains(key)));
    }

    @Test
    void testGetApplicationProperty() {
        DEFAULT_PROPERTIES.forEach((key, value) -> assertEquals(value, provider.getApplicationProperty(key, String.class).orElse(null)));
    }

    @Test
    void setApplicationProperty() {
        provider.setApplicationProperty("test.key", "test.value");
        assertEquals("test.value", provider.getApplicationProperty("test.key").orElse(null));
    }

    @Test
    void removeApplicationProperty() {
        provider.setApplicationProperty("test.key", "test.value");
        assertTrue(provider.getApplicationProperties().contains("test.key"));
        provider.removeApplicationProperty("test.key");
        assertFalse(provider.getApplicationProperties().contains("test.key"));
    }

}
