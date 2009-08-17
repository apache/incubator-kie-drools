package org.drools.assistant.engine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.assistant.info.RuleRefactorInfo;

public abstract class AbstractParserEngine {
	
	protected RuleRefactorInfo ruleRefactorInfo;
	protected Pattern pattern;
	protected Matcher matcher;
	protected String rule;
	
	public abstract RuleRefactorInfo parse();

}
