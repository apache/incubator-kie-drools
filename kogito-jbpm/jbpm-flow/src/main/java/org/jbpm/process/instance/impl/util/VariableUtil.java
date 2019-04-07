/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance.impl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.kie.api.runtime.process.NodeInstance;



public class VariableUtil {
    public static String resolveVariable(String s, NodeInstance nodeInstance) {
        if (s == null) {
            return null;
        }
        
        Map<String, String> replacements = new HashMap<String, String>();
        Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (replacements.get(paramName) == null) {
                VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                        ((org.jbpm.workflow.instance.NodeInstance)nodeInstance).resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName);
                if (variableScopeInstance != null) {
                    Object variableValue = variableScopeInstance.getVariable(paramName);
                    String variableValueString = variableValue == null ? "" : variableValue.toString(); 
                    replacements.put(paramName, variableValueString);
                } else {
                    try {
                        Object variableValue = MVELSafeHelper.getEvaluator().eval(paramName, new NodeInstanceResolverFactory((org.jbpm.workflow.instance.NodeInstance) nodeInstance));
                        String variableValueString = variableValue == null ? "" : variableValue.toString(); 
                        replacements.put(paramName, variableValueString);
                    } catch (Throwable t) {
                        
                    }
                }
            }
        }
        for (Map.Entry<String, String> replacement: replacements.entrySet()) {
            s = s.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }
        
        return s;
    }
}
