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
import java.util.stream.Collectors;

import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Field;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;

import static org.kie.pmml.compiler.api.utils.ModelUtils.getDataType;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpression;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLDerivedField</code> instance
 * out of <code>DerivedField</code>s
 */
public class KiePMMLDerivedFieldInstanceFactory {

    private KiePMMLDerivedFieldInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLDerivedField> getKiePMMLDerivedFields(final List<DerivedField> derivedFields,
                                                            final List<Field<?>> fields) {
        return derivedFields != null ? derivedFields.stream()
                .map(derivedField -> KiePMMLDerivedFieldInstanceFactory.getKiePMMLDerivedField(derivedField, fields))
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    static KiePMMLDerivedField getKiePMMLDerivedField(final DerivedField derivedField,
                                                      final List<Field<?>> fields) {
        DataType dataType = derivedField.getDataType() != null ? derivedField.getDataType() : getDataType(fields,derivedField.getName());
        OP_TYPE opType = derivedField.getOpType() != null ? OP_TYPE.byName(derivedField.getOpType().value()) : null;
        return KiePMMLDerivedField.builder(derivedField.getName(),
                                           getKiePMMLExtensions(derivedField.getExtensions()),
                                           DATA_TYPE.byName(dataType.value()),
                                           opType,
                                           getKiePMMLExpression(derivedField.getExpression()))
                .withDisplayName(derivedField.getDisplayName())
                .build();
    }
}
