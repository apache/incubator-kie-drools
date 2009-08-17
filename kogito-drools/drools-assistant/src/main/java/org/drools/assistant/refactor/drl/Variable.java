package org.drools.assistant.refactor.drl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.assistant.info.drl.RuleBasicContentInfo;
import org.drools.assistant.info.drl.RuleLineContentInfo;

public abstract class Variable {
	
	private static final String VARIABLE_PATTERN = "[\\$\\d\\w]*\\s*:";
	private static final Pattern pattern = Pattern.compile(VARIABLE_PATTERN);
	private static List<String> variables = new ArrayList<String>();
	private static Matcher matcher;
	
	protected static void detectCurrentVariables(RuleBasicContentInfo contentInfo) {
		variables.clear();
		String lhs = "";
		List<RuleLineContentInfo> ruleLines = ((RuleLineContentInfo)contentInfo).getRule().getLHSRuleLines();
		for (RuleLineContentInfo ruleLineContentInfo : ruleLines)
			lhs = lhs.concat(ruleLineContentInfo.getContent());
		matcher = pattern.matcher(lhs);
		String varname;
		while (matcher.find()) {
			varname = matcher.group().replace(":", "").trim();
			addVariableName(varname);
		}
	}
	
	protected static boolean existsVariableWithSameName(String varname) {
		return variables.contains(varname);
	}
	
	private static void addVariableName(String variableName) {
		if (!variables.contains(variableName))
			variables.add(variableName);
	}

}
