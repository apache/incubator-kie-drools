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

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertLogicalFact;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RHSClassDependencyVisitorTest {

    private RuleModelDRLPersistenceImpl.RHSClassDependencyVisitor visitor;

    @Before
    public void setUp() {
        visitor = new RuleModelDRLPersistenceImpl.RHSClassDependencyVisitor();
    }

    @Test
    public void visitActionInsertLogicalFact() {
        ActionInsertLogicalFact iAction = new ActionInsertLogicalFact();
        iAction.setFieldValues(new ActionFieldValue[]{new ActionFieldValue("field",
                                                                           "value",
                                                                           "type")});

        visitor.visit(iAction);

        assertTrue(visitor.getRHSClasses().containsKey("type"));
    }

    @Test
    public void visitFreeFormLine() {
        visitor.visit(new FreeFormLine());

        assertTrue(visitor.getRHSClasses().isEmpty());
    }
}
