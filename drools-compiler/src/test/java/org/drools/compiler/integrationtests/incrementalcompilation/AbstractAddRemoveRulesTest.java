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

import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Abstract class for tests that test adding and removing rules at runtime.
 */
public abstract class AbstractAddRemoveRulesTest {

    protected static final String PKG_NAME_TEST = "com.rules";

    protected KnowledgeBuilder createKnowledgeBuilder(final KnowledgeBase kbase, final String drl) {
        final KnowledgeBuilder kbuilder;
        if (kbase == null) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbase);
        }

        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        return kbuilder;
    }

    protected StatefulKnowledgeSession buildSessionInTwoSteps(final String... drls) {

        String drl = drls[0];
        final KnowledgeBuilder kbuilder = createKnowledgeBuilder(null, drl);
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();
        kSession.fireAllRules();

        for ( int i = 1; i < drls.length; i++) {
            drl = drls[i];
            final KnowledgeBuilder kbuilder2 = createKnowledgeBuilder(kSession.getKieBase(), drl);
            kSession.getKieBase().addKnowledgePackages(kbuilder2.getKnowledgePackages());
        }

        return kSession;
    }

    protected void testRemoveWithSplitStartBasicTestSet(final String rule1, final String rule2,
                                                        final List<Object> facts,
                                                        final Map<String, Object> additionalGlobals) {
        // delete before first fireAllRules
        testRemoveWithSplitStartGeneral(rule1, rule2, facts, false, "[R1]", additionalGlobals);

        // repeat but reverse the rule order
        testRemoveWithSplitStartGeneral(rule2, rule1, facts, false, "[R1]", additionalGlobals);

        // delete after first fireAllRules
        testRemoveWithSplitStartGeneral(rule1, rule2, facts, true, "[R1, R2]", additionalGlobals);

        // repeat but reverse the rule order
        testRemoveWithSplitStartGeneral(rule2, rule1, facts, true, "[R1, R2]", additionalGlobals);
    }

    protected void testRemoveWithSplitStartGeneral(final String rule1, final String rule2,
                                                   final List<Object> facts,
                                                   final boolean deleteAfterFirstFire,
                                                   final String expectedResult,
                                                   final Map<String, Object> additionalGlobals) {

        final List resultsList = new ArrayList();

        final StatefulKnowledgeSession session = buildSessionInTwoSteps(rule1, rule2);
        session.setGlobal("list", resultsList);
        if (additionalGlobals != null) {
            insertGlobalsIntoSession(session, additionalGlobals);
        }
        insertFactsIntoSession(session, facts);
        final KieBase base = session.getKieBase();
        if (deleteAfterFirstFire) {
            session.fireAllRules();
            base.removeRule(PKG_NAME_TEST, "R2");
        } else {
            base.removeRule(PKG_NAME_TEST, "R2");
            session.fireAllRules();
        }

        base.removeRule(PKG_NAME_TEST, "R1");
        session.fireAllRules();
        assertEquals(expectedResult, resultsList.toString());
        resultsList.clear();
    }

    protected int getRulesCount(final KnowledgeBase kBase) {
        int result = 0;
        for (KiePackage kiePackage : kBase.getKiePackages()) {
            result += kiePackage.getRules().size();
        }
        return result;
    }

    private void insertFactsIntoSession(final StatefulKnowledgeSession session, final List<Object> facts) {
        for (Object fact: facts) {
            session.insert(fact);
        }
    }

    private void insertGlobalsIntoSession(final StatefulKnowledgeSession session, final Map<String, Object> globals) {
        for (Map.Entry<String, Object> globalEntry : globals.entrySet()) {
            session.setGlobal(globalEntry.getKey(), globalEntry.getValue());
        }
    }
}
