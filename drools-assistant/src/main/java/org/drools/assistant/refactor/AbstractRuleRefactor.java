package org.drools.assistant.refactor;

import java.util.List;

import org.drools.assistant.info.RuleRefactorInfo;
import org.drools.assistant.info.drl.RuleBasicContentInfo;
import org.drools.assistant.option.AssistantOption;

public abstract class AbstractRuleRefactor {

	protected RuleRefactorInfo ruleRefactorInfo;
	protected List<AssistantOption> options;
	protected AssistantOption option;
	protected int offset;
	
	public abstract List<AssistantOption> execute(int offset);
	
	protected abstract AssistantOption bindVariable(RuleBasicContentInfo contentInfo);
	
	protected abstract AssistantOption fixImports(RuleBasicContentInfo contentInfo);
	
	protected abstract AssistantOption renameVariable(RuleBasicContentInfo contentInfo);

}
