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
package org.kie.pmml.compiler.api.provider;

import java.util.List;

import org.dmg.pmml.Model;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * Actual implementation is required to retrieve a
 * <code>List&lt;ModelImplementationProvider&gt;</code> out from the classes found in the classpath
 */
public interface ModelImplementationProviderFinder {

    /**
     * Retrieve all the <code>ModelImplementationProvider</code> implementations in the classpath
     * @param refresh pass <code>true</code> to reload classes from classpath; <code>false</code> to use cached ones
     * @return
     */
    <T extends Model, E extends KiePMMLModel> List<ModelImplementationProvider<T, E>> getImplementations(boolean refresh);
}
