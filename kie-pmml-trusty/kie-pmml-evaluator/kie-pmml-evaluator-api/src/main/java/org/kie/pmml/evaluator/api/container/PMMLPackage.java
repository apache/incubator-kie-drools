/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.evaluator.api.container;

import java.util.Collection;
import java.util.Map;

import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 *
 */
public interface PMMLPackage extends ResourceTypePackage<KiePMMLModel> {

    KiePMMLModel getModelByName(String name);

    Map<String, KiePMMLModel> getAllModels();

    void addAll(Collection<KiePMMLModel> toAdd);
}
