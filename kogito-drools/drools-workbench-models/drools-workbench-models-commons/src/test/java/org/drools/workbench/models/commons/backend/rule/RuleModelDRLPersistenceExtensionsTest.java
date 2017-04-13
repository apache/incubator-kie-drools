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

import java.util.Arrays;
import java.util.Collections;

import org.drools.workbench.models.commons.backend.oracle.PackageDataModelOracleImpl;
import org.drools.workbench.models.commons.backend.rule.actions.TestIAction;
import org.drools.workbench.models.commons.backend.rule.exception.RuleModelDRLPersistenceException;
import org.drools.workbench.models.commons.backend.rule.extensions.TestIActionPersistenceExtension;
import org.drools.workbench.models.commons.backend.rule.extensions.TestIActionPersistenceExtensionCopy;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuleModelDRLPersistenceExtensionsTest {

    private static final String DRL_RULE = "" +
            "rule \"TestIAction rule\"\n" +
            "\tdialect \"mvel\"\n" +
            "\twhen\n" +
            "\tthen\n" +
            "\t\ttestIAction();\n" +
            "end\n";

    private static final String DSL_RULE = "" +
            "rule \"TestIAction rule\"\n" +
            "\tdialect \"mvel\"\n" +
            "\twhen\n" +
            "\tthen\n" +
            "\t\t>testIAction();\n" +
            "end\n";

    @Test
    public void unmarshalWithoutExtensions() {
        RuleModel ruleModel = RuleModelDRLPersistenceImpl.getInstance().unmarshal(DRL_RULE,
                                                                                  Collections.emptyList(),
                                                                                  new PackageDataModelOracleImpl());
        assertEquals(1,
                     ruleModel.rhs.length);

        IAction iAction = ruleModel.rhs[0];
        assertTrue(iAction instanceof FreeFormLine);
    }

    @Test
    public void unmarshalWithExtensions() {
        RuleModel ruleModel = RuleModelDRLPersistenceImpl.getInstance().unmarshal(DRL_RULE,
                                                                                  Collections.emptyList(),
                                                                                  new PackageDataModelOracleImpl(),
                                                                                  Arrays.asList(new TestIActionPersistenceExtension()));
        assertEquals(1,
                     ruleModel.rhs.length);

        IAction iAction = ruleModel.rhs[0];
        assertTrue(iAction instanceof TestIAction);
    }

    @Test(expected = RuleModelDRLPersistenceException.class)
    public void unmarshalWithAmbiguousExtensions() {
        RuleModelDRLPersistenceImpl.getInstance().unmarshal(DRL_RULE,
                                                            Collections.emptyList(),
                                                            new PackageDataModelOracleImpl(),
                                                            Arrays.asList(new TestIActionPersistenceExtension(),
                                                                          new TestIActionPersistenceExtensionCopy()));
    }

    @Test
    public void unmarshalDSLWithoutExtensions() {
        RuleModel ruleModel = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(DSL_RULE,
                                                                                          Collections.emptyList(),
                                                                                          new PackageDataModelOracleImpl());
        assertEquals(1,
                     ruleModel.rhs.length);

        IAction iAction = ruleModel.rhs[0];
        assertTrue(iAction instanceof FreeFormLine);
    }

    @Test
    public void unmarshalDSLWithExtensions() {
        RuleModel ruleModel = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(DSL_RULE,
                                                                                          Collections.emptyList(),
                                                                                          new PackageDataModelOracleImpl(),
                                                                                          Arrays.asList(new TestIActionPersistenceExtension()));
        assertEquals(1,
                     ruleModel.rhs.length);

        IAction iAction = ruleModel.rhs[0];
        assertTrue(iAction instanceof TestIAction);
    }

    @Test(expected = RuleModelDRLPersistenceException.class)
    public void unmarshalDSLWithAmbiguousExtensions() {
        RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL(DRL_RULE,
                                                                    Collections.emptyList(),
                                                                    new PackageDataModelOracleImpl(),
                                                                    Arrays.asList(new TestIActionPersistenceExtension(),
                                                                                  new TestIActionPersistenceExtensionCopy()));
    }

    @Test
    public void marshalWithoutExtensions() {
        RuleModel ruleModel = new RuleModel();
        ruleModel.name = "TestIAction rule";
        ruleModel.addRhsItem(new TestIAction());

        String marshaledString = RuleModelDRLPersistenceImpl.getInstance().marshal(ruleModel);

        String expectedString = "" +
                "rule \"TestIAction rule\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\tthen\n" +
                "end\n";
        assertEquals(expectedString,
                     marshaledString);
    }

    @Test
    public void marshalWithExtensions() {
        RuleModel ruleModel = new RuleModel();
        ruleModel.name = "TestIAction rule";
        ruleModel.addRhsItem(new TestIAction());

        String marshaledString = RuleModelDRLPersistenceImpl.getInstance().marshal(ruleModel,
                                                                                   Arrays.asList(new TestIActionPersistenceExtension()));

        assertEquals(DRL_RULE,
                     marshaledString);
    }

    @Test(expected = RuleModelDRLPersistenceException.class)
    public void marshalWithAmbigiousExtensions() {
        RuleModel ruleModel = new RuleModel();
        ruleModel.name = "TestIAction rule";
        ruleModel.addRhsItem(new TestIAction());

        RuleModelDRLPersistenceImpl.getInstance().marshal(ruleModel,
                                                          Arrays.asList(new TestIActionPersistenceExtension(),
                                                                        new TestIActionPersistenceExtensionCopy()));
    }
}
