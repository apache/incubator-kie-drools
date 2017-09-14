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
 *
 */

package org.drools.workbench.models.guided.dtable.backend;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.PluggableIAction;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.TemplateAware;
import org.drools.workbench.models.guided.dtable.backend.util.GuidedDTBRDRLPersistence;
import org.junit.Test;

import static org.junit.Assert.*;

public class GuidedDTBRDRLPersistenceTest {

    @Test
    public void testRHSWithTemplateAwareIAction() {
        GuidedDTBRDRLPersistence persistence = new GuidedDTBRDRLPersistence((key) -> "value");

        RuleModel ruleModel = new RuleModel();
        ruleModel.name = "Template aware";

        ruleModel.addRhsItem(new TemplateAwareIAction("initialValue"));

        String result = persistence.marshal(ruleModel);

        String expected = "rule \"Template aware\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\tthen\n" +
                "\t\tsubstitutedValue;\n" +
                "end\n";

        assertEquals(expected,
                     result);
    }

    private static class TemplateAwareIAction implements IAction,
                                                         TemplateAware,
                                                         PluggableIAction {

        private String value;

        public TemplateAwareIAction(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public Collection<InterpolationVariable> extractInterpolationVariables() {
            return Arrays.asList(new InterpolationVariable("test",
                                                           DataType.TYPE_OBJECT));
        }

        @Override
        public void substituteTemplateVariables(Function<String, String> keyToValueFunction) {
            this.value = "substitutedValue";
        }

        @Override
        public TemplateAware cloneTemplateAware() {
            return new TemplateAwareIAction(value);
        }

        @Override
        public String getStringRepresentation() {
            return value;
        }
    }
}
