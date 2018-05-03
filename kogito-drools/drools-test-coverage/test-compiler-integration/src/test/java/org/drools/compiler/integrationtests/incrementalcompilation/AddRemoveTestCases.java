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
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.kie.api.runtime.KieSession;

public final class AddRemoveTestCases {
    
    public static void runAllTestCases(final String rule1,
                                       final String rule2,
                                       final String rule1Name,
                                       final String rule2Name,
                                       final Map<String, Object> additionalGlobals,
                                       final Object... facts) {
        
        insertFactsFireRulesRemoveRules1(rule1, rule2, rule1Name, rule2Name, additionalGlobals,facts);
        insertFactsFireRulesRemoveRules2(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        insertFactsFireRulesRemoveRules3(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);

        fireRulesInsertFactsFireRulesRemoveRules1(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        fireRulesInsertFactsFireRulesRemoveRules2(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        fireRulesInsertFactsFireRulesRemoveRules3(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);

        insertFactsRemoveRulesFireRulesRemoveRules1(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        insertFactsRemoveRulesFireRulesRemoveRules2(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        insertFactsRemoveRulesFireRulesRemoveRules3(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);

        insertFactsFireRulesRemoveRulesReinsertRules1(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        insertFactsFireRulesRemoveRulesReinsertRules2(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
        insertFactsFireRulesRemoveRulesReinsertRules3(rule1, rule2, rule1Name, rule2Name, additionalGlobals, facts);
    }

    public static void insertFactsFireRulesRemoveRules1(final String rule1,
                                                        final String rule2,
                                                        final String rule1Name,
                                                        final String rule2Name,
                                                        final Map<String, Object> additionalGlobals,
                                                        final Object... facts) {
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();

            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();

            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();

            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule2Name);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.addRules(kieSession, rule1, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();

            TestUtil.addRules(kieSession, rule1);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name);
            resultsList.clear();

            TestUtil.addRules(kieSession, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
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
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
            resultsList.clear();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule1Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();

            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rule2Name);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();

            TestUtil.addRules(kieSession, true, rule1);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name);
            resultsList.clear();

            TestUtil.addRules(kieSession, true, rule2);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(rule1Name, rule2Name);
        } finally {
            kieSession.dispose();
        }
    }

    public static void insertFactsRemoveFire(final String rule1,
                                             final String rule2,
                                             final Map<String, Object> additionalGlobals,
                                             final Object... facts) {
        final KieSession kieSession = TestUtil.createSession(rule1, rule2);
        try {
            final List resultsList = new ArrayList();
            kieSession.setGlobal("list", resultsList);
            if (additionalGlobals != null && !additionalGlobals.isEmpty()) {
                additionalGlobals.forEach(kieSession::setGlobal);
            }
            TestUtil.insertFacts(kieSession, facts);
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, "R2");
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly("R1");
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, "R1");
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }

    public static Object[] getDefaultFacts() {
        return new Object[]{1, 2, "1"};
    }
}
