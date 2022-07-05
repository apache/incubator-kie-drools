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
package org.kie.efesto.runtimemanager.api.service;

import java.util.Collection;

import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.memorycompiler.KieMemoryCompiler;

public interface RuntimeManager {

    /**
     * Produce a <code>List&lt;EfestoOutput&gt;</code> from the given <code>List&lt;EfestoInput&gt;</code>
     * @param toEvaluate
     * @param memoryCompilerClassLoader
     * @return
     */
    Collection<EfestoOutput> evaluateInput(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader,
                                           EfestoInput... toEvaluate);

}
