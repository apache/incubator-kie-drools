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

package org.jbpm.bpmn2.xpath;

//import javax.script.Bindings;
//import javax.script.ScriptEngine;
//import javax.script.SimpleBindings;

import org.drools.runtime.process.ProcessContext;
import org.jbpm.process.instance.impl.ReturnValueEvaluator;

public class XPathReturnValueEvaluator implements ReturnValueEvaluator {
    
//    private static XPathScriptEngineFactory FACTORY = new XPathScriptEngineFactory();

//    private String expression;
    
    public void setExpression(String expression) {
//        this.expression = expression;
    }

    public Object evaluate(ProcessContext processContext) throws Exception {
//        ScriptEngine engine = FACTORY.getScriptEngine();
//        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
//            ((ProcessInstance) processContext.getProcessInstance())
//                .getContextInstance(VariableScope.VARIABLE_SCOPE);
//        if (variableScopeInstance != null) {
//            // TODO include other scopes that process-level scope as well
//            Bindings bindings = new SimpleBindings(variableScopeInstance.getVariables());
//            return engine.eval(expression, bindings);
//        } else {
//            return engine.eval(expression);
//        }
    	throw new UnsupportedOperationException("XPath expressions not supported");
    }

}
