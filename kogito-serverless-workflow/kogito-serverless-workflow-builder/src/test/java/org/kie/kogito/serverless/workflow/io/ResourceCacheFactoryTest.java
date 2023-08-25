/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.io;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceCacheFactoryTest {

    private URI uri;

    private AtomicInteger counter;

    @BeforeEach
    void setup() {
        uri = URI.create("http:://www.google.com");
        counter = new AtomicInteger();
    }

    @Test
    void testDefaultResourceCache() {
        final int numOfTimes = 3;
        callIt(numOfTimes);
        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    void testCustomResourceCache() {
        ResourceCache defaultCache = ResourceCacheFactory.getCache();
        try {
            ResourceCacheFactory.setResourceCache(new TestResourceCache());
            callIt(7);
            assertThat(counter.get()).isEqualTo(1);

        } finally {
            ResourceCacheFactory.setResourceCache(defaultCache);
        }
    }

    private static class TestResourceCache implements ResourceCache {
        private Map<URI, byte[]> map = new ConcurrentHashMap<>();

        @Override
        public byte[] get(URI uri, Function<URI, byte[]> retrieveCall) {
            return map.computeIfAbsent(uri, retrieveCall);
        }
    }

    private void callIt(int numOfTimes) {
        ResourceCache cache = ResourceCacheFactory.getCache();
        while (numOfTimes-- > 0) {
            cache.get(uri, this::called);
        }
    }

    private byte[] called(URI uri) {
        return new byte[] { (byte) counter.incrementAndGet() };
    }
}
