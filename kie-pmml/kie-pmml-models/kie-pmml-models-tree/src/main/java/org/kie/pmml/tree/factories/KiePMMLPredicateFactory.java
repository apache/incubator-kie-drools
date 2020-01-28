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
package org.kie.pmml.tree.factories;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.tree.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.model.tree.enums.OPERATOR;
import org.kie.pmml.api.model.tree.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.api.model.tree.predicates.KiePMMLPredicate;
import org.kie.pmml.api.model.tree.predicates.KiePMMLSimplePredicate;

import static org.kie.pmml.api.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;

public class KiePMMLPredicateFactory {

    private static final Logger log = Logger.getLogger(KiePMMLPredicateFactory.class.getName());

    private KiePMMLPredicateFactory() {
    }

    public static List<KiePMMLPredicate> getPredicates(List<Predicate> predicates)throws KiePMMLException {
        log.info("getPredicates " + predicates);
        return predicates.stream().map(throwingFunctionWrapper(KiePMMLPredicateFactory::getPredicate)).collect(Collectors.toList());
    }

    public static KiePMMLPredicate getPredicate(Predicate predicate) throws KiePMMLException {
        log.info("getPredicate " + predicate);
        if (predicate instanceof SimplePredicate) {
            return getKiePMMLSimplePredicate((SimplePredicate) predicate);
        } else if (predicate instanceof CompoundPredicate) {
            return getKiePMMLCompoundPredicate((CompoundPredicate) predicate);
        } else {
            throw new KiePMMLException("Predicate of type " + predicate.getClass().getName() + " not managed, yet");
        }
    }

    public static KiePMMLSimplePredicate getKiePMMLSimplePredicate(SimplePredicate predicate) throws KieEnumException {
        return KiePMMLSimplePredicate.builder(predicate.getField().getValue(), Collections.emptyList(), OPERATOR.byName(predicate.getOperator().value()))
                .withValue(predicate.getValue())
                .build();
    }

    public static KiePMMLCompoundPredicate getKiePMMLCompoundPredicate(CompoundPredicate predicate) throws KiePMMLException {
        return KiePMMLCompoundPredicate.builder(Collections.emptyList(), BOOLEAN_OPERATOR.byName(predicate.getBooleanOperator().value()))
                .withKiePMMLPredicates(getPredicates(predicate.getPredicates()))
                .build();
    }
}
