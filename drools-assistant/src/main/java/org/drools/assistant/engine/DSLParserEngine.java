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
