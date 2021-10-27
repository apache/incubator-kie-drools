/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.functions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Used to generate a better error message when constraints fail
 */
public class PredicateInformation {

    public static final PredicateInformation EMPTY_PREDICATE_INFORMATION =
            new PredicateInformation("", "", "");

    // Used to generate a significant error message
    private final String stringConstraint;
    private final Set<RuleDef> ruleDefs = new TreeSet<>();

    public PredicateInformation(String stringConstraint, String... ruleNames) {
        this.stringConstraint = defaultToEmptyString(stringConstraint);
        addRuleNames(ruleNames);
    }

    public PredicateInformation(String stringConstraint, String ruleName, String ruleFileName) {
        this(stringConstraint, new String[]{ruleName, ruleFileName});
    }

    public String getStringConstraint() {
        return stringConstraint;
    }

    public Set<RuleDef> getRuleDefs() {
        return ruleDefs;
    }

    public void addRuleNames(String... ruleNames) {
        for (int i = 0; i < ruleNames.length; i+=2) {
            ruleDefs.add(new RuleDef(defaultToEmptyString(ruleNames[i+1]), defaultToEmptyString(ruleNames[i])));
        }
    }

    public Map<String, Set<String>> getRuleNameMap() {
        Map<String, Set<String>> map = new HashMap<>();
        for (RuleDef ruleDef : ruleDefs) {
            map.computeIfAbsent(ruleDef.fileName, k -> new HashSet<>()).add(ruleDef.ruleName);
        }
        return map;
    }

    public static String defaultToEmptyString(String str) {
        return str == null ? "" : str;
    }

    public boolean isEmpty() {
        return EMPTY_PREDICATE_INFORMATION.equals(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PredicateInformation that = (PredicateInformation) o;
        return Objects.equals(stringConstraint, that.stringConstraint) &&
                Objects.equals(ruleDefs, that.ruleDefs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringConstraint, ruleDefs);
    }

    @Override
    public String toString() {
        return "PredicateInformation{" +
                "stringConstraint='" + stringConstraint + '\'' +
                ", ruleNameMap='" + ruleDefs + '\'' +
                '}';
    }

    public static class RuleDef implements Comparable<RuleDef> {
        private final String fileName;
        private final String ruleName;

        public RuleDef(String fileName, String ruleName) {
            this.fileName = fileName;
            this.ruleName = ruleName;
        }

        public String getFileName() {
            return fileName;
        }

        public String getRuleName() {
            return ruleName;
        }

        @Override
        public int compareTo(RuleDef other) {
            int fileNameCompare = this.fileName.compareTo(other.fileName);
            return fileNameCompare != 0 ? fileNameCompare : this.ruleName.compareTo(other.ruleName);
        }

        @Override
        public String toString() {
            return "RuleDef{" +
                    "fileName='" + fileName + '\'' +
                    ", ruleName='" + ruleName + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RuleDef ruleDef = (RuleDef) o;
            return Objects.equals(fileName, ruleDef.fileName) && Objects.equals(ruleName, ruleDef.ruleName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fileName, ruleName);
        }
    }
}
