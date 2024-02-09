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

import org.dmg.pmml.ParameterField;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLParameterField</code> instance
 * out of <code>ParameterField</code>s
 */
public class KiePMMLParameterFieldInstanceFactory {

    private KiePMMLParameterFieldInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLParameterField> getKiePMMLParameterFields(final List<ParameterField> parameterFields) {
        return parameterFields != null ? parameterFields.stream()
                .map(KiePMMLParameterFieldInstanceFactory::getKiePMMLParameterField)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    static KiePMMLParameterField getKiePMMLParameterField(final ParameterField parameterField) {
        DATA_TYPE dataType = parameterField.getDataType() != null ?
                DATA_TYPE.byName(parameterField.getDataType().value()) : null;
        OP_TYPE opType = parameterField.getOpType() != null ? OP_TYPE.byName(parameterField.getOpType().value()) : null;
        return KiePMMLParameterField.builder(parameterField.getName(),
                                             Collections.emptyList())
                .withDataType(dataType)
                .withOpType(opType)
                .withDisplayName(parameterField.getDisplayName())
                .build();
    }
}
