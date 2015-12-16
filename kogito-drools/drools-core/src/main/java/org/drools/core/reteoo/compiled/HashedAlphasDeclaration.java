/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo.compiled;

import org.drools.core.base.ValueType;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;

/**
 * This class is used to hold information for Hashed {@link org.kie.reteoo.AlphaNode}s for generated subclasses
 * of {@link CompiledNetwork}.
 *
 * @see org.kie.reteoo.compiled.DeclarationsHandler
 */
public class HashedAlphasDeclaration {
    private final String variableName;
    private final ValueType valueType;

    /**
     * This map contains keys which are different values of the same field and the node id that of the
     * {@link org.kie.common.NetworkNode} the value is from.
     */
    private final Map<Object, String> hashedValuesToNodeIds = new HashMap<Object, String>();

    HashedAlphasDeclaration(String variableName,ValueType valueType) {
        this.variableName = variableName;
        this.valueType = valueType;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getVariableName() {
        return variableName;
    }

    void add(Object hashedValue, String nodeId) {
        hashedValuesToNodeIds.put(hashedValue,  nodeId);
    }

    public Collection<Object> getHashedValues() {
        return Collections.unmodifiableSet(hashedValuesToNodeIds.keySet());
    }

    public String getNodeId(Object hashedValue) {
        return hashedValuesToNodeIds.get(hashedValue);
    }
}
