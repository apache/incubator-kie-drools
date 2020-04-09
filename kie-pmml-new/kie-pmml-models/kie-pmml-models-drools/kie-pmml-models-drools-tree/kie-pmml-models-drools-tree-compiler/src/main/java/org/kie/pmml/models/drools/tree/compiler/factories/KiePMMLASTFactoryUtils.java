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
package org.kie.pmml.models.drools.tree.compiler.factories;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.drools.tree.model.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.models.drools.tree.model.enums.OPERATOR;

import static java.util.stream.Collectors.groupingBy;
import static org.kie.pmml.models.drools.commons.utils.KiePMMLDroolsModelUtils.getCorrectlyFormattedResult;

public class KiePMMLASTFactoryUtils {

    private KiePMMLASTFactoryUtils() {
        // not instatiate
    }

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>AND</code> or <code>OR</code>. Throws exception otherwise
     * @param compoundPredicate
     * @param fieldTypeMap
     * @return
     */
    public static List<KiePMMLFieldOperatorValue> getConstraintEntriesFromAndOrCompoundPredicate(final CompoundPredicate compoundPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        final List<Predicate> simplePredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof SimplePredicate).collect(Collectors.toList());
        if (!CompoundPredicate.BooleanOperator.AND.equals(compoundPredicate.getBooleanOperator()) &&
                !CompoundPredicate.BooleanOperator.OR.equals((compoundPredicate.getBooleanOperator()))) {
            throw new KiePMMLException(String.format("getConstraintEntriesFromAndOrCompoundPredicate invoked with %s CompoundPredicate", compoundPredicate.getBooleanOperator()));
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
                throw new IllegalStateException(String.format("CompoundPredicate.booleanOperator should never be %s at this point", compoundPredicate.getBooleanOperator()));
        }
        final List<KiePMMLFieldOperatorValue> nestedPredicates = new LinkedList<>();
        final List<Predicate> compoundPredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof CompoundPredicate).collect(Collectors.toList());
        compoundPredicates.forEach(nestedCompoundPredicate -> {
            switch (((CompoundPredicate) nestedCompoundPredicate).getBooleanOperator()) {
                case OR:
                case AND:
                    nestedPredicates.addAll(getConstraintEntriesFromAndOrCompoundPredicate((CompoundPredicate) nestedCompoundPredicate, fieldTypeMap));
                    break;
                default:
                    throw new IllegalStateException(String.format("CompoundPredicate.booleanOperator should never be %s at this point", compoundPredicate.getBooleanOperator()));
            }
        });
        if (!nestedPredicates.isEmpty()) {
            toReturn.add(new KiePMMLFieldOperatorValue(null, BOOLEAN_OPERATOR.byName(compoundPredicate.getBooleanOperator().value()).getCustomOperator(), Collections.emptyList(), nestedPredicates));
        }
        return toReturn;
    }

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>XOR</code>. Throws exception otherwise
     * @param compoundPredicate
     * @param fieldTypeMap
     * @return
     */
    public static List<KiePMMLFieldOperatorValue> getConstraintEntriesFromXOrCompoundPredicate(final CompoundPredicate compoundPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        if (!CompoundPredicate.BooleanOperator.XOR.equals(compoundPredicate.getBooleanOperator())) {
            throw new KiePMMLException(String.format("getConstraintEntriesFromXOrCompoundPredicate invoked with %s CompoundPredicate", compoundPredicate.getBooleanOperator()));
        }
        // Managing only SimplePredicates for the moment being
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

    /**
     * This method should be invoked with a <code>List&lt;SimplePredicate&gt;</code> where each <code>SimplePredicate</code> is referring to the same field
     * @param fieldName
     * @param containerOperator
     * @param simplePredicates
     * @param fieldTypeMap
     * @return
     */
    public static KiePMMLFieldOperatorValue getConstraintEntryFromSimplePredicates(final String fieldName, final String containerOperator, final List<SimplePredicate> simplePredicates, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = simplePredicates
                .stream()
                .map(simplePredicate -> new KiePMMLOperatorValue(OPERATOR.byName(simplePredicate.getOperator().value()).getOperator(),
                                                                 getCorrectlyFormattedObject(simplePredicate, fieldTypeMap)))
                .collect(Collectors.toList());
        return new KiePMMLFieldOperatorValue(fieldName, containerOperator, kiePMMLOperatorValues, null);
    }

    public static List<KiePMMLFieldOperatorValue> getXORConstraintEntryFromSimplePredicates(final List<Predicate> predicates, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return predicates.stream()
                .filter(predicate -> predicate instanceof SimplePredicate)
                .map(predicate -> {
                    SimplePredicate simplePredicate = (SimplePredicate) predicate;
                    String fieldName = fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType();
                    String operator = OPERATOR.byName(simplePredicate.getOperator().value()).getOperator();
                    Object value = getCorrectlyFormattedObject(simplePredicate, fieldTypeMap);
                    return new KiePMMLFieldOperatorValue(fieldName, null, Collections.singletonList(new KiePMMLOperatorValue(operator, value)), null);
                }).collect(Collectors.toList());
    }

    public static Object getCorrectlyFormattedObject(final SimplePredicate simplePredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        DATA_TYPE dataType = DATA_TYPE.byName(fieldTypeMap.get(simplePredicate.getField().getValue()).getOriginalType());
        return getCorrectlyFormattedResult(simplePredicate.getValue(), dataType);
    }
}
