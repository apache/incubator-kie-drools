/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.pmml.openapi.factories;

import org.kie.kogito.pmml.openapi.api.PMMLOASResult;
import org.kie.kogito.pmml.openapi.impl.PMMLOASResultImpl;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * Static <code>PMMLOASResult</code> factory
 */
public class PMMLOASResultFactory {

    private PMMLOASResultFactory() {
        // avoid instantiation
    }

    public static PMMLOASResult getPMMLOASResult(KiePMMLModel source) {
        PMMLOASResultImpl.Builder builder = new PMMLOASResultImpl.Builder();
        if (source.getMiningFields() != null) {
            builder = builder.withMiningFields(source.getMiningFields());
        }
        if (source.getOutputFields() != null) {
            builder = builder.withOutputFields(source.getOutputFields());
        }
        return builder.build();
    }
}
