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

package org.drools.workbench.models.commons.backend.rule;

import org.drools.workbench.models.commons.backend.rule.actions.TestIAction;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PluggableIActionTest {

    private static final String DRL_RULE = "" +
            "rule \"TestIAction rule\"\n" +
            "\tdialect \"mvel\"\n" +
            "\twhen\n" +
            "\tthen\n" +
            "\t\ttestIAction();\n" +
            "end\n";

    @Test
    public void marshalPluggableIAction() {
        RuleModel ruleModel = new RuleModel();
        ruleModel.name = "TestIAction rule";
        ruleModel.addRhsItem(new TestIAction());

        String marshaledString = RuleModelDRLPersistenceImpl.getInstance().marshal(ruleModel);

        assertEquals(DRL_RULE,
                     marshaledString);
    }
}
