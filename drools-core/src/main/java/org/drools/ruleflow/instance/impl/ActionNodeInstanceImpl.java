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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
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
    		// imports
    		List imports = getProcessInstance().getRuleFlowProcess().getImports();
    		if (imports != null) {
    			for (Iterator iterator = imports.iterator(); iterator.hasNext(); ) {
    				String importClassName = (String) iterator.next();
    				if ( importClassName.endsWith( ".*" ) ) {
    					importClassName = importClassName.substring(0, importClassName.indexOf(".*"));
    		            parserContext.addPackageImport(importClassName);
    		        } else {
	    				try {
	    					parserContext.addImport(Class.forName(importClassName));
	    				} catch (ClassNotFoundException e) {
	    					// class not found, do nothing
	    				}
    		        }
    			}
    		}
    		Set importSet = new HashSet();
    		importSet.addAll(imports);
    		TypeResolver typeResolver = new ClassTypeResolver(importSet, Thread.currentThread().getContextClassLoader());
    		// compile expression
    		Serializable expression = compiler.compile(parserContext);
    		// globals
    		Map globalDefs = getProcessInstance().getRuleFlowProcess().getGlobals();
    		Map globals = new HashMap();
    		if (globalDefs != null) {
    			for (Iterator iterator = globalDefs.entrySet().iterator(); iterator.hasNext(); ) {
    				Map.Entry entry = (Map.Entry) iterator.next();
    				try {
    					globals.put(entry.getKey(), typeResolver.resolveType((String) entry.getValue()));
    				} catch (ClassNotFoundException exc) {
    					throw new IllegalArgumentException("Could not find type " + entry.getValue() + " of global " + entry.getKey());
    				}
    			}
    		}
    		// execute
    		DroolsMVELFactory factory = new DroolsMVELFactory(Collections.EMPTY_MAP, null, globals);
    		factory.setContext(null, null, null, getProcessInstance().getWorkingMemory(), null);
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