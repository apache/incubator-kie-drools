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
package org.kie.pmml.commons.utils;

import java.util.Collection;
import java.util.stream.Collectors;

import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.pmml.api.PMMLContext;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLModelFactory;

import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedExecutableResource;
import static org.kie.pmml.commons.Constants.PMML_STRING;

public class PMMLLoaderUtils {

    private PMMLLoaderUtils() {
    }

    public static Collection<KiePMMLModelFactory> loadAllKiePMMLModelFactories(Collection<GeneratedExecutableResource> finalResources, PMMLContext pmmlContext) {
        return finalResources
                .stream().map(finalResource -> loadKiePMMLModelFactory(finalResource, pmmlContext))
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public static KiePMMLModelFactory loadKiePMMLModelFactory(FRI fri, PMMLContext pmmlContext) {
        GeneratedExecutableResource finalResource = getGeneratedExecutableResource(fri, PMML_STRING)
                .orElseThrow(() -> new KieRuntimeServiceException("Can not find expected GeneratedExecutableResource " +
                                                                          "for " + fri));
        return loadKiePMMLModelFactory(finalResource, pmmlContext);
    }

    public static KiePMMLModelFactory loadKiePMMLModelFactory(GeneratedExecutableResource finalResource,
                                                       PMMLContext pmmlContext) {
        try {
            String fullKiePMMLModelFactorySourceClassName = finalResource.getFullClassNames().get(0);
            final Class<? extends KiePMMLModelFactory> aClass =
                    (Class<? extends KiePMMLModelFactory>) pmmlContext.loadClass(fullKiePMMLModelFactorySourceClassName);
            return aClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new KieRuntimeServiceException(e);
        }
    }
}
