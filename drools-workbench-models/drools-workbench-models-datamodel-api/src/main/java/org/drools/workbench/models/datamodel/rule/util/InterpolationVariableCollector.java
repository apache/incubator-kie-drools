/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.datamodel.rule.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import org.drools.workbench.models.datamodel.rule.InterpolationVariable;

import static org.drools.workbench.models.datamodel.rule.RuleModel.DEFAULT_TYPE;

public class InterpolationVariableCollector {

    private final String defaultType;
    private final Map<Integer, InterpolationVariable> byColumn = new TreeMap<Integer, InterpolationVariable>();

    public InterpolationVariableCollector(final Map<InterpolationVariable, Integer> map,
                                          final String defaultType) {
        this.defaultType = defaultType;
        removeDuplicates(map);
    }

    public InterpolationVariableCollector(final Map<InterpolationVariable, Integer> map) {
        this(map,
             DEFAULT_TYPE);
    }

    private void removeDuplicates(Map<InterpolationVariable, Integer> map) {
        for (Map.Entry<InterpolationVariable, Integer> entry : map.entrySet()) {

            final InterpolationVariable newVar = entry.getKey();
            final Optional<InterpolationVariable> oldColumnData = findByVariable(byColumn,
                                                                                 newVar);

            if (oldColumnData.isPresent()) {
                if (isThereANeedToSetDefaultType(newVar, oldColumnData.get())) {
                    oldColumnData.get().setDataType(defaultType);
                }
            } else {
                InterpolationVariable variable = new InterpolationVariable(newVar.getVarName(),
                                                                           newVar.getDataType(),
                                                                           newVar.getFactType(),
                                                                           newVar.getFactField());
                variable.setOperator(newVar.getOperator());
                byColumn.put(entry.getValue(),
                             variable);
            }
        }
    }

    public boolean isThereANeedToSetDefaultType(final InterpolationVariable newVar,
                                                final InterpolationVariable oldVar) {
        if (Objects.equals(newVar.getDataType(), oldVar.getDataType())
                && Objects.equals(newVar.getFactType(), oldVar.getFactType())
                && Objects.equals(newVar.getFactField(), oldVar.getFactField())) {
            return false;
        } else {
            return true;
        }
    }

    private Optional<InterpolationVariable> findByVariable(final Map<Integer, InterpolationVariable> result,
                                                           final InterpolationVariable newVar) {
        for (final InterpolationVariable columnData : result.values()) {
            if (Objects.equals(columnData.getVarName(), newVar.getVarName())) {

                return Optional.of(columnData);
            }
        }

        return Optional.empty();
    }

    public Map<InterpolationVariable, Integer> getMap() {
        final HashMap<InterpolationVariable, Integer> result = new HashMap<InterpolationVariable, Integer>();

        int index = 0;
        for (Integer integer : byColumn.keySet()) {
            result.put(byColumn.get(integer),
                       index);
            index++;
        }

        return result;
    }
}
