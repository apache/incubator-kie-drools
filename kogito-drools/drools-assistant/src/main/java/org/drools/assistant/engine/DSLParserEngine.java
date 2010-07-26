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

import java.util.regex.Pattern;

import org.drools.assistant.info.RuleRefactorInfo;
import org.drools.assistant.info.dsl.DSLRuleRefactorInfo;

public class DSLParserEngine extends AbstractParserEngine {
	
	private static final String WHEN_PATTERN = "(\\[when\\]|\\[consequence\\]).*";
	private static final String THEN_PATTERN = "\\[then\\].*";

	
	public DSLParserEngine(String rule) {
		this.ruleRefactorInfo = new DSLRuleRefactorInfo();
		this.rule = rule;
	}
	
	@Override
	public RuleRefactorInfo parse() {
		detectWHEN();
		detectTHEN();
		return ruleRefactorInfo;
	}

	
	private void detectWHEN() {
		pattern = Pattern.compile(WHEN_PATTERN);
		matcher = pattern.matcher(rule);
		System.out.println("--------------when--------------");
		while (matcher.find())
			System.out.println(matcher.group() + "\n\tstart at index " + matcher.start());
	}
	
	private void detectTHEN() {
		pattern = Pattern.compile(THEN_PATTERN);
		matcher = pattern.matcher(rule);
		System.out.println("--------------then--------------");
		while (matcher.find())
			System.out.println(matcher.group() + "\n\tstart at index " + matcher.start());
	}
	
}
