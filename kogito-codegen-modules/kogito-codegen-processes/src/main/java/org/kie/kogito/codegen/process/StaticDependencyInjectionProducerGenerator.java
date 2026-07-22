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
package org.kie.kogito.codegen.process;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

public class StaticDependencyInjectionProducerGenerator {

    private final KogitoBuildContext context;
    //All available Producer templates for dependency injection
    private static final List<String> producerTemplates = Arrays.asList("ProcessServiceProducer");

    private StaticDependencyInjectionProducerGenerator(
            KogitoBuildContext context) {
        this.context = context;
    }

    public static StaticDependencyInjectionProducerGenerator of(KogitoBuildContext context) {
        return new StaticDependencyInjectionProducerGenerator(context);
    }

    /**
     * Key is the FilePath, Value is the content
     *
     * @return Map with the generated resources
     */
    public Map<String, String> generate() {
        return generate(producerTemplates);
    }

    /**
     * Key is the FilePath, Value is the content
     *
     * @return Map with the generated resources
     */
    public Map<String, String> generate(List<String> templates) {
        if (!context.hasDI()) {
            return Collections.emptyMap();
        }
        return templates.stream().map(this::buildProducerTemplatedGenerator)
                .collect(Collectors.toMap(TemplatedGenerator::generatedFilePath,
                        generator -> generator.compilationUnitOrThrow().toString()));
    }

    private TemplatedGenerator buildProducerTemplatedGenerator(String template) {
        return TemplatedGenerator.builder()
                .withTemplateBasePath("/class-templates/producer/")
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .withTargetTypeName(template)
                .build(context, template);
    }
}
