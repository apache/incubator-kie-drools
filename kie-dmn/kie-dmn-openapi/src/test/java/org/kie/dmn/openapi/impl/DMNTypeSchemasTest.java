/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.openapi.impl;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.UnaryTests;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNTypeSchemasTest {

    @Test
    public void generateSchemas() {
    }

    @Test
    public void schemaFromSimpleType() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("valid_models/DMNv1_5" +
                                                                        "/AllowedValuesChecksInsideCollection.dmn",
                                                                this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442",
                "AllowedValuesChecksInsideCollection");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        ItemDefinition itemDefinition = dmnModel.getDefinitions().getItemDefinition().stream().filter(itDef -> itDef.getName().equals("tInterests"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No tInterests item definition found"));
        UnaryTests allowedValues = itemDefinition.getAllowedValues();
        assertThat(allowedValues).isNotNull();

    }

    private static String getAllowedValuesInCollection() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<dmn:definitions xmlns=\"http://www.trisotech" +
                ".com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442\"\n" +
                "                      xmlns:dc=\"http://www.omg.org/spec/DMN/20180521/DC/\"\n" +
                "                      xmlns:di=\"http://www.omg.org/spec/DMN/20180521/DI/\"\n" +
                "                      xmlns:dmndi=\"https://www.omg.org/spec/DMN/20230324/DMNDI/\"\n" +
                "                      xmlns:feel=\"https://www.omg.org/spec/DMN/20230324/FEEL/\"\n" +
                "                      xmlns:dmn=\"https://www.omg.org/spec/DMN/20230324/MODEL/\"\n" +
                "                      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "                      exporter=\"DMN Modeler\"\n" +
                "                      exporterVersion=\"5.1.10.201705011622\"\n" +
                "                      id=\"_238bd96d-47cd-4746-831b-504f3e77b442\"\n" +
                "                      name=\"AllowedValuesChecksInsideCollection\"\n" +
                "                      namespace=\"http://www.trisotech" +
                ".com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442\">\n" +
                "   <dmn:itemDefinition isCollection=\"true\" label=\"tInterests\" name=\"tInterests\">\n" +
                "     <dmn:typeRef>string</dmn:typeRef>\n" +
                "     <dmn:allowedValues xmlns:triso=\"http://www.trisotech.com/2015/triso/modeling\"\n" +
                "                             triso:constraintsType=\"enumeration\">\n" +
                "       <dmn:text>\"Golf\",\"Computer\",\"Hockey\",\"Jogging\"</dmn:text>\n" +
                "     </dmn:allowedValues>\n" +
                "   </dmn:itemDefinition>\n" +
                "</dmn:definitions>";
    }
}