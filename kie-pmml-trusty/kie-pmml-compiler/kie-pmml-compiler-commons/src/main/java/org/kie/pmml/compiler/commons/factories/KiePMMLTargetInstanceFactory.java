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

import org.dmg.pmml.Target;
import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.api.models.TargetValue;
import org.kie.pmml.commons.model.KiePMMLTarget;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTarget</code> instance
 * out of <code>Target</code>s
 */
public class KiePMMLTargetInstanceFactory {

    private KiePMMLTargetInstanceFactory() {
        // Avoid instantiation
    }

    public static KiePMMLTarget getKiePMMLTarget(final Target target) {
        final List<TargetValue> targetValues = target.hasTargetValues() ? target.getTargetValues()
                .stream()
                .map(KiePMMLTargetInstanceFactory::getKieTargetValue)
                .collect(Collectors.toList()) : Collections.emptyList();
        final OP_TYPE opType = target.getOpType() != null ? OP_TYPE.byName(target.getOpType().value()) : null;
        final String field = target.getField() != null ?target.getField() : null;
        final CAST_INTEGER castInteger = target.getCastInteger() != null ?
                CAST_INTEGER.byName(target.getCastInteger().value()) : null;
        TargetField targetField = new TargetField(targetValues,
                                                  opType,
                                                  field,
                                                  castInteger,
                                                  target.getMin(),
                                                  target.getMax(),
                                                  target.getRescaleConstant(),
                                                  target.getRescaleFactor());
        final KiePMMLTarget.Builder builder = KiePMMLTarget.builder(targetField.getName(), Collections.emptyList(),
                                                                    targetField);
        return builder.build();
    }

    private static TargetValue getKieTargetValue(org.dmg.pmml.TargetValue source) {
        final String value = source.getValue() != null ? source.getValue().toString() : null;
        final String displayValue = source.getDisplayValue() != null ? source.getDisplayValue() : null;
        return new TargetValue(value, displayValue, source.getPriorProbability(), source.getDefaultValue());
    }
}
