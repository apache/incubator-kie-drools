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

import org.dmg.pmml.DefineFunction;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpression;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLParameterFieldInstanceFactory.getKiePMMLParameterFields;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLDefineFunction</code> instance
 * out of <code>DefineFunction</code>s
 */
public class KiePMMLDefineFunctionInstanceFactory {

    private KiePMMLDefineFunctionInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLDefineFunction> getKiePMMLDefineFunctions(final List<DefineFunction> defineFunctions) {
        return defineFunctions != null ? defineFunctions.stream()
                .map(KiePMMLDefineFunctionInstanceFactory::getKiePMMLDefineFunction)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    static KiePMMLDefineFunction getKiePMMLDefineFunction(final DefineFunction defineFunction) {
        final List<KiePMMLParameterField> kiePMMLParameterFields =
                getKiePMMLParameterFields(defineFunction.getParameterFields());
        DATA_TYPE dataType = defineFunction.getDataType() != null ?
                DATA_TYPE.byName(defineFunction.getDataType().value()) : null;
        OP_TYPE opType = defineFunction.getOpType() != null ? OP_TYPE.byName(defineFunction.getOpType().value()) : null;
        return new KiePMMLDefineFunction(defineFunction.getName(),
                                         getKiePMMLExtensions(defineFunction.getExtensions()),
                                         dataType,
                                         opType,
                                         kiePMMLParameterFields,
                                         getKiePMMLExpression(defineFunction.getExpression()));
    }
}