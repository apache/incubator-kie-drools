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
package org.kie.drl.engine.runtime.model;

import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoInput;

/**
 * Generic <code>EfestoInput</code> specific for DRL engines, to be subclassed depending on the actual implementation needs
 * @param <T>
 */
public abstract class EfestoInputDrl<T>  extends AbstractEfestoInput<T> {

    // TODO {mfusco} Define a generic (instead of "String") that could reasonably contain any given "input" for rule execution
    protected EfestoInputDrl(FRI fri, T inputData) {
        super(fri, inputData);
    }


}
