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
package org.drools.compiler.integrationtests.incrementalcompilation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public final class AddRemoveTestCases {

    public static void runAllTestCases(final String rule1,
                                       final String rule2,
                                       final String rule1Name,
                                       final String rule2Name,
                                       final Map<String, Object> additionalGlobals,
                                       final Object... facts) {
        runAllTestCases(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void runAllTestCases(final InternalKnowledgeBase originalKnowledgeBase,
                                       final String rule1,
                                       final String rule2,
                                       final String rule1Name,
                                       final String rule2Name,
                                       final Map<String, Object> additionalGlobals,
                                       final Object... facts) {
        
        insertFactsFireRulesRemoveRules1(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals,facts);
        insertFactsFireRulesRemoveRules2(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        insertFactsFireRulesRemoveRules3(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);

        fireRulesInsertFactsFireRulesRemoveRules1(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        fireRulesInsertFactsFireRulesRemoveRules2(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        fireRulesInsertFactsFireRulesRemoveRules3(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);

        insertFactsRemoveRulesFireRulesRemoveRules1(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        insertFactsRemoveRulesFireRulesRemoveRules2(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        insertFactsRemoveRulesFireRulesRemoveRules3(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);

        insertFactsFireRulesRemoveRulesReinsertRules1(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        insertFactsFireRulesRemoveRulesReinsertRules2(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        insertFactsFireRulesRemoveRulesReinsertRules3(originalKnowledgeBase, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsFireRulesRemoveRules1(final String rule1,
                                                        final String rule2,
                                                        final String rule1Name,
                                                        final String rule2Name,
                                                        final Map<String, Object> additionalGlobals,
                                                        final Object... facts) {
        insertFactsFireRulesRemoveRules1(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsFireRulesRemoveRules1(final InternalKnowledgeBase originalKnowledgeBase,
                                                        final String rule1,
                                                        final String rule2,
                                                        final String rule1Name,
                                                        final String rule2Name,
                                                        final Map<String, Object> additionalGlobals,
                                                        final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();

            assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static void insertFactsFireRulesRemoveRules2(final String rule1,
                                                        final String rule2,
                                                        final String rule1Name,
                                                        final String rule2Name,
                                                        final Map<String, Object> additionalGlobals,
                                                        final Object... facts) {
        insertFactsFireRulesRemoveRules2(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsFireRulesRemoveRules2(final InternalKnowledgeBase originalKnowledgeBase,
                                                        final String rule1,
                                                        final String rule2,
                                                        final String rule1Name,
                                                        final String rule2Name,
                                                        final Map<String, Object> additionalGlobals,
                                                        final Object... facts) {
//        final String rule3 = "package " + TestUtil.RULES_PACKAGE_NAME + ";" +
//                             "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
//                             "global java.util.List list\n" +
//                             "rule " + TestUtil.RULE3_NAME + " \n" +
//                             "when\n" +
//                             "    $s : String()\n" +
//                             "    Integer()\n" +
//                             "    exists( Integer() and Integer() )\n" +
//                             "then\n" +
//                             " list.add('" + TestUtil.RULE3_NAME + "'); \n" +
//                             "end\n";

        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2); //, rule3);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();

            assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static void insertFactsFireRulesRemoveRules3(final String rule1,
                                                        final String rule2,
                                                        final String rule1Name,
                                                        final String rule2Name,
                                                        final Map<String, Object> additionalGlobals,
                                                        final Object... facts) {
        insertFactsFireRulesRemoveRules3(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsFireRulesRemoveRules3(final InternalKnowledgeBase originalKnowledgeBase,
                                                        final String rule1,
                                                        final String rule2,
                                                        final String rule1Name,
                                                        final String rule2Name,
                                                        final Map<String, Object> additionalGlobals,
                                                        final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();

            assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static void insertFactsRemoveRulesFireRulesRemoveRules1(final String rule1,
                                                                   final String rule2,
                                                                   final String rule1Name,
                                                                   final String rule2Name,
                                                                   final Map<String, Object> additionalGlobals,
                                                                   final Object... facts) {
        insertFactsRemoveRulesFireRulesRemoveRules1(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsRemoveRulesFireRulesRemoveRules1(final InternalKnowledgeBase originalKnowledgeBase,
                                                                   final String rule1,
                                                                   final String rule2,
                                                                   final String rule1Name,
                                                                   final String rule2Name,
                                                                   final Map<String, Object> additionalGlobals,
                                                                   final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static void insertFactsRemoveRulesFireRulesRemoveRules2(final String rule1,
                                                                   final String rule2,
                                                                   final String rule1Name,
                                                                   final String rule2Name,
                                                                   final Map<String, Object> additionalGlobals,
                                                                   final Object... facts) {
        insertFactsRemoveRulesFireRulesRemoveRules2(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsRemoveRulesFireRulesRemoveRules2(final InternalKnowledgeBase originalKnowledgeBase,
                                                                   final String rule1,
                                                                   final String rule2,
                                                                   final String rule1Name,
                                                                   final String rule2Name,
                                                                   final Map<String, Object> additionalGlobals,
                                                                   final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule1Name);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static void insertFactsRemoveRulesFireRulesRemoveRules3(final String rule1,
                                                                   final String rule2,
                                                                   final String rule1Name,
                                                                   final String rule2Name,
                                                                   final Map<String, Object> additionalGlobals,
                                                                   final Object... facts) {
        insertFactsRemoveRulesFireRulesRemoveRules3(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsRemoveRulesFireRulesRemoveRules3(final InternalKnowledgeBase originalKnowledgeBase,
                                                                   final String rule1,
                                                                   final String rule2,
                                                                   final String rule1Name,
                                                                   final String rule2Name,
                                                                   final Map<String, Object> additionalGlobals,
                                                                   final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule2Name);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static void fireRulesInsertFactsFireRulesRemoveRules1(final String rule1,
                                                                 final String rule2,
                                                                 final String rule1Name,
                                                                 final String rule2Name,
                                                                 final Map<String, Object> additionalGlobals,
                                                                 final Object... facts) {
        fireRulesInsertFactsFireRulesRemoveRules1(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void fireRulesInsertFactsFireRulesRemoveRules1(final InternalKnowledgeBase originalKnowledgeBase,
                                                                 final String rule1,
                                                                 final String rule2,
                                                                 final String rule1Name,
                                                                 final String rule2Name,
                                                                 final Map<String, Object> additionalGlobals,
                                                                 final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static void fireRulesInsertFactsFireRulesRemoveRules2(final String rule1,
                                                                 final String rule2,
                                                                 final String rule1Name,
                                                                 final String rule2Name,
                                                                 final Map<String, Object> additionalGlobals,
                                                                 final Object... facts) {
        fireRulesInsertFactsFireRulesRemoveRules2(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void fireRulesInsertFactsFireRulesRemoveRules2(final InternalKnowledgeBase originalKnowledgeBase,
                                                                 final String rule1,
                                                                 final String rule2,
                                                                 final String rule1Name,
                                                                 final String rule2Name,
                                                                 final Map<String, Object> additionalGlobals,
                                                                 final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static void fireRulesInsertFactsFireRulesRemoveRules3(final String rule1,
                                                                 final String rule2,
                                                                 final String rule1Name,
                                                                 final String rule2Name,
                                                                 final Map<String, Object> additionalGlobals,
                                                                 final Object... facts) {
        fireRulesInsertFactsFireRulesRemoveRules3(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void fireRulesInsertFactsFireRulesRemoveRules3(final InternalKnowledgeBase originalKnowledgeBase,
                                                                 final String rule1,
                                                                 final String rule2,
                                                                 final String rule1Name,
                                                                 final String rule2Name,
                                                                 final Map<String, Object> additionalGlobals,
                                                                 final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static void insertFactsFireRulesRemoveRulesReinsertRules1(final String rule1,
                                                                     final String rule2,
                                                                     final String rule1Name,
                                                                     final String rule2Name,
                                                                     final Map<String, Object> additionalGlobals,
                                                                     final Object... facts) {
        insertFactsFireRulesRemoveRulesReinsertRules1(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsFireRulesRemoveRulesReinsertRules1(final InternalKnowledgeBase originalKnowledgeBase,
                                                                     final String rule1,
                                                                     final String rule2,
                                                                     final String rule1Name,
                                                                     final String rule2Name,
                                                                     final Map<String, Object> additionalGlobals,
                                                                     final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
            TestUtil.addRules(kieSession, rule1, rule2);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule1Name, rule2Name);
        } finally {
            kieSession.dispose();
        }
    }

    public static void insertFactsFireRulesRemoveRulesReinsertRules2(final String rule1,
                                                                     final String rule2,
                                                                     final String rule1Name,
                                                                     final String rule2Name,
                                                                     final Map<String, Object> additionalGlobals,
                                                                     final Object... facts) {
        insertFactsFireRulesRemoveRulesReinsertRules2(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsFireRulesRemoveRulesReinsertRules2(final InternalKnowledgeBase originalKnowledgeBase,
                                                                     final String rule1,
                                                                     final String rule2,
                                                                     final String rule1Name,
                                                                     final String rule2Name,
                                                                     final Map<String, Object> additionalGlobals,
                                                                     final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();

            TestUtil.addRules(kieSession, rule1);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule1Name);
            resultsList.clear();

            TestUtil.addRules(kieSession, rule2);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static void insertFactsFireRulesRemoveRulesReinsertRules3(final String rule1,
                                                                     final String rule2,
                                                                     final String rule1Name,
                                                                     final String rule2Name,
                                                                     final Map<String, Object> additionalGlobals,
                                                                     final Object... facts) {
        insertFactsFireRulesRemoveRulesReinsertRules3(null, rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsFireRulesRemoveRulesReinsertRules3(final InternalKnowledgeBase originalKnowledgeBase,
                                                                     final String rule1,
                                                                     final String rule2,
                                                                     final String rule1Name,
                                                                     final String rule2Name,
                                                                     final Map<String, Object> additionalGlobals,
                                                                     final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();

            TestUtil.addRules(kieSession, true, rule1);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule1Name);
            resultsList.clear();

            TestUtil.addRules(kieSession, true, rule2);
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly(rule2Name);
        } finally {
            kieSession.dispose();
        }
    }

    public static void insertFactsRemoveFire(final String rule1,
                                             final String rule2,
                                             final Map<String, Object> additionalGlobals,
                                             final Object... facts) {
        insertFactsRemoveFire(null, rule1, rule2, additionalGlobals, facts);
    }

    public static void insertFactsRemoveFire(final InternalKnowledgeBase originalKnowledgeBase,
                                             final String rule1,
                                             final String rule2,
                                             final Map<String, Object> additionalGlobals,
                                             final Object... facts) {
        final KieSession kieSession = TestUtil.buildSessionInSteps(originalKnowledgeBase, rule1, rule2);
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, "R2");
            kieSession.fireAllRules();
            assertThat(resultsList).containsOnly("R1");
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, "R1");
            kieSession.fireAllRules();
            assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    private AddRemoveTestCases() {
        // Creating instances is not allowed for util classes.
    }
}
