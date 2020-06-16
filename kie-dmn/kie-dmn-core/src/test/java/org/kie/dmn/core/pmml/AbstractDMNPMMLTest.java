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
package org.kie.dmn.core.pmml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.api.pmml.PMMLConstants.KIE_PMML_IMPLEMENTATION;
import static org.kie.dmn.core.util.DMNRuntimeUtil.resetServices;

public abstract class AbstractDMNPMMLTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDMNPMMLTest.class);

    protected void resetEnvironment(String pmmlVersion) {
        LOG.debug("resetEnvironment {}", pmmlVersion);
        System.setProperty(KIE_PMML_IMPLEMENTATION.getName(), pmmlVersion);
        resetServices();
    }
}
