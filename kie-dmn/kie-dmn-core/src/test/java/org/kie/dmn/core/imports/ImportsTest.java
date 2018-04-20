/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.imports;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class ImportsTest {

    public static final Logger LOG = LoggerFactory.getLogger(ImportsTest.class);

    @Test
    public void testImportDependenciesForDTInAContext() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Imported_Model.dmn",
                                                                                 this.getClass(),
                                                                                 "Import_BKM_and_have_a_Decision_Ctx_with_DT.dmn");

        DMNModel importedModel = runtime.getModel("http://www.trisotech.com/definitions/_f27bb64b-6fc7-4e1f-9848-11ba35e0df36",
                                                  "Imported Model");
        assertThat(importedModel, notNullValue());
        for (DMNMessage message : importedModel.getMessages()) {
            LOG.debug("1 {}", message);
        }

        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_c3e08836-7973-4e4d-af2b-d46b23725c13",
                                             "Import BKM and have a Decision Ctx with DT");
        assertThat(dmnModel, notNullValue());
        for (DMNMessage message : dmnModel.getMessages()) {
            LOG.debug("2 {}", message);
        }

        DMNContext context = runtime.newContext();
        context.set("A Person", mapOf(entry("name", "John"), entry("age", 47)));

        DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
        for (DMNMessage message : evaluateAll.getMessages()) {
            LOG.debug("e {}", message);
        }
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("A Decision Ctx with DT").getResult(), is("Respectfully, Hello John!"));
    }

}