/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.codegen.decision;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.io.Resource;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;

class DecisionCodegenUtilsTest {

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void loadModelsAndValidate(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        final Collection<CollectedResource> cResources = CollectedResourceProducer.fromPaths(
                Paths.get("src/test/resources/decision/models/vacationDays").toAbsolutePath());
        Map<Resource, CollectedResource> r2cr = cResources.stream().collect(Collectors.toMap(CollectedResource::resource, Function.identity()));
        Map.Entry<String, GeneratedResources> retrieved = DecisionCodegenUtils.loadModelsAndValidate(context, r2cr, Collections.emptySet(), new RuntimeTypeCheckOption(false));
        System.out.println(retrieved.getKey());
    }

}
