/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.constraints;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.io.ByteArrayResource;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.model.codegen.execmodel.BaseModelTest;
import org.drools.model.functions.PredicateInformation;
import org.drools.modelcompiler.constraints.ConstraintEvaluationException;
import org.drools.mvel.MVELConstraint;
import org.junit.Test;
import org.kie.api.io.Resource;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintEvaluationExceptionTest extends BaseModelTest {

    private PredicateInformation predicateInformation; // exec-model

    private MVELConstraint mvelConstraint; // non-exec-model

    public ConstraintEvaluationExceptionTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testMultipleRules() {
        initConstraintTestField("age > 20", "R1", "sample.drl");
        addRuleToConstraintTestField("R2", "sample.drl");
        addRuleToConstraintTestField("R3", "sample.drl");

        assertMessage("Error evaluating constraint 'age > 20' in [Rule \"R1\", \"R2\", \"R3\" in sample.drl]");
    }

    @Test
    public void testMultipleRuleFiles() {
        initConstraintTestField("age > 20", "R1", "sample1.drl");
        addRuleToConstraintTestField("R2", "sample1.drl");
        addRuleToConstraintTestField("R3", "sample2.drl");

        assertMessage("Error evaluating constraint 'age > 20' in [Rule \"R1\", \"R2\" in sample1.drl] [Rule \"R3\" in sample2.drl]");
    }

    @Test
    public void testNull() {
        initConstraintTestField("age > 20", null, "sample1.drl");
        addRuleToConstraintTestField("R2", null);
        addRuleToConstraintTestField(null, null);

        // Irregular case. Not much useful info but doesn't throw NPE
        assertMessage("Error evaluating constraint 'age > 20' in [Rule \"\", \"R2\" in ] [Rule \"\" in sample1.drl]");
    }

    @Test
    public void testExceedMaxRuleDefs() {
        initConstraintTestField("age > 20", "R1", "sample1.drl");
        addRuleToConstraintTestField("R2", "sample1.drl");
        addRuleToConstraintTestField("R3", "sample1.drl");
        addRuleToConstraintTestField("R4", "sample1.drl");
        addRuleToConstraintTestField("R5", "sample1.drl");
        addRuleToConstraintTestField("R6", "sample1.drl");
        addRuleToConstraintTestField("R7", "sample2.drl");
        addRuleToConstraintTestField("R8", "sample2.drl");
        addRuleToConstraintTestField("R9", "sample2.drl");
        addRuleToConstraintTestField("R10", "sample3.drl");
        addRuleToConstraintTestField("R11", "sample3.drl");

        assertMessage("Error evaluating constraint 'age > 20' in [Rule \"R1\", \"R2\", \"R3\", \"R4\", \"R5\", \"R6\" in sample1.drl] [Rule \"R7\", \"R8\", \"R9\" in sample2.drl] [Rule \"R10\" in sample3.drl]" +
                " and in more rules");
    }

    private void initConstraintTestField(String constraint, String ruleName, String ruleFileName) {
        if (testRunType.isExecutableModel()) {
            predicateInformation = new PredicateInformation(constraint, ruleName, ruleFileName);
        } else {
            mvelConstraint = new MVELConstraint("com.example", constraint, null, null, null, null, null);

            InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
            BuildContext buildContext = new BuildContext(kBase, Collections.emptyList());
            RuleImpl ruleImpl = new RuleImpl(ruleName);
            Resource resource = new ByteArrayResource();
            resource.setSourcePath(ruleFileName);
            ruleImpl.setResource(resource);
            buildContext.setRule(ruleImpl);

            mvelConstraint.registerEvaluationContext(buildContext);
        }
    }

    private void addRuleToConstraintTestField(String ruleName, String ruleFileName) {
        if (testRunType.isExecutableModel()) {
            predicateInformation.addRuleNames(ruleName, ruleFileName);
        } else {
            // in non-exec-model, node sharing triggers merging
            MVELConstraint otherMvelConstraint = new MVELConstraint("com.example", mvelConstraint.getExpression(), null, null, null, null, null);

            InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
            BuildContext buildContext = new BuildContext(kBase, Collections.emptyList());
            RuleImpl ruleImpl = new RuleImpl(ruleName);
            Resource resource = new ByteArrayResource();
            resource.setSourcePath(ruleFileName);
            ruleImpl.setResource(resource);
            buildContext.setRule(ruleImpl);
            otherMvelConstraint.registerEvaluationContext(buildContext);

            mvelConstraint.mergeEvaluationContext(otherMvelConstraint);
        }
    }

    private void assertMessage(String expected) {
        Exception ex;
        if (testRunType.isExecutableModel()) {
            ex = new ConstraintEvaluationException(predicateInformation, new RuntimeException("OriginalException"));
        } else {
            ex = new org.drools.mvel.ConstraintEvaluationException(mvelConstraint.getExpression(), mvelConstraint.getEvaluationContext(), new RuntimeException("OriginalException"));
        }
        assertThat(ex.getMessage()).isEqualTo(expected);
    }
}
