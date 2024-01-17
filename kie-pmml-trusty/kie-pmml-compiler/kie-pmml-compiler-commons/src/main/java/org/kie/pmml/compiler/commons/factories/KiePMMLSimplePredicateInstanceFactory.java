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

import java.util.List;

import org.dmg.pmml.DataType;
import org.dmg.pmml.Field;
import org.dmg.pmml.SimplePredicate;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;

import static org.kie.pmml.compiler.api.utils.ModelUtils.getDataType;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLSimplePredicate</code> instance
 * out of <code>SimplePredicate</code>s
 */
public class KiePMMLSimplePredicateInstanceFactory {

    private KiePMMLSimplePredicateInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLSimplePredicate getKiePMMLSimplePredicate(final SimplePredicate simplePredicate,
                                                            final List<Field<?>> fields) {
        final List<KiePMMLExtension> kiePMMLExtensions = getKiePMMLExtensions(simplePredicate.getExtensions());
        DataType dataType = getDataType(fields,simplePredicate.getField());
        Object value = DATA_TYPE.byName(dataType.value()).getActualValue(simplePredicate.getValue());
        return KiePMMLSimplePredicate.builder(simplePredicate.getField(),
                                              kiePMMLExtensions,
                                              OPERATOR.byName(simplePredicate.getOperator().value()))
                .withValue(value)
                .build();
    }
}
