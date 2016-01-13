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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Abstract class for tests that test adding and removing rules at runtime.
 */
public abstract class AbstractAddRemoveRulesTest {

    protected static final String PKG_NAME_TEST = "com.rules";
    protected static final String RULE1_NAME = "R1";
    protected static final String RULE2_NAME = "R2";
    protected static final String RULE3_NAME = "R3";

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
        final List<List<TestOperation>> testPlan = AddRemoveTestBuilder.getTestPlan(rule1, rule2, rule1Name, rule2Name,
                facts);
        int i = 0;
        for (List<TestOperation> test : testPlan) {
            runAddRemoveTest(test, additionalGlobals);
        }
    }

    protected StatefulKnowledgeSession runAddRemoveTest(final List<TestOperation> testOperations,
            final Map<String, Object> additionalGlobals) {

        StatefulKnowledgeSession session = null;
        final List resultsList = new ArrayList();

        int index = 1;
        for (TestOperation testOperation : testOperations) {
            final TestOperationType testOperationType = testOperation.getType();
            final Object testOperationParameter = testOperation.getParameter();
            if (testOperationType != TestOperationType.CREATE_SESSION) {
                checkSessionInitialized(session);
            }
            switch (testOperationType) {
                case CREATE_SESSION:
                    session = createNewSession((String[]) testOperationParameter, resultsList, additionalGlobals);
                    break;
                case ADD_RULES:
                    addRulesToSession(session, (String[]) testOperationParameter, false);
                    break;
                case ADD_RULES_REINSERT_OLD:
                    addRulesToSession(session, (String[]) testOperationParameter, true);
                    break;
                case REMOVE_RULES:
                    removeRulesFromSession(session, (String[]) testOperationParameter);
                    break;
                case FIRE_RULES:
                    session.fireAllRules();
                    break;
                case CHECK_RESULTS:
                    final Set<String> expectedResultsSet = new HashSet<String>();
                    expectedResultsSet.addAll(Arrays.asList((String[])testOperationParameter));
                    if (expectedResultsSet.size() > 0) {
                        assertTrue(createTestFailMessage(testOperations, index, expectedResultsSet, resultsList),
                                resultsList.size() > 0);
                    }
                    assertTrue(createTestFailMessage(testOperations, index, expectedResultsSet, resultsList),
                            expectedResultsSet.containsAll(resultsList));
                    assertTrue(createTestFailMessage(testOperations, index, expectedResultsSet, resultsList),
                            resultsList.containsAll(expectedResultsSet));
                    resultsList.clear();
                    break;
                case INSERT_FACTS:
                    insertFactsIntoSession(session, (Object[]) testOperationParameter);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported test operation: " + testOperationType + "!");
            }
            index++;
        }
        return session;
    }

    protected int getRulesCount(final KnowledgeBase kBase) {
        int result = 0;
        for (KiePackage kiePackage : kBase.getKiePackages()) {
            result += kiePackage.getRules().size();
        }
        return result;
    }

    private void checkSessionInitialized(final StatefulKnowledgeSession session) {
        if (session == null) {
            throw new IllegalStateException("Session is not initialized! Please, initialize session first.");
        }
    }

    private StatefulKnowledgeSession createNewSession(final String[] drls, final List resultsList,
            final Map<String, Object> additionalGlobals) {
        final StatefulKnowledgeSession session = buildSessionInSteps(drls);
        session.setGlobal("list", resultsList);
        if (additionalGlobals != null) {
            insertGlobalsIntoSession(session, additionalGlobals);
        }
        return session;
    }

    private void addRulesToSession(final StatefulKnowledgeSession session, final String[] drls,
            final boolean reimportAllRules) {
        for (String drl : drls) {
            final KnowledgeBuilder kBuilder;
            if (reimportAllRules) {
                kBuilder = createKnowledgeBuilder(session.getKieBase(), drl);
            } else {
                kBuilder = createKnowledgeBuilder(null, drl);
            }
            session.getKieBase().addKnowledgePackages(kBuilder.getKnowledgePackages());
        }
    }

    private void removeRulesFromSession(final StatefulKnowledgeSession session, final String[] ruleNames) {
        final KieBase kieBase = session.getKieBase();
        for (String ruleName : ruleNames) {
            kieBase.removeRule(PKG_NAME_TEST, ruleName);
        }
    }

    private void insertFactsIntoSession(final StatefulKnowledgeSession session, final Object[] facts) {
        for (Object fact: facts) {
            session.insert(fact);
        }
    }

    private void insertGlobalsIntoSession(final StatefulKnowledgeSession session, final Map<String, Object> globals) {
        for (Map.Entry<String, Object> globalEntry : globals.entrySet()) {
            session.setGlobal(globalEntry.getKey(), globalEntry.getValue());
        }
    }

    private String createTestFailMessage(final List<TestOperation> testOperations, final int operationIndex,
            final Collection<String> expectedResults, final Collection<String> actualResults) {
        final StringBuilder messageBuilder = new StringBuilder();
        final String lineSeparator = System.getProperty("line.separator");
        messageBuilder.append("Test failed on " + operationIndex + ". operation! Operations:" + lineSeparator);
        int index = 1;
        for (TestOperation testOperation : testOperations) {
            messageBuilder.append(index + ". " + testOperation.toString());
            messageBuilder.append(lineSeparator);
            index++;
        }
        messageBuilder.append("Expected results: " + lineSeparator + "[");
        for (String expectedResult : expectedResults) {
            messageBuilder.append(expectedResult + " ");
        }
        messageBuilder.append("]" + lineSeparator);
        messageBuilder.append("Actual results: " + lineSeparator + "[");
        for (String actualResult : actualResults) {
            messageBuilder.append(actualResult + " ");
        }
        messageBuilder.append("]" + lineSeparator);
        return messageBuilder.toString();
    }
}
