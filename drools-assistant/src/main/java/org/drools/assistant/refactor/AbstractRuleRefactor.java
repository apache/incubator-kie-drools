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
