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

import org.dmg.pmml.NormContinuous;
import org.kie.pmml.api.enums.OUTLIER_TREATMENT_METHOD;
import org.kie.pmml.commons.model.expressions.KiePMMLLinearNorm;
import org.kie.pmml.commons.model.expressions.KiePMMLNormContinuous;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLLinearNormInstanceFactory.getKiePMMLLinearNorms;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLNormContinuous</code> instance
 * out of <code>NormContinuous</code>s
 */
public class KiePMMLNormContinuousInstanceFactory {

    private KiePMMLNormContinuousInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLNormContinuous getKiePMMLNormContinuous(final NormContinuous normContinuous) {
        final List<KiePMMLLinearNorm> linearNorms = normContinuous.hasLinearNorms() ?
                getKiePMMLLinearNorms(normContinuous.getLinearNorms()) : Collections.emptyList();
        final OUTLIER_TREATMENT_METHOD outlierTreatmentMethod = normContinuous.getOutliers() != null ? OUTLIER_TREATMENT_METHOD.byName(normContinuous.getOutliers().value()) : null;
        return new KiePMMLNormContinuous(normContinuous.getField(), getKiePMMLExtensions(normContinuous.getExtensions()), linearNorms, outlierTreatmentMethod, normContinuous.getMapMissingTo());
    }

}
