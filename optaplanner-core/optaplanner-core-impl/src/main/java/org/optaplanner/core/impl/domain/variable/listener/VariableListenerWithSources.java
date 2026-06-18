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

package org.optaplanner.core.impl.domain.variable.listener;

import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;

/**
 * Holds a variable listener and all its source variable descriptors.
 *
 * @param <Solution_>
 */
public final class VariableListenerWithSources<Solution_> {

    private final AbstractVariableListener<Solution_, Object> variableListener;
    private final Collection<VariableDescriptor<Solution_>> sourceVariableDescriptors;

    public VariableListenerWithSources(AbstractVariableListener<Solution_, Object> variableListener,
            Collection<VariableDescriptor<Solution_>> sourceVariableDescriptors) {
        this.variableListener = variableListener;
        this.sourceVariableDescriptors = sourceVariableDescriptors;
    }

    public VariableListenerWithSources(AbstractVariableListener<Solution_, Object> variableListener,
            VariableDescriptor<Solution_> sourceVariableDescriptor) {
        this(variableListener, Collections.singleton(sourceVariableDescriptor));
    }

    public AbstractVariableListener<Solution_, Object> getVariableListener() {
        return variableListener;
    }

    public Collection<VariableDescriptor<Solution_>> getSourceVariableDescriptors() {
        return sourceVariableDescriptors;
    }

    public Collection<VariableListenerWithSources<Solution_>> toCollection() {
        return Collections.singleton(this);
    }
}
