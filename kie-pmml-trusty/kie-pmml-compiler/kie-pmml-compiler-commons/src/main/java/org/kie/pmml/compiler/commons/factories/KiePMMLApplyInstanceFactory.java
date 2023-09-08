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
import java.util.UUID;

import org.dmg.pmml.Apply;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpressions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLApply</code> instance
 * out of <code>Apply</code>s
 */
public class KiePMMLApplyInstanceFactory {

    private KiePMMLApplyInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLApply getKiePMMLApply(final Apply apply) {
        final String invalidValueTreatment = apply.getInvalidValueTreatment() != null ?
                apply.getInvalidValueTreatment().value() : null;
        final List<KiePMMLExpression> kiePMMLExpressions = getKiePMMLExpressions(apply.getExpressions());
        final KiePMMLApply.Builder builder = KiePMMLApply.builder(UUID.randomUUID().toString(),
                                                                  getKiePMMLExtensions(apply.getExtensions()),
                                                                  apply.getFunction())
                .withKiePMMLExpressions(kiePMMLExpressions)
                .withMapMissingTo(apply.getMapMissingTo())
                .withDefaultValue(apply.getDefaultValue())
                .withInvalidValueTreatmentMethod(invalidValueTreatment);
        return builder.build();
    }

}
