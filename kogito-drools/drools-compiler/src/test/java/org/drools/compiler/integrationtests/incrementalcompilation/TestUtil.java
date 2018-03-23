/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.incrementalcompilation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public final class TestUtil {

    public static final String RULES_PACKAGE_NAME = "com.rules";
    public static final String RULE1_NAME = "R1";
    public static final String RULE2_NAME = "R2";
    public static final String RULE3_NAME = "R3";

    public static KieSession createSession(final String... drls) {
        return buildSessionInSteps(drls);
    }

    public static void addRules(final KieSession session, final String... drls) {
        addRules(session, false, drls);
    }

    public static void addRules(final KieSession session, final boolean reuseKieBaseWhenAddingRules, final String... drls) {
        for (final String drl : drls) {
            final KnowledgeBuilder kBuilder;
            if (reuseKieBaseWhenAddingRules) {
                kBuilder = createKnowledgeBuilder(session.getKieBase(), drl);
            } else {
                kBuilder = createKnowledgeBuilder(null, drl);
            }
            ((InternalKnowledgeBase)session.getKieBase()).addPackages(kBuilder.getKnowledgePackages());
        }
    }

    public static void removeRules(final KieSession session, final String rulesPackageName, final String... ruleNames) {
        final KieBase kieBase = session.getKieBase();
        for (final String ruleName : ruleNames) {
            kieBase.removeRule(rulesPackageName, ruleName);
        }
    }

    public static List<FactHandle> insertFacts(final KieSession session, final Object... facts) {
        final List<FactHandle> factHandles = new ArrayList<>();
        for (final Object fact: facts) {
            factHandles.add(session.insert(fact));
        }
        return factHandles;
    }

    public static void removeFacts(final KieSession session, final List<FactHandle> factHandles) {
        for (final FactHandle factHandle : factHandles) {
            session.delete(factHandle);
        }
    }

    public static KieSession buildSessionInSteps(final String... drls) {
        return buildSessionInSteps(false, drls);
    }

    public static KieSession buildSessionInSteps(final boolean reuseKieBaseWhenAddingRules, final String... drls) {
        if (drls == null || drls.length == 0) {
            return KnowledgeBaseFactory.newKnowledgeBase().newKieSession();
        } else {
            String drl = drls[0];
            final KnowledgeBuilder kbuilder = createKnowledgeBuilder(null, drl);
            final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addPackages(kbuilder.getKnowledgePackages());

            final KieSession kSession = kbase.newKieSession();

            for (int i = 1; i < drls.length; i++) {
                drl = drls[i];
                final KnowledgeBuilder kbuilder2;
                if (reuseKieBaseWhenAddingRules) {
                    kbuilder2 = createKnowledgeBuilder(kSession.getKieBase(), drl);
                } else {
                    kbuilder2 = createKnowledgeBuilder(null, drl);
                }
                ((InternalKnowledgeBase)kSession.getKieBase()).addPackages(kbuilder2.getKnowledgePackages());
            }
            return kSession;
        }
    }

    public static KnowledgeBuilder createKnowledgeBuilder(final KieBase kbase, final String drl) {
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

    private static void insertGlobals(final KieSession session, final Map<String, Object> globals) {
        for (final Map.Entry<String, Object> globalEntry : globals.entrySet()) {
            session.setGlobal(globalEntry.getKey(), globalEntry.getValue());
        }
    }

    public static int getRulesCount(final KieBase kBase) {
        int result = 0;
        for (KiePackage kiePackage : kBase.getKiePackages()) {
            result += kiePackage.getRules().size();
        }
        return result;
    }

    public static Object[] getDefaultFacts() {
        return new Object[]{1, 2, "1"};
    }
}
