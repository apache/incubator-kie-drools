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
package org.kie.pmml.commons.utils;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.pmml.api.PMMLContext;
import org.kie.pmml.commons.model.KiePMMLModelFactory;

import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedExecutableResource;

public class PMMLLoaderUtils {

    private PMMLLoaderUtils() {
    }

    public static Collection<KiePMMLModelFactory> loadAllKiePMMLModelFactories(Collection<GeneratedExecutableResource> finalResources, PMMLContext pmmlContext) {
        return finalResources
                .stream().map(finalResource -> loadKiePMMLModelFactory(finalResource, pmmlContext))
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public static KiePMMLModelFactory loadKiePMMLModelFactory(ModelLocalUriId modelLocalUriId,
                                                              PMMLContext pmmlContext) {
        Optional<GeneratedExecutableResource> generatedExecutableResource = getGeneratedExecutableResource(modelLocalUriId, pmmlContext.getGeneratedResourcesMap());
        if (generatedExecutableResource.isPresent()) {
            return loadKiePMMLModelFactory(generatedExecutableResource.get(), pmmlContext);
        } else {
            throw new KieRuntimeServiceException("Can not find expected GeneratedExecutableResource " +
                                                             "for " + modelLocalUriId);
        }
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
