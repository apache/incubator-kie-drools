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
package org.kie.kogito.codegen.core.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.GeneratorFactory;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.ApplicationGenerator;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that performs automatic ApplicationGenerator discovery
 */
public class ApplicationGeneratorDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationGeneratorDiscovery.class);

    private ApplicationGeneratorDiscovery() {
        // utility class
    }

    public static ApplicationGenerator discover(KogitoBuildContext context) {
        ApplicationGenerator appGen = new ApplicationGenerator(context);
        loadGenerators(context).forEach(appGen::registerGeneratorIfEnabled);
        return appGen;
    }

    protected static Collection<Generator> loadGenerators(KogitoBuildContext context) {
        Collection<CollectedResource> collectedResources = CollectedResourceProducer.fromPaths(context.ignoreHiddenFiles(), context.getAppPaths().getPaths());

        ServiceLoader<GeneratorFactory> generatorFactories = ServiceLoader.load(GeneratorFactory.class, context.getClassLoader());

        List<Generator> generators = StreamSupport.stream(generatorFactories.spliterator(), false)
                .map(gf -> gf.create(context, collectedResources))
                .sorted(Comparator.comparingInt(Generator::priority))
                .collect(Collectors.toList());

        if (LOGGER.isInfoEnabled()) {
            String generatorMessages = generators.stream().map(Generator::name).collect(Collectors.joining(", "));
            LOGGER.info("Generator discovery performed, found [{}]", generatorMessages);
        }

        return generators;
    }
}
