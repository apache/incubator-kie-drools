/**
 * Copyright 2010 JBoss Inc
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
