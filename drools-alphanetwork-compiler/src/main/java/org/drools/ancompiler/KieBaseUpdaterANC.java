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
package org.drools.ancompiler;

import java.util.Map;
import java.util.Optional;

import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.compiler.kie.builder.impl.KieBaseUpdaterOptions;
import org.drools.compiler.kie.builder.impl.KieBaseUpdatersContext;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.Rete;
import org.kie.api.KieBase;
import org.kie.api.conf.Option;
import org.kie.internal.builder.conf.AlphaNetworkCompilerOption;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.ancompiler.MapUtils.mapValues;

public class KieBaseUpdaterANC implements KieBaseUpdater {

    private final Logger logger = LoggerFactory.getLogger(KieBaseUpdaterANC.class);

    private final KieBaseUpdatersContext ctx;

    public KieBaseUpdaterANC(KieBaseUpdatersContext ctx) {
        this.ctx = ctx;
    }

    public void run() {
        Optional<Option> ancMode = ctx.getOption(AlphaNetworkCompilerOption.class);

        // find the new compiled alpha network in the classpath, if it's not there,
        // generate compile it and reattach it
        if (ancMode.filter(AlphaNetworkCompilerOption.INMEMORY::equals).isPresent()) {
            inMemoryUpdate(ctx.getClassLoader(), ctx.getRete());
        } // load it from the kjar
        else if (ancMode.filter(AlphaNetworkCompilerOption.LOAD::equals).isPresent()) {
            logger.debug("Loading compiled alpha network from KJar");
            loadFromKJar(ctx.getClassLoader(), ctx.getRete());
        }
    }

    /**
     * This assumes the kie-memory-compiler module is provided at runtime
     */
    private void inMemoryUpdate(ClassLoader rootClassLoader, Rete rete) {
        Map<String, CompiledNetworkSources> compiledNetworkSourcesMap = ObjectTypeNodeCompiler.compiledNetworkSourceMap(rete);
        if (!compiledNetworkSourcesMap.isEmpty()) {
            Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(mapValues(compiledNetworkSourcesMap, CompiledNetworkSources::getSource),
                                                                              rootClassLoader);
            // No need to clear previous sinks/ANC compiled instances
            // as they are removed by ReteOOBuilder.removeTerminalNode after standard KieBaseUpdaterImpl
            compiledNetworkSourcesMap.values().forEach(c -> {
                Class<?> aClass = compiledClasses.get(c.getName());
                c.createInstanceAndSet(aClass);
            });
        }
    }

    private void loadFromKJar(ClassLoader rootClassLoader, Rete rete) {
        // There's not actual need to regenerate the source here but the indexableConstraint is parsed throughout the generation
        // It should be possible to get the indexable constraint without generating the full source
        // see https://issues.redhat.com/browse/DROOLS-5718
        Map<String, CompiledNetworkSources> compiledNetworkSourcesMap = ObjectTypeNodeCompiler.compiledNetworkSourceMap(rete);
        for (Map.Entry<String, CompiledNetworkSources> kv : compiledNetworkSourcesMap.entrySet()) {
            String compiledNetworkClassName = kv.getValue().getName();
            Class<?> aClass;
            try {
                aClass = rootClassLoader.loadClass(compiledNetworkClassName);
            } catch (ClassNotFoundException e) {
                throw new CouldNotCreateAlphaNetworkCompilerException(e);
            }
            kv.getValue().createInstanceAndSet(aClass);
        }
    }

    public static void generateAndSetInMemoryANC(KieBase kbase) {
        KieBaseUpdaterOptions kieBaseUpdaterOptions = new KieBaseUpdaterOptions(new KieBaseUpdaterOptions.OptionEntry(
                AlphaNetworkCompilerOption.class, AlphaNetworkCompilerOption.INMEMORY));
        KieBaseUpdatersContext context = new KieBaseUpdatersContext(kieBaseUpdaterOptions,
                                                                    ((InternalKnowledgeBase) kbase).getRete(), ((InternalKnowledgeBase) kbase).getRootClassLoader());
        new KieBaseUpdaterANC(context).run();
    }
}
