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
