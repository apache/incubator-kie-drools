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
package org.kie.efesto.runtimemanager.core.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.runtimemanager.api.utils.SPIUtils.getKieRuntimeService;
import static org.kie.efesto.runtimemanager.api.utils.SPIUtils.getKieRuntimeServiceFromEfestoRuntimeContext;

public class RuntimeManagerImpl implements RuntimeManager {
    private static final Logger logger = LoggerFactory.getLogger(RuntimeManagerImpl.class.getName());

    @Override
    public Collection<EfestoOutput> evaluateInput(EfestoRuntimeContext context, EfestoInput... toEvaluate) {
        if (toEvaluate.length == 1) { // minor optimization for the (most typical) case with 1 input
            return getOptionalOutput(context, toEvaluate[0]).map(Collections::singletonList).orElse(Collections.emptyList());
        }
        return Arrays.stream(toEvaluate)
                .flatMap(input -> getOptionalOutput(context, input).map(Stream::of).orElse(Stream.empty()))
                .collect(Collectors.toList());
    }

    private Optional<EfestoOutput> getOptionalOutput(EfestoRuntimeContext context, EfestoInput input) {
        Optional<KieRuntimeService> retrieved = getKieRuntimeService(input, false, context);
        if (!retrieved.isPresent()) {
            logger.warn("Cannot find KieRuntimeService for {}, looking inside context classloader", input.getFRI());
            retrieved = getKieRuntimeServiceFromEfestoRuntimeContext(input, context);
        }
        if (!retrieved.isPresent()) {
            logger.warn("Cannot find KieRuntimeService for {}", input.getFRI());
            return Optional.empty();
        }
        return retrieved.flatMap(kieRuntimeService -> kieRuntimeService.evaluateInput(input, context));
    }
}
