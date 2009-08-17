package org.drools.assistant.processor;

import java.util.List;

import org.drools.assistant.engine.DSLParserEngine;
import org.drools.assistant.info.RuleRefactorInfo;
import org.drools.assistant.option.AssistantOption;
import org.drools.assistant.refactor.DSLRuleRefactor;

public class DSLRefactorProcessor extends AbstractRuleRefactorProcessor {

	@Override
	public List<AssistantOption> getRuleAssistant(String text, Integer offset) {
		RuleRefactorInfo ruleRefactorInfo = this.generateRuleRefactorInfo(text);
		ruleRefactorEngine = new DSLRuleRefactor(ruleRefactorInfo);
		return ruleRefactorEngine.execute(offset);
	}
	
	@Override
	protected RuleRefactorInfo generateRuleRefactorInfo(String text) {
		ruleParserEngine = new DSLParserEngine(text);
		return ruleParserEngine.parse();
	}

}
