package org.drools.assistant.processor;

import java.util.List;

import org.drools.assistant.option.AssistantOption;

public abstract class AbstractRuleAssistantProcessor {
	
	public abstract List<AssistantOption> getRuleAssistant(String text, Integer offset);

}
