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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.reteoo.ReteDumper;
import org.junit.Assert;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class TestContext {

    private final Map<FactHandle, Object> actualSessionFacts = new HashMap<FactHandle, Object>();
    private final List<TestOperation> executedOperations = new ArrayList<TestOperation>();

    private final Map<String, Object> sessionGlobals;
    private final String rulesPackageName;
    private final List resultsList;

    private StatefulKnowledgeSession session;

    private boolean failFast = true;
    private final List<String> errorMessages = new ArrayList<String>();

    public TestContext(final String rulesPackageName, final Map<String, Object> sessionGlobals,
            final List resultsList) {
        this.rulesPackageName = rulesPackageName;
        this.sessionGlobals = sessionGlobals;
        this.resultsList = resultsList;
    }

    public TestContext(final String rulesPackageName, final Map<String, Object> sessionGlobals,
            final List resultsList, final boolean failFast) {
        this.rulesPackageName = rulesPackageName;
        this.sessionGlobals = sessionGlobals;
        this.resultsList = resultsList;
        this.failFast = failFast;
    }

    public void executeTestOperations(final List<TestOperation> testOperations) {
        for (TestOperation testOperation : testOperations) {
            try {
                executeTestOperation(testOperation);
            } catch (Exception e) {
                throw new RuntimeException(createTestFailMessage(testOperations, null, null), e);
            }
        }
    }

    public void executeTestOperation(final TestOperation testOperation) {
        final TestOperationType testOperationType = testOperation.getType();
        final Object testOperationParameter = testOperation.getParameter();
        if (testOperationType != TestOperationType.CREATE_SESSION) {
            checkSessionInitialized();
        }
        switch (testOperationType) {
            case CREATE_SESSION:
                createSession((String[]) testOperationParameter, false);
                break;
            case ADD_RULES:
                addRules((String[]) testOperationParameter, false);
                break;
            case ADD_RULES_REINSERT_OLD:
                addRules((String[]) testOperationParameter, true);
                break;
            case REMOVE_RULES:
                removeRules((String[]) testOperationParameter);
                break;
            case FIRE_RULES:
                session.fireAllRules();
                break;
            case INSERT_FACTS:
                insertFacts((Object[]) testOperationParameter);
                break;
            case REMOVE_FACTS:
                removeFacts((FactHandle[]) testOperationParameter);
                break;
            case CHECK_RESULTS:
                checkResults((String[]) testOperationParameter);
                break;
            case DUMP_RETE:
                ReteDumper.dumpRete((KieSession) session);
                break;
            default:
                throw new IllegalArgumentException("Unsupported test operation: " + testOperationType + "!");
        }
        executedOperations.add(testOperation);
    }

    public void dumpRete() {
        checkSessionInitialized();
        ReteDumper.dumpRete((KieSession) session);
    }

    public Map<FactHandle, Object> getActualSessionFacts() {
        return actualSessionFacts;
    }

    public Set<FactHandle> getActualSessionFactHandles() {
        return actualSessionFacts.keySet();
    }

    public List<TestOperation> getExecutedOperations() {
        return executedOperations;
    }

    public void clearExecutedOperations() {
        executedOperations.clear();
    }

    public StatefulKnowledgeSession getSession() {
        return session;
    }

    public Object getSessionGlobal(final String sessionGlobalName) {
        return sessionGlobals.get(sessionGlobalName);
    }

    public boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(final boolean failFast) {
        this.failFast = failFast;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void clearErrorMessages() {
        errorMessages.clear();
    }

    private void checkSessionInitialized() {
        if (session == null) {
            throw new IllegalStateException("Session is not initialized! Please, initialize session first.");
        }
    }

    private void addRules(final String[] drls, final boolean reuseKieBaseWhenAddingRules) {
        for (String drl : drls) {
            final KnowledgeBuilder kBuilder;
            if (reuseKieBaseWhenAddingRules) {
                kBuilder = createKnowledgeBuilder(session.getKieBase(), drl);
            } else {
                kBuilder = createKnowledgeBuilder(null, drl);
            }
            session.getKieBase().addKnowledgePackages(kBuilder.getKnowledgePackages());
        }
    }

    private void removeRules(final String[] ruleNames) {
        final KieBase kieBase = session.getKieBase();
        for (String ruleName : ruleNames) {
            kieBase.removeRule(rulesPackageName, ruleName);
        }
    }

    private void insertFacts(final Object[] facts) {
        for (Object fact: facts) {
            actualSessionFacts.put(session.insert(fact), fact);
        }
    }

    private void removeFacts(final FactHandle[] factHandles) {
        for (FactHandle factHandle : factHandles) {
            session.delete(factHandle);
            actualSessionFacts.remove(factHandle);
        }
    }

    private void checkResults(final String[] expectedResults) {
        final Set<String> expectedResultsSet = new HashSet<String>();
        expectedResultsSet.addAll(Arrays.asList(expectedResults));

        if (((expectedResultsSet.size() > 0) && (resultsList.size() == 0))
                || !expectedResultsSet.containsAll(resultsList)
                || !resultsList.containsAll(expectedResultsSet)) {
            if (failFast) {
                Assert.fail(createTestFailMessage(executedOperations, expectedResultsSet, resultsList));
            } else {
                errorMessages.add(createTestFailMessage(executedOperations, expectedResultsSet, resultsList));
            }
        }
        resultsList.clear();
    }

    private void createSession(final String[] drls, final boolean reuseKieBaseWhenAddingRules) {
        if (session != null) {
            actualSessionFacts.clear();
            session.dispose();
        }
        session = buildSessionInSteps(drls, reuseKieBaseWhenAddingRules);
        if (sessionGlobals != null) {
            insertGlobals(session, sessionGlobals);
        }
    }

    private StatefulKnowledgeSession buildSessionInSteps(final String[] drls,
            final boolean reuseKieBaseWhenAddingRules) {
        if (drls == null || drls.length == 0) {
            return KnowledgeBaseFactory.newKnowledgeBase().newStatefulKnowledgeSession();
        } else {
            String drl = drls[0];
            final KnowledgeBuilder kbuilder = createKnowledgeBuilder(null, drl);
            final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

            final StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();

            for (int i = 1; i < drls.length; i++) {
                drl = drls[i];
                final KnowledgeBuilder kbuilder2;
                if (reuseKieBaseWhenAddingRules) {
                    kbuilder2 = createKnowledgeBuilder(kSession.getKieBase(), drl);
                } else {
                    kbuilder2 = createKnowledgeBuilder(null, drl);
                }
                kSession.getKieBase().addKnowledgePackages(kbuilder2.getKnowledgePackages());
            }
            return kSession;
        }
    }

    private KnowledgeBuilder createKnowledgeBuilder(final KnowledgeBase kbase, final String drl) {
        final KnowledgeBuilder kbuilder;
        if (kbase == null) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbase);
        }

        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            throw new RuntimeException("Knowledge contains errors: " + kbuilder.getErrors().toString());
        }
        return kbuilder;
    }

    private void insertGlobals(final StatefulKnowledgeSession session, final Map<String, Object> globals) {
        for (Map.Entry<String, Object> globalEntry : globals.entrySet()) {
            session.setGlobal(globalEntry.getKey(), globalEntry.getValue());
        }
    }

    private String createTestFailMessage(final List<TestOperation> testOperations,
            final Collection<String> expectedResults, final Collection<String> actualResults) {
        final StringBuilder messageBuilder = new StringBuilder();
        final String lineSeparator = System.getProperty("line.separator");
        messageBuilder.append("Expected results are different than actual after operations:" + lineSeparator);
        int index = 1;
        for (TestOperation testOperation : testOperations) {
            messageBuilder.append(index + ". " + testOperation.toString());
            messageBuilder.append(lineSeparator);
            index++;
        }
        if (expectedResults != null && actualResults != null) {
            messageBuilder.append( "Expected results: " + lineSeparator + "[" );
            for ( String expectedResult : expectedResults ) {
                messageBuilder.append( expectedResult + " " );
            }
            messageBuilder.append( "]" + lineSeparator );
            messageBuilder.append( "Actual results: " + lineSeparator + "[" );
            for ( String actualResult : actualResults ) {
                messageBuilder.append( actualResult + " " );
            }
        }
        messageBuilder.append("]" + lineSeparator);
        return messageBuilder.toString();
    }
}
