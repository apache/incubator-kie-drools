/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.commons.backend.rule;

import java.util.List;

public class SetStatementValidator {

    private SetStatementValidator() {
    }

    private static boolean validate(final String statement) {
        return onlyOneTemplateKey(statement) && noFunctionCallsInStatement(statement);
    }

    private static boolean noFunctionCallsInStatement(final String statement) {
        if (atLeastOneTemplateKey(statement)) {
            return onlyOne(statement, "(");
        } else {
            return true;
        }
    }

    private static boolean atLeastOneTemplateKey(final String statement) {
        return statement.indexOf("@{") >= 0;
    }

    private static boolean onlyOneTemplateKey(final String statement) {
        return onlyOne(statement, "@{");
    }

    private static boolean onlyOne(final String statement,
                                   final String str) {
        int firstTemplateKeyStart = statement.indexOf(str);
        int lastTemplateKeyStart = statement.lastIndexOf(str);
        return firstTemplateKeyStart == lastTemplateKeyStart;
    }

    /**
     * @param setters List of setter statements that affect a Pattern
     * @return True if content is valid for set field value components. False if we need to use FreeForm.
     */
    public static boolean validate(final List<String> setters) {
        for (final String setter : setters) {

            if (!validate(trimUpdate(setter))) {
                return false;
            }
        }

        return true;
    }

    private static String trimUpdate(final String setter) {
        final int lastIndexOfUpdate = setter.lastIndexOf(";update(");

        if (lastIndexOfUpdate >= 0) {
            return setter.substring(0, lastIndexOfUpdate);
        } else {
            return setter;
        }
    }
}
