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

/**
 * Used to generate better error message
 */
public class PredicateInformation {

    public static final PredicateInformation EMPTY_PREDICATE_INFORMATION =
            new PredicateInformation("", "", "");


    // Used to generate a significant error message
    private final String stringConstraint;
    private final String ruleName;
    private final String ruleFileName;

    public PredicateInformation(String stringConstraint, String ruleName, String ruleFileName) {
        this.stringConstraint = stringConstraint;
        this.ruleName = ruleName;
        this.ruleFileName = ruleFileName;
    }

    public RuntimeException betterErrorMessage(RuntimeException originalException) {
        if("".equals(stringConstraint)) {
            return originalException;
        }

        String errorMessage = String.format(
                "Error evaluating constraint '%s' in [Rule %s in %s]",
                stringConstraint,
                ruleName,
                ruleFileName);
        return new RuntimeException(errorMessage, originalException);
    }

    public String getStringConstraint() {
        return stringConstraint;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getRuleFileName() {
        return ruleFileName;
    }
}
