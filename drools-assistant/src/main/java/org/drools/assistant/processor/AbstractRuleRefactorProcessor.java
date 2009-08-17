package org.drools.assistant.processor;

import org.drools.assistant.engine.AbstractParserEngine;
import org.drools.assistant.info.RuleRefactorInfo;
import org.drools.assistant.refactor.AbstractRuleRefactor;

public abstract class AbstractRuleRefactorProcessor extends AbstractRuleAssistantProcessor {

	protected AbstractRuleRefactor ruleRefactorEngine;
	protected AbstractParserEngine ruleParserEngine;
	
	protected abstract RuleRefactorInfo generateRuleRefactorInfo(String text);
	
}
