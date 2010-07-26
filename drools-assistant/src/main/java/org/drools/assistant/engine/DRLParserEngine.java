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

package org.drools.assistant.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.assistant.info.RuleRefactorInfo;
import org.drools.assistant.info.drl.DRLContentTypeEnum;
import org.drools.assistant.info.drl.DRLRuleRefactorInfo;
import org.drools.assistant.info.drl.RuleLineContentInfo;

/**
 * A simple DRL parser implemented with regular expressions to get the offset of rule components
 * 
 * @author lucas
 *
 */
public class DRLParserEngine extends AbstractParserEngine {
	
	private static final String RULE_DECLARATION = "(rule|RULE)";
	private static final String PACKAGE_DECLARATION = "(package|PACKAGE)";
	private static final String IMPORT_DECLARATION = "(import|IMPORT)";
	private static final String GLOBAL_DECLARATION = "(global|GLOBAL)";
	private static final String RULE_WHEN_DECLARATION = "(when|WHEN)";
	private static final String RULE_THEN_DECLARATION = "(then|THEN)";
	private static final String RULE_END_DECLARATION = "(end|END)";
	
	private static final String OPTIONAL_TAB = "[\t]*";
	
	private static final String FULLY_QUALIFIED_NAME = "[\\w\\.]*";
	private static final String ONE_OR_MORE_SPACES = "[\\s]+";
	private static final String RULE_NAME = "\"[\\s\\w]*\"";
	
	// Regulars expressions to match DRL Rule 
	private static final String PACKAGE_PATTERN = PACKAGE_DECLARATION + ONE_OR_MORE_SPACES + FULLY_QUALIFIED_NAME + ";?"; // OK
	private static final String IMPORT_PATTERN = IMPORT_DECLARATION + ONE_OR_MORE_SPACES + FULLY_QUALIFIED_NAME + ";"; // OK
	private static final String GLOBAL_PATTERN = GLOBAL_DECLARATION + ONE_OR_MORE_SPACES + FULLY_QUALIFIED_NAME + ONE_OR_MORE_SPACES + "[\\w]*" + ONE_OR_MORE_SPACES + ""; // OK

	private static final String RULE_NAME_PATTERN = RULE_DECLARATION + ONE_OR_MORE_SPACES + RULE_NAME;
	private static final String RULE_LHS_PATTERN = "[\\t\\s]*" + RULE_WHEN_DECLARATION + ONE_OR_MORE_SPACES + OPTIONAL_TAB + "[\\w\\W]*" + "(?=" + RULE_THEN_DECLARATION + ")";
	private static final String RULE_RHS_PATTERN = "[\\t\\s]*" + RULE_THEN_DECLARATION + ONE_OR_MORE_SPACES + "[\\w\\W]*" + RULE_END_DECLARATION;
	
	private static final Pattern rulePattern = Pattern.compile("rule.+?end\\s*$", Pattern.MULTILINE | Pattern.DOTALL);
	
	public DRLParserEngine(String rule) {
		this.ruleRefactorInfo = new DRLRuleRefactorInfo();
		this.rule = rule;
	}
	
	public RuleRefactorInfo parse() {
		detectPackage(rule);
		detectGlobals(rule);
		detectImports(rule);
		detectRules(rule);
		return ruleRefactorInfo;
	}
	
	private void detectPackage(CharSequence rule) {
		pattern = Pattern.compile(PACKAGE_PATTERN);
		matcher = pattern.matcher(rule);
		if (matcher.find())
			((DRLRuleRefactorInfo) ruleRefactorInfo).addContent(DRLContentTypeEnum.PACKAGE, matcher.start(), matcher.group());
	}

	private void detectImports(CharSequence rule) {
		pattern = Pattern.compile(IMPORT_PATTERN);
		matcher = pattern.matcher(rule);
		while (matcher.find())
			((DRLRuleRefactorInfo) ruleRefactorInfo).addContent(DRLContentTypeEnum.IMPORT, matcher.start(), matcher.group());
	}
	
	private void detectGlobals(CharSequence rule) {
		pattern = Pattern.compile(GLOBAL_PATTERN);
		matcher = pattern.matcher(rule);
		while (matcher.find())
			((DRLRuleRefactorInfo) ruleRefactorInfo).addContent(DRLContentTypeEnum.GLOBAL, matcher.start(), matcher.group());
	}
	
	private void detectRules(CharSequence rule) {
		Matcher ruleMatcher = rulePattern.matcher(rule);
		while (ruleMatcher.find()) {
			for( int position = 0; position < ruleMatcher.groupCount()+1; position++ ){
				String value = ruleMatcher.group(position);
				int offset = ruleMatcher.start();
				String ruleName = detectRuleName(value);
				List<RuleLineContentInfo> lhs = detectLHS(value, offset);
				// TODO: remove this awful line... need to optimize the lhs regex
				lhs.remove(lhs.size()-1);
				List<RuleLineContentInfo> rhs = detectRHS(value, offset);
				((DRLRuleRefactorInfo) ruleRefactorInfo).addContent(DRLContentTypeEnum.RULE, offset, value, ruleName, lhs, rhs);
			}
		}
	}
	
	private String detectRuleName(CharSequence rule) {
		pattern = Pattern.compile(RULE_NAME_PATTERN);
		matcher = pattern.matcher(rule);
		if (matcher.find())
			return matcher.group();
		return null;
	}
	
	private List<RuleLineContentInfo> detectLHS(CharSequence rule, int ruleOffset) {
		pattern = Pattern.compile(RULE_LHS_PATTERN);
		matcher = pattern.matcher(rule);
		if (matcher.find())
			return detectLines(matcher.group(), matcher.start() + ruleOffset, DRLContentTypeEnum.RULE_LHS_LINE);
		return null;
	}
	
	private List<RuleLineContentInfo> detectRHS(CharSequence rule, int ruleOffset) {
		pattern = Pattern.compile(RULE_RHS_PATTERN);
		matcher = pattern.matcher(rule);
		if (matcher.find())
			return detectLines(matcher.group(), matcher.start() + ruleOffset, DRLContentTypeEnum.RULE_RHS_LINE);
		return null;
	}
	
	private List<RuleLineContentInfo> detectLines(CharSequence rule, int lineOffset, DRLContentTypeEnum type) {
		List<RuleLineContentInfo> ruleLines = new ArrayList<RuleLineContentInfo>();
		pattern = Pattern.compile(".*");
		matcher = pattern.matcher(rule);
		while (matcher.find()) {
			if (matcher.start()!=matcher.end())
				ruleLines.add(new RuleLineContentInfo(matcher.start()+lineOffset, matcher.group(), type));
		}
		return ruleLines;
	}

}
