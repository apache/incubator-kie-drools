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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.models.drooled.tuples.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.tree.model.enums.OPERATOR;

public class KiePMMLASTFactoryUtils {

    private KiePMMLASTFactoryUtils() {
        // not instatiate
    }

    public static Map<String, List<KiePMMLOperatorValue>> getConstraintEntryFromSimplePredicates(final String fieldName, final List<SimplePredicate> simplePredicates, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        List<KiePMMLOperatorValue> operatorValues = simplePredicates.stream().map(simplePredicate -> {
            String operator = OPERATOR.byName(simplePredicate.getOperator().value()).getOperator();
            Object value = getCorrectlyFormattedObject(simplePredicate, fieldTypeMap);
            return new KiePMMLOperatorValue(operator, value);
        }).collect(Collectors.toList());
        return Collections.singletonMap(fieldName, operatorValues);
    }

    public static List<KiePMMLFieldOperatorValue> getXORConstraintEntryFromSimplePredicates(final List<Predicate> predicates, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return predicates.stream()
                .filter(predicate -> predicate instanceof SimplePredicate)
                .map(predicate -> {
                    SimplePredicate simplePredicate = (SimplePredicate) predicate;
                    String fieldName = fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType();
                    String operator = OPERATOR.byName(simplePredicate.getOperator().value()).getOperator();
                    Object value = getCorrectlyFormattedObject(simplePredicate, fieldTypeMap);
                    return new KiePMMLFieldOperatorValue(fieldName, operator, value);
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
