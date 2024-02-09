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

import org.dmg.pmml.SimpleSetPredicate;
import org.kie.pmml.api.enums.ARRAY_TYPE;
import org.kie.pmml.api.enums.IN_NOTIN;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;

import static org.kie.pmml.compiler.api.utils.ModelUtils.getObjectsFromArray;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLSimpleSetPredicate</code> instance
 * out of <code>SimpleSetPredicate</code>s
 */
public class KiePMMLSimpleSetPredicateInstanceFactory {

    private KiePMMLSimpleSetPredicateInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLSimpleSetPredicate getKiePMMLSimpleSetPredicate(final SimpleSetPredicate simpleSetPredicate) {
        return KiePMMLSimpleSetPredicate.builder(simpleSetPredicate.getField(),
                                                 getKiePMMLExtensions(simpleSetPredicate.getExtensions()),
                                                 ARRAY_TYPE.byName(simpleSetPredicate.getArray().getType().value()),
                                                 IN_NOTIN.byName(simpleSetPredicate.getBooleanOperator().value()))
                .withValues(getObjectsFromArray(simpleSetPredicate.getArray()))
                .build();
    }
}
