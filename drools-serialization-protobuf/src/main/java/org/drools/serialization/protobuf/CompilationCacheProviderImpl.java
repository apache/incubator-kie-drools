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
package org.drools.serialization.protobuf;

import java.io.ByteArrayInputStream;
import java.util.Map;

import com.google.protobuf.ExtensionRegistry;
import org.kie.memorycompiler.resources.ResourceStore;
import org.drools.compiler.kie.builder.impl.AbstractKieModule;
import org.drools.compiler.kie.builder.impl.CompilationCacheProvider;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieModule.CompilationCache;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.base.util.Drools;
import org.drools.serialization.protobuf.kie.KieModuleCache.CompDataEntry;
import org.drools.serialization.protobuf.kie.KieModuleCache.CompilationData;
import org.drools.serialization.protobuf.kie.KieModuleCache.Header;
import org.drools.serialization.protobuf.kie.KieModuleCache.KModuleCache;
import org.drools.serialization.protobuf.kie.KieModuleCacheHelper;
import org.drools.serialization.protobuf.kie.MarshallingKieMetaInfoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompilationCacheProviderImpl implements CompilationCacheProvider {

    private static final Logger log = LoggerFactory.getLogger(CompilationCacheProviderImpl.class);

    @Override
    public CompilationCache getCompilationCache( AbstractKieModule kieModule, Map<String, CompilationCache> compilationCache, String kbaseName) {
        // Map< DIALECT, Map< RESOURCE, List<BYTECODE> > >
        CompilationCache cache = compilationCache.get(kbaseName);
        if (cache == null) {
            byte[] fileContents = kieModule.getBytes( KieBuilderImpl.getCompilationCachePath(kieModule.getReleaseId(), kbaseName));
            if (fileContents != null) {
                ExtensionRegistry registry = KieModuleCacheHelper.buildRegistry();
                try {
                    Header header = KieModuleCacheHelper.readFromStreamWithHeaderPreloaded(new ByteArrayInputStream(fileContents), registry);

                    if (!Drools.isCompatible(header.getVersion().getVersionMajor(),
                            header.getVersion().getVersionMinor(),
                            header.getVersion().getVersionRevision())) {
                        // if cache has been built with an incompatible version avoid to use it
                        log.warn("The compilation cache has been built with an incompatible version. " +
                                "You should recompile your project in order to use it with current release.");
                        return null;
                    }

                    KModuleCache kModuleCache = KModuleCache.parseFrom(header.getPayload());

                    cache = new CompilationCache();
                    for (CompilationData _data : kModuleCache.getCompilationDataList()) {
                        for (CompDataEntry _entry : _data.getEntryList()) {
                            cache.addEntry(_data.getDialect(), _entry.getId(),  _entry.getData().toByteArray());
                        }
                    }
                    compilationCache.put(kbaseName, cache);
                } catch (Exception e) {
                    log.error("Unable to load compilation cache... ", e);
                }
            }
        }
        return cache;
    }

    @Override
    public void writeKieModuleMetaInfo(InternalKieModule kModule, ResourceStore trgMfs) {
        new MarshallingKieMetaInfoBuilder( kModule ).writeKieModuleMetaInfo( trgMfs );
    }
}
