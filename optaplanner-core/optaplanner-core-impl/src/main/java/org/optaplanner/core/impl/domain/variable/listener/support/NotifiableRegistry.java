/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.listener.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;

final class NotifiableRegistry<Solution_> {

    private final List<Notifiable> notifiableList = new ArrayList<>();
    private final Map<EntityDescriptor<?>, Set<EntityNotifiable<Solution_>>> sourceEntityToNotifiableMap =
            new LinkedHashMap<>();
    private final Map<VariableDescriptor<?>, List<VariableListenerNotifiable<Solution_>>> sourceVariableToNotifiableMap =
            new LinkedHashMap<>();
    private final Map<VariableDescriptor<?>, List<ListVariableListenerNotifiable<Solution_>>> sourceListVariableToNotifiableMap =
            new LinkedHashMap<>();

    NotifiableRegistry(SolutionDescriptor<Solution_> solutionDescriptor) {
        for (EntityDescriptor<Solution_> entityDescriptor : solutionDescriptor.getEntityDescriptors()) {
            sourceEntityToNotifiableMap.put(entityDescriptor, new LinkedHashSet<>());
            for (VariableDescriptor<Solution_> variableDescriptor : entityDescriptor.getDeclaredVariableDescriptors()) {
                if (variableDescriptor.isGenuineListVariable()) {
                    sourceListVariableToNotifiableMap.put(variableDescriptor, new ArrayList<>());
                } else {
                    sourceVariableToNotifiableMap.put(variableDescriptor, new ArrayList<>());
                }
            }
        }
    }

    void registerNotifiable(VariableDescriptor<Solution_> source, EntityNotifiable<Solution_> notifiable) {
        registerNotifiable(Collections.singletonList(source), notifiable);
    }

    void registerNotifiable(Collection<VariableDescriptor<Solution_>> sources, EntityNotifiable<Solution_> notifiable) {
        for (VariableDescriptor<?> source : sources) {
            if (source.isGenuineListVariable()) {
                sourceListVariableToNotifiableMap.get(source).add(((ListVariableListenerNotifiable<Solution_>) notifiable));
            } else {
                sourceVariableToNotifiableMap.get(source).add(((VariableListenerNotifiable<Solution_>) notifiable));
            }
            sourceEntityToNotifiableMap.get(source.getEntityDescriptor()).add(notifiable);
        }
        notifiableList.add(notifiable);
    }

    Iterable<Notifiable> getAll() {
        return notifiableList;
    }

    Collection<EntityNotifiable<Solution_>> get(EntityDescriptor<?> entityDescriptor) {
        return sourceEntityToNotifiableMap.get(entityDescriptor);
    }

    Collection<VariableListenerNotifiable<Solution_>> get(VariableDescriptor<?> variableDescriptor) {
        return sourceVariableToNotifiableMap.getOrDefault(variableDescriptor,
                Collections.emptyList()); // Avoids null for chained swap move on an unchained var.
    }

    Collection<ListVariableListenerNotifiable<Solution_>> get(ListVariableDescriptor<?> variableDescriptor) {
        return sourceListVariableToNotifiableMap.getOrDefault(variableDescriptor,
                Collections.emptyList()); // Avoids null for chained swap move on an unchained var.
    }
}
