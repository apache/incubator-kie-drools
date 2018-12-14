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

package org.kie.dmn.core.v1_2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;
import org.kie.dmn.feel.util.SpaceInsensitiveBuilder;
import org.kie.dmn.feel.util.SpaceInsensitiveBuilder.SpacesAndStringParts;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.InputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class SpaceInsensitivityTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(SpaceInsensitivityTest.class);

    public SpaceInsensitivityTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testSpaceInsensitivityBase() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("spaceinsensitivity.dmn", this.getClass());
        checkSpaceInsensitivityModel(runtime);
    }

    private void checkSpaceInsensitivityModel(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_abd715a0-c39f-4232-9b08-53eff5232d82", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("my person", mapOf(entry("first name", "John")));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("my decision"), is("Hello, John"));
    }

    @Test
    public void testSpaceInsensitivityVariations() throws Exception {
        try (final Reader reader = new InputStreamReader(getClass().getResourceAsStream("spaceinsensitivity.dmn"));
                BufferedReader buffer = new BufferedReader(reader)) {
            String original_xml = buffer.lines().collect(Collectors.joining("\n"));
            DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();
            Definitions definitions = marshaller.unmarshal(original_xml);
            DRGElement drgElement = definitions.getDrgElement().get(0); // this test expect it to be the InputData
            assertThat(drgElement.getName(), is("my person"));
            InputData inputData = (InputData) drgElement;
            InformationItem inputDataVariable = inputData.getVariable();
            SpacesAndStringParts myPersonString = new SpaceInsensitiveBuilder("my").append("person").build();
            inputData.setName(myPersonString.toString());
            inputDataVariable.setName(myPersonString.toString());
            String xml = marshaller.marshal(definitions);
            KieServices ks = KieServices.Factory.get();
            Resource dmnResource = ks.getResources().newByteArrayResource(xml.getBytes());
            final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                        dmnResource);
            final DMNRuntime runtime = DMNRuntimeUtil.typeSafeGetKieRuntime(kieContainer);
            Assert.assertNotNull(runtime);

            checkSpaceInsensitivityModel(runtime);
        }
    }

}
