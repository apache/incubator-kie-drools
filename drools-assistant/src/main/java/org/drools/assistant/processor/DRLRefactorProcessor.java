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

package org.drools.assistant.processor;

import java.util.List;

import org.drools.assistant.engine.DRLParserEngine;
import org.drools.assistant.info.RuleRefactorInfo;
import org.drools.assistant.option.AssistantOption;
import org.drools.assistant.option.RenameAssistantOption;
import org.drools.assistant.refactor.DRLRuleRefactor;
import org.drools.assistant.refactor.drl.VariableRename;

public class DRLRefactorProcessor extends AbstractRuleRefactorProcessor {
	
	private static DRLRefactorProcessor instance;
	
	@Override
	public List<AssistantOption> getRuleAssistant(String text, Integer offset) {
		RuleRefactorInfo ruleRefactorInfo = generateRuleRefactorInfo(text);
		ruleRefactorEngine = new DRLRuleRefactor(ruleRefactorInfo);
		return ruleRefactorEngine.execute(offset);
	}
	
	@Override
	protected RuleRefactorInfo generateRuleRefactorInfo(String text) {
		ruleParserEngine = new DRLParserEngine(text);
		RuleRefactorInfo info = ruleParserEngine.parse();
		return info;
	}
	
	public AssistantOption executeVariableRename(RenameAssistantOption assistantOption, String newVariableName) {
		return VariableRename.execute(assistantOption, newVariableName);
	}


	public static DRLRefactorProcessor getInstance() {
		if (instance==null)
			instance = new DRLRefactorProcessor();
		return instance;
	}

}
