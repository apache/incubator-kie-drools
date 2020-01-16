/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.regression.executor;

import java.util.logging.Logger;

import org.kie.pmml.library.api.enums.PMMLModelType;
import org.kie.pmml.library.api.implementations.ModelImplementationProvider;
import org.kie.pmml.library.api.model.KiePMML;

/**
 * Default <code>AlgorithmImplementationProvider</code> for <b>Regression</b>
 */
public class RegressionAlgorithmImplementationProvider implements ModelImplementationProvider {

    private static final Logger log = Logger.getLogger(RegressionAlgorithmImplementationProvider.class.getName());

    @Override
    public PMMLModelType getPMMLModelType() {
        log.info("getPMMLModelType");
        return PMMLModelType.REGRESSION_MODEL;
    }

    @Override
    public KiePMML getKiePMMLModel(Object node) {
        log.info("getKiePMMLModel " + node);
        // TODO @gcardosi
        return new KiePMML();
    }
}
