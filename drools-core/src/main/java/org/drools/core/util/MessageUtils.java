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
package org.drools.core.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageUtils {

    private MessageUtils() {}

    public static String defaultToEmptyString(String str) {
        return Optional.ofNullable(str).orElse("");
    }

    public static String formatConstraintErrorMessage(String expression, Map<String, Set<String>> ruleNameMap, boolean moreThanMaxRuleDefs) {
        String baseMessage = "Error evaluating constraint '%s' in %s";
        if (moreThanMaxRuleDefs) {
            baseMessage += " and in more rules";
        }
        return String.format(baseMessage, expression, formatRuleNameMap(ruleNameMap));
    }

    public static String formatRuleNameMap(Map<String, Set<String>> ruleNameMap) {
        if (ruleNameMap == null || ruleNameMap.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        List<String> ruleFileNameList = ruleNameMap.keySet().stream().sorted().collect(Collectors.toList());
        ruleFileNameList.forEach(ruleFileName -> {
            sb.append("[Rule ");
            ruleNameMap.get(ruleFileName).stream().sorted().forEach(ruleName -> {
                sb.append("\"" + ruleName + "\", ");
            });
            if (!ruleFileNameList.isEmpty()) {
                sb.delete(sb.length() - 2, sb.length());
            }
            sb.append(" in " + ruleFileName + "] ");
        });
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
