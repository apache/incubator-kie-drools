/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests.incrementalcompilation;

import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * Abstract class for tests that test adding and removing rules at runtime.
 */
public abstract class AbstractAddRemoveRulesTest {

    protected static final String PKG_NAME_TEST = "com.rules";
    protected static final String RULE1_NAME = "R1";
    protected static final String RULE2_NAME = "R2";
    protected static final String RULE3_NAME = "R3";

    protected static void checkRunTurtleTests() {
        assumeTrue("true".equals(System.getProperty("runTurtleTests")));
    }

    // TODO - remove these two methods - they are also in TestContext
    protected KnowledgeBuilder createKnowledgeBuilder(final KnowledgeBase kbase, final String drl) {
        final KnowledgeBuilder kbuilder;
        if (kbase == null) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbase);
        }

        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            Assert.fail(kbuilder.getErrors().toString());
        }
        return kbuilder;
    }

    protected StatefulKnowledgeSession buildSessionInSteps(final String... drls) {
        if (drls == null || drls.length == 0) {
            return KnowledgeBaseFactory.newKnowledgeBase().newStatefulKnowledgeSession();
        } else {
            String drl = drls[0];
            final KnowledgeBuilder kbuilder = createKnowledgeBuilder(null, drl);
            final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

            final StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
            kSession.fireAllRules();

            for (int i = 1; i < drls.length; i++) {
                drl = drls[i];
                final KnowledgeBuilder kbuilder2 = createKnowledgeBuilder(kSession.getKieBase(), drl);
                kSession.getKieBase().addKnowledgePackages(kbuilder2.getKnowledgePackages());
            }
            return kSession;
        }
    }

    protected void runAddRemoveTests(final String rule1, final String rule2, final String rule1Name,
            final String rule2Name, final Object[] facts, final Map<String, Object> additionalGlobals) {
        final List<List<TestOperation>> testPlans = AddRemoveTestBuilder.getTestPlan(rule1, rule2, rule1Name, rule2Name,
                facts);
        runAddRemoveTests(testPlans, additionalGlobals);
    }

    protected void runAddRemoveTests(final List<List<TestOperation>> testPlans,
            final Map<String, Object> additionalGlobals) {
        for (List<TestOperation> testPlan : testPlans) {
            runAddRemoveTest(testPlan, additionalGlobals);
        }
    }

    protected StatefulKnowledgeSession runAddRemoveTest(final List<TestOperation> testOperations,
            final Map<String, Object> additionalGlobals) {

        final List resultsList = new ArrayList();
        final Map<String, Object> sessionGlobals = new HashMap<String, Object>();
        if (additionalGlobals != null) {
            sessionGlobals.putAll(additionalGlobals);
        }
        sessionGlobals.put("list", resultsList);

        final TestContext testContext = new TestContext(PKG_NAME_TEST, sessionGlobals, resultsList);
        testContext.executeTestOperations(testOperations);
        return testContext.getSession();
    }

    protected int getRulesCount(final KnowledgeBase kBase) {
        int result = 0;
        for (KiePackage kiePackage : kBase.getKiePackages()) {
            result += kiePackage.getRules().size();
        }
        return result;
    }
}
