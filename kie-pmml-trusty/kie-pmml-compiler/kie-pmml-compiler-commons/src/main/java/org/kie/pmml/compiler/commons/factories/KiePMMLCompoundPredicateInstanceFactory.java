/**
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
package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Field;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateInstanceFactory.getKiePMMLPredicates;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLCompoundPredicate</code> instance
 * out of <code>CompoundPredicate</code>s
 */
public class KiePMMLCompoundPredicateInstanceFactory {

    private KiePMMLCompoundPredicateInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLCompoundPredicate getKiePMMLCompoundPredicate(final CompoundPredicate compoundPredicate,
                                                                final List<Field<?>> fields) {
        final BOOLEAN_OPERATOR booleanOperator =
                BOOLEAN_OPERATOR.byName(compoundPredicate.getBooleanOperator().value());
        final List<KiePMMLPredicate> kiePMMLPredicates = compoundPredicate.hasPredicates() ?
                getKiePMMLPredicates(compoundPredicate.getPredicates(), fields) : Collections.emptyList();
        return KiePMMLCompoundPredicate.builder(getKiePMMLExtensions(compoundPredicate.getExtensions()),
                                                booleanOperator)
                .withKiePMMLPredicates(kiePMMLPredicates)
                .build();
    }
}
