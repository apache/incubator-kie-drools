/**
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
package org.kie.dmn.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableMap;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMetadata;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseDMNContextTest {

    protected static final Map<String, Object> DEFAULT_ENTRIES = ImmutableMap.of(
            "s_entr1", "value1",
            "i_entr2", 2,
            "f_entr3", 3.0,
            "s_entr4", "4"
    );

    protected static final String DEFAULT_SCOPE_NAME = "theName";
    protected static final String DEFAULT_SCOPE_NAMESPACE = "theNamespace";
    protected static final Map<String, Object> DEFAULT_SCOPE_ENTRIES = ImmutableMap.of(
            "s_scEn1", "scopeValue1",
            "i_scEn2", 2,
            "f_scEn3", 3.0,
            "s_scEn4", "4"
    );

    protected static final Map<String, Object> DEFAULT_METADATA = ImmutableMap.of(
            "s_meta1", "value1",
            "i_meta2", 2,
            "f_meta3", 3.0,
            "s_meta4", "4"
    );

    public void testEquals(DMNContext ctx, Map<String, Object> expectedEntries, Map<String, Object> expectedMetadata) {
        testEntries(ctx, expectedEntries);
        testEntries(ctx.getMetadata(), expectedMetadata);
    }

    public void testCloneAndAlter(DMNContext ctx, Map<String, Object> expectedEntries, Map<String, Object> expectedMetadata) {
        // test that original context matches expected entries
        testEquals(ctx, expectedEntries, expectedMetadata);

        // test that cloned context matches original context
        DMNContext cloned = ctx.clone();
        testEquals(cloned, expectedEntries, expectedMetadata);

        // alter original context
        Map<String, Object> alteredEntries = alter(ctx::set, expectedEntries, ImmutableMap.of(
                "f_entr3", 6.0,
                "s_entr5", "five"
        ));
        Map<String, Object> alteredMetadata = alter(ctx.getMetadata()::set, expectedMetadata, ImmutableMap.of(
                "i_meta2", 20,
                "s_meta5", "FIVE"
        ));

        // test that original context matches altered entries
        testEquals(ctx, alteredEntries, alteredMetadata);
        // test that cloned context still matches original context
        testEquals(cloned, expectedEntries, expectedMetadata);

        // alter original context
        Map<String, Object> alteredClonedEntries = alter(cloned::set, expectedEntries, ImmutableMap.of(
                "f_entr3", 9.0,
                "s_entr6", "six"
        ));
        Map<String, Object> alteredClonedMetadata = alter(cloned.getMetadata()::set, expectedMetadata, ImmutableMap.of(
                "i_meta2", 200,
                "s_meta6", "SIX"
        ));

        // test that original context still matches altered entries
        testEquals(ctx, alteredEntries, alteredMetadata);
        // test that cloned context matches altered cloned entries
        testEquals(cloned, alteredClonedEntries, alteredClonedMetadata);
    }

    public void testPushAndPopScope(DMNContext ctx, Map<String, Object> expectedEntries, Map<String, Object> expectedMetadata) {
        // test that original context matches expected entries
        testEquals(ctx, expectedEntries, expectedMetadata);

        // test that no namespace is set
        assertNamespaceIsAbsent(ctx);

        // alter context by pushing a new scope with default entries
        ctx.pushScope(DEFAULT_SCOPE_NAME, DEFAULT_SCOPE_NAMESPACE);
        for (Map.Entry<String, Object> entry : DEFAULT_SCOPE_ENTRIES.entrySet()) {
            ctx.set(entry.getKey(), entry.getValue());
        }

        // test that namespace is the expected one with the expected entries
        assertNamespaceEquals(DEFAULT_SCOPE_NAMESPACE, ctx);
        testEquals(ctx, DEFAULT_SCOPE_ENTRIES, expectedMetadata);

        // alter context by popping the scope
        ctx.popScope();

        // test that no namespace is set again
        assertNamespaceIsAbsent(ctx);

        // generate altered entry map
        Map<String, Object> alteredEntries = new HashMap<>(expectedEntries);
        alteredEntries.put(DEFAULT_SCOPE_NAME, DEFAULT_SCOPE_ENTRIES);

        // test that entries matches altered map
        testEquals(ctx, alteredEntries, expectedMetadata);
    }

    public void testEntries(DMNContext context, Map<String, Object> expectedEntries) {
        testEntries(containerFor(context), expectedEntries);
    }

    public void testEntries(DMNMetadata metadata, Map<String, Object> expectedEntries) {
        testEntries(containerFor(metadata), expectedEntries);
    }

    private void testEntries(EntryContainerFacade container, Map<String, Object> expectedEntries) {
        Map<String, Object> currentEntries = container.getAll();

        assertThat(currentEntries).isNotNull();
        assertThat(currentEntries).hasSameSizeAs(expectedEntries);

        for (Map.Entry<String, Object> entry : expectedEntries.entrySet()) {
            assertThat(currentEntries).containsKey(entry.getKey());
            assertThat(currentEntries.get(entry.getKey())).isEqualTo(entry.getValue());

            assertThat(container.isDefined(entry.getKey())).isTrue();
            assertThat(container.get(entry.getKey())).isEqualTo(entry.getValue());
        }
    }

    public static <K,V> Map<K,V> alter(BiConsumer<K,V> setter, Map<K,V> original, Map<K,V> additions) {
        Map<K,V> altered = new HashMap<>(original);
        for (Map.Entry<K,V> entry : additions.entrySet()) {
            altered.put(entry.getKey(), entry.getValue());
            setter.accept(entry.getKey(), entry.getValue());
        }
        return altered;
    }

    public static void assertNamespaceIsAbsent(DMNContext ctx) {
        Optional<String> optNamespace = ctx.scopeNamespace();
        assertThat(optNamespace).isNotNull();
        assertThat(optNamespace).isNotPresent();
    }

    public static void assertNamespaceEquals(String expectedName, DMNContext ctx) {
        Optional<String> optNamespace = ctx.scopeNamespace();
        assertThat(optNamespace).isNotNull();
        assertThat(optNamespace).isPresent();
        assertThat(optNamespace.get()).isEqualTo(expectedName);
    }

    public static void assertNamespaceEquals(DMNContext expectedCtx, DMNContext testCtx) {
        Optional<String> optExpectedNamespace = expectedCtx.scopeNamespace();
        assertThat(optExpectedNamespace).isNotNull();
        Optional<String> optTestNamespace = testCtx.scopeNamespace();
        assertThat(optTestNamespace).isNotNull();

        if (optExpectedNamespace.isPresent()) {
            assertThat(optTestNamespace).isPresent();
            assertThat(optTestNamespace.get()).isEqualTo(optExpectedNamespace.get());
        } else {
            assertThat(optTestNamespace).isNotPresent();
        }
    }

    private static EntryContainerFacade containerFor(DMNContext context) {
        return new DMNContextFacade(context);
    }

    private static EntryContainerFacade containerFor(DMNMetadata metadata) {
        return new DMNMetadataFacade(metadata);
    }

    private interface EntryContainerFacade {
        Object set(String name, Object value);

        Object get(String name);

        Map<String, Object> getAll();

        boolean isDefined(String name);
    }

    private static class DMNContextFacade implements EntryContainerFacade {
        private final DMNContext context;

        private DMNContextFacade(DMNContext context) {
            this.context = context;
        }

        @Override
        public Object set(String name, Object value) {
            return context.set(name, value);
        }

        @Override
        public Object get(String name) {
            return context.get(name);
        }

        @Override
        public Map<String, Object> getAll() {
            return context.getAll();
        }

        @Override
        public boolean isDefined(String name) {
            return context.isDefined(name);
        }
    }

    private static class DMNMetadataFacade implements EntryContainerFacade {
        private final DMNMetadata metadata;

        private DMNMetadataFacade(DMNMetadata metadata) {
            this.metadata = metadata;
        }

        @Override
        public Object set(String name, Object value) {
            return metadata.set(name, value);
        }

        @Override
        public Object get(String name) {
            return metadata.get(name);
        }

        @Override
        public Map<String, Object> getAll() {
            return metadata.asMap();
        }

        @Override
        public boolean isDefined(String name) {
            return metadata.get(name) != null;
        }
    }

}
