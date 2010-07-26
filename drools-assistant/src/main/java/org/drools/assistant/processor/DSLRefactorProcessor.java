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
