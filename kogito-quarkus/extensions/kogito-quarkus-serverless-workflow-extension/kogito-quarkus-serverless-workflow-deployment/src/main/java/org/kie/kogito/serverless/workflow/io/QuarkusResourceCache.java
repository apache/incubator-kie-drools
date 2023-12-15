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
package org.kie.kogito.serverless.workflow.io;

import java.net.URI;
import java.util.function.Function;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.runtime.Startup;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

@Startup
public class QuarkusResourceCache {

    @Inject
    @CacheName("SWFResourceCache")
    Cache cache;

    @PostConstruct
    void init() {
        ResourceCacheFactory.setResourceCache(this::get);
    }

    private byte[] get(URI uri, Function<URI, byte[]> retrieveCall) {
        return cache.get(uri, retrieveCall).await().indefinitely();
    }
}
