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
package org.kie.pmml.models.tree.compiler.factories;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.models.drooled.tuples.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.tree.model.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.models.tree.model.enums.OPERATOR;

import static java.util.stream.Collectors.groupingBy;

public class KiePMMLASTFactoryUtils {

    private KiePMMLASTFactoryUtils() {
        // not instatiate
    }

    public static List<KiePMMLFieldOperatorValue> getConstraintEntriesFromAndOrCompoundPredicate(final CompoundPredicate compoundPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        // Managing only SimplePredicates for the moment being
        final List<Predicate> simplePredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof SimplePredicate).collect(Collectors.toList());
        if (!CompoundPredicate.BooleanOperator.AND.equals(compoundPredicate.getBooleanOperator()) &&
                !CompoundPredicate.BooleanOperator.OR.equals((compoundPredicate.getBooleanOperator()))) {
            throw new KiePMMLException("getConstraintEntriesFromAndOrCompoundPredicate invoked with a " + compoundPredicate.getBooleanOperator() + " CompoundPredicate");
        }
        final Map<String, List<SimplePredicate>> predicatesByField = simplePredicates.stream()
                .map(child -> (SimplePredicate) child)
                .collect(groupingBy(child -> fieldTypeMap.get(child.getField().getValue()).getGeneratedType()));
        final List<KiePMMLFieldOperatorValue> toReturn = new LinkedList<>();
        switch (compoundPredicate.getBooleanOperator()) {
            case AND:
                predicatesByField.forEach((fieldName, predicates) -> toReturn.add(getConstraintEntryFromSimplePredicates(fieldName, "&&", predicates, fieldTypeMap)));
                break;
            case OR:
                predicatesByField.forEach((fieldName, predicates) -> toReturn.add(getConstraintEntryFromSimplePredicates(fieldName, "||", predicates, fieldTypeMap)));
                break;
            default:
                break;
        }
        final List<KiePMMLFieldOperatorValue> nestedPredicates = new LinkedList<>();
        final List<Predicate> compoundPredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof CompoundPredicate).collect(Collectors.toList());
        compoundPredicates.forEach(nestedCompoundPredicate -> {
            switch (((CompoundPredicate) nestedCompoundPredicate).getBooleanOperator()) {
                case OR:
                case AND:
                    nestedPredicates.addAll(getConstraintEntriesFromAndOrCompoundPredicate((CompoundPredicate) nestedCompoundPredicate, fieldTypeMap));
                    break;
                case XOR:
                    nestedPredicates.addAll(getConstraintEntriesFromXOrCompoundPredicate((CompoundPredicate) nestedCompoundPredicate, fieldTypeMap));
                    break;
                default:
                    // noop
            }
        });
        if (!nestedPredicates.isEmpty()) {
            toReturn.add(new KiePMMLFieldOperatorValue(null, BOOLEAN_OPERATOR.byName(compoundPredicate.getBooleanOperator().value()).getCustomOperator(), Collections.EMPTY_MAP,  nestedPredicates));

        }
        return toReturn;
    }

    public static List<KiePMMLFieldOperatorValue> getConstraintEntriesFromXOrCompoundPredicate(final CompoundPredicate compoundPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        // Managing only SimplePredicates for the moment being
        if (!CompoundPredicate.BooleanOperator.XOR.equals(compoundPredicate.getBooleanOperator())) {
            throw new KiePMMLException("getConstraintEntriesFromXOrCompoundPredicate invoked with a " + compoundPredicate.getBooleanOperator() + " CompoundPredicate");
        }
        final List<Predicate> simplePredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof SimplePredicate).collect(Collectors.toList());
        if (simplePredicates.size() < 2) {
            throw new KiePMMLException("At least two elements expected for XOR operations");
        }
        if (simplePredicates.size() > 2) {
            // Not managed yet
            throw new KiePMMLException("More then two elements not managed, yet, for XOR operations");
        }
        return getXORConstraintEntryFromSimplePredicates(simplePredicates, fieldTypeMap);
    }

    public static KiePMMLFieldOperatorValue getConstraintEntryFromSimplePredicates(final String fieldName, final String containerOperator, final List<SimplePredicate> simplePredicates, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        LinkedHashMap<String, Object> operatorValues = simplePredicates.stream().collect(Collectors.toMap(simplePredicate -> OPERATOR.byName(simplePredicate.getOperator().value()).getOperator(),
                                                                                                          simplePredicate -> getCorrectlyFormattedObject(simplePredicate, fieldTypeMap),
                                                                                                          (o1, o2) -> o1,
                                                                                                          LinkedHashMap::new));
        return new KiePMMLFieldOperatorValue(fieldName, containerOperator, operatorValues, null);
    }

    public static List<KiePMMLFieldOperatorValue> getXORConstraintEntryFromSimplePredicates(final List<Predicate> predicates, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return predicates.stream()
                .filter(predicate -> predicate instanceof SimplePredicate)
                .map(predicate -> {
                    SimplePredicate simplePredicate = (SimplePredicate) predicate;
                    String fieldName = fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType();
                    String operator = OPERATOR.byName(simplePredicate.getOperator().value()).getOperator();
                    Object value = getCorrectlyFormattedObject(simplePredicate, fieldTypeMap);
                    return new KiePMMLFieldOperatorValue(fieldName, null, Collections.singletonMap(operator, value), null);
                }).collect(Collectors.toList());
    }

    public static Object getCorrectlyFormattedObject(final SimplePredicate simplePredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        DATA_TYPE dataType = DATA_TYPE.byName(fieldTypeMap.get(simplePredicate.getField().getValue()).getOriginalType());
        return getCorrectlyFormattedResult(simplePredicate.getValue(), dataType);
    }

    public static Object getCorrectlyFormattedResult(Object rawValue, DATA_TYPE targetType) {
        Object toReturn = targetType.getActualValue(rawValue);
        if (DATA_TYPE.STRING.equals(targetType)) {
            toReturn = "\"" + toReturn + "\"";
        }
        return toReturn;
    }
}
