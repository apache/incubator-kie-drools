/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

public class Splitter {

    /**
     * Used to split expressions. Respects ( ) brackets for methods, for the fist level.
     */
    public static String[] split(final String fieldName) {
        int openBrackets = 0;
        int closedBrackets = 0;
        final char splitOn = '.';

        final List result = new ArrayList();
        StringBuilder wordBuilder = new StringBuilder();

        for (final char c : fieldName.toCharArray()) {

            if (c == '(') {
                openBrackets++;
            } else if (c == ')') {
                closedBrackets++;
            } else if (c == splitOn && (closedBrackets == openBrackets)) {
                result.add(wordBuilder.toString());
                wordBuilder = new StringBuilder();
                continue;
            }

            wordBuilder.append(c);
        }

        result.add(wordBuilder.toString());

        return (String[]) result.toArray(new String[result.size()]);
    }
}
