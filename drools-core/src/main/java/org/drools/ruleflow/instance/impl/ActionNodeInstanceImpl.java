package org.drools.ruleflow.instance.impl;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.Serializable;
import java.util.Collections;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.ruleflow.core.ActionNode;
import org.drools.ruleflow.core.impl.DroolsConsequenceAction;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;
import org.mvel.ExpressionCompiler;
import org.mvel.MVEL;
import org.mvel.ParserContext;

/**
 * Runtime counterpart of an action node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ActionNodeInstanceImpl extends RuleFlowNodeInstanceImpl {

    protected ActionNode getActionNode() {
        return (ActionNode) getNode();
    }

    public void trigger(final RuleFlowNodeInstance from) {
		Object action = getActionNode().getAction();
		if (action instanceof DroolsConsequenceAction) {
			String actionString = ((DroolsConsequenceAction) action).getConsequence();
    		ExpressionCompiler compiler = new ExpressionCompiler(actionString);
    		ParserContext parserContext = new ParserContext();
    		Serializable expression = compiler.compile(parserContext);
    		DroolsMVELFactory factory = new DroolsMVELFactory(Collections.EMPTY_MAP, null, Collections.EMPTY_MAP);
    		MVEL.executeExpression(expression, null, factory);
		} else {
			throw new RuntimeException("Unknown action: " + action);
		}
    	triggerCompleted();
    }

    public void triggerCompleted() {
        getProcessInstance().getNodeInstance( getActionNode().getTo().getTo() ).trigger( this );
        getProcessInstance().removeNodeInstance(this);
    }

}