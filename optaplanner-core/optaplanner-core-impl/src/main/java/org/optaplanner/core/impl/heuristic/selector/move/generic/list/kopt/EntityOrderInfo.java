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

package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;

public final class EntityOrderInfo {
    final Object[] entities;
    final int[] offsets;
    final Map<Object, Integer> entityToEntityIndex;

    public <Node_> EntityOrderInfo(Node_[] pickedValues, SingletonInverseVariableSupply inverseVariableSupply,
            ListVariableDescriptor<?> listVariableDescriptor) {
        entityToEntityIndex = new IdentityHashMap<>();
        for (int i = 1; i < pickedValues.length && pickedValues[i] != null; i++) {
            entityToEntityIndex.computeIfAbsent(inverseVariableSupply.getInverseSingleton(pickedValues[i]),
                    entity -> entityToEntityIndex.size());
        }
        entities = new Object[entityToEntityIndex.size()];
        offsets = new int[entities.length];
        for (Map.Entry<Object, Integer> entry : entityToEntityIndex.entrySet()) {
            entities[entry.getValue()] = entry.getKey();
        }
        for (int i = 1; i < offsets.length; i++) {
            offsets[i] = offsets[i - 1] + listVariableDescriptor.getListSize(entities[i - 1]);
        }
    }

    public EntityOrderInfo(Object[] entities, Map<Object, Integer> entityToEntityIndex, int[] offsets) {
        this.entities = entities;
        this.entityToEntityIndex = entityToEntityIndex;
        this.offsets = offsets;
    }

    public <Node_> EntityOrderInfo withNewNode(Node_ node, ListVariableDescriptor<?> listVariableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply) {
        Object entity = inverseVariableSupply.getInverseSingleton(node);
        if (entityToEntityIndex.containsKey(entity)) {
            return this;
        } else {
            Object[] newEntities = Arrays.copyOf(entities, entities.length + 1);
            Map<Object, Integer> newEntityToEntityIndex = new IdentityHashMap<>(entityToEntityIndex);
            int[] newOffsets = Arrays.copyOf(offsets, offsets.length + 1);

            newEntities[entities.length] = entity;
            newEntityToEntityIndex.put(entity, entities.length);
            newOffsets[entities.length] =
                    offsets[entities.length - 1] + listVariableDescriptor.getListSize(entities[entities.length - 1]);
            return new EntityOrderInfo(newEntities, newEntityToEntityIndex, newOffsets);
        }
    }

    @SuppressWarnings("unchecked")
    public <Node_> Node_ successor(Node_ object, ListVariableDescriptor<?> listVariableDescriptor,
            IndexVariableSupply indexVariableSupply, SingletonInverseVariableSupply inverseVariableSupply) {
        Object entity = inverseVariableSupply.getInverseSingleton(object);
        int indexInEntityList = indexVariableSupply.getIndex(object);
        List<Object> listVariable = listVariableDescriptor.getListVariable(entity);
        if (indexInEntityList == listVariable.size() - 1) {
            int nextEntityIndex = (entityToEntityIndex.get(entity) + 1) % entities.length;
            return (Node_) listVariableDescriptor.getListVariable(entities[nextEntityIndex]).get(0);
        } else {
            return (Node_) listVariable.get(indexInEntityList + 1);
        }
    }

    @SuppressWarnings("unchecked")
    public <Node_> Node_ predecessor(Node_ object, ListVariableDescriptor<?> listVariableDescriptor,
            IndexVariableSupply indexVariableSupply, SingletonInverseVariableSupply inverseVariableSupply) {
        Object entity = inverseVariableSupply.getInverseSingleton(object);
        int indexInEntityList = indexVariableSupply.getIndex(object);
        List<Object> listVariable = listVariableDescriptor.getListVariable(entity);
        if (indexInEntityList == 0) {
            // add entities.length to ensure modulo result is positive
            int previousEntityIndex = (entityToEntityIndex.get(entity) - 1 + entities.length) % entities.length;
            listVariable = listVariableDescriptor.getListVariable(entities[previousEntityIndex]);
            return (Node_) listVariable.get(listVariable.size() - 1);
        } else {
            return (Node_) listVariable.get(indexInEntityList - 1);
        }
    }

    public <Node_> boolean between(Node_ start, Node_ middle, Node_ end, IndexVariableSupply indexVariableSupply,
            SingletonInverseVariableSupply inverseVariableSupply) {
        int startEntityIndex = entityToEntityIndex.get(inverseVariableSupply.getInverseSingleton(start));
        int middleEntityIndex = entityToEntityIndex.get(inverseVariableSupply.getInverseSingleton(middle));
        int endEntityIndex = entityToEntityIndex.get(inverseVariableSupply.getInverseSingleton(end));

        int startIndex = indexVariableSupply.getIndex(start) + offsets[startEntityIndex];
        int middleIndex = indexVariableSupply.getIndex(middle) + offsets[middleEntityIndex];
        int endIndex = indexVariableSupply.getIndex(end) + offsets[endEntityIndex];

        if (startIndex <= endIndex) {
            // test middleIndex in [startIndex, endIndex]
            return startIndex <= middleIndex && middleIndex <= endIndex;
        } else {
            // test middleIndex in [0, endIndex] or middleIndex in [startIndex, listSize)
            return middleIndex >= startIndex || middleIndex <= endIndex;
        }
    }
}
