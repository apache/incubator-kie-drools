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
package org.kie.efesto.runtimemanager.api.service;

import java.util.Optional;

import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;

/**
 * The compilation-related interface to be implemented by engine-plugin.
 * It will be looked for with SPI, so each engine should declare that implementation inside
 * <code>src/main/resources/META-INF/services/org.kie.efesto.runtimemanager.api.service.KieRuntimeService</code> file
 */
public interface KieRuntimeService<S, U, T extends EfestoInput<S>, E extends EfestoOutput<U>, K extends EfestoRuntimeContext> {

    EfestoClassKey getEfestoClassKeyIdentifier();
    /**
     * Every engine is responsible to verify if it can evaluate a result with the resource of the given <code>T</code>
     * (that contains a specific <code>LocalUri</code>)
     *
     * @param toEvaluate
     * @param context
     * @return
     */
    boolean canManageInput(EfestoInput toEvaluate, K context);

    /**
     * Produce one <code>EfestoOutput</code> from the given <code>EfestoInput</code>
     *
     * @param toEvaluate
     * @param context
     * @return
     */
    Optional<E> evaluateInput(T toEvaluate, K context);

    /**
     * Return the model type that the RuntimeService handles
     *
     * @return model type
     */
    String getModelType();
}
