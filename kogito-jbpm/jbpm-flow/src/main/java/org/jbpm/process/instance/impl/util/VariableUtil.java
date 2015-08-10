package org.jbpm.process.instance.impl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.kie.api.runtime.process.NodeInstance;



public class VariableUtil {

    protected static final Pattern PARAMETER_MATCHER = Pattern.compile("#\\{([\\S&&[^\\}]]+)\\}", Pattern.DOTALL);
    
    public static String resolveVariable(String s, NodeInstance nodeInstance) {
        if (s == null) {
            return null;
        }
        
        Map<String, String> replacements = new HashMap<String, String>();
        Matcher matcher = PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (replacements.get(paramName) == null) {
                VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                        ((org.jbpm.workflow.instance.NodeInstance)nodeInstance).resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName);
                if (variableScopeInstance != null) {
                    Object variableValue = variableScopeInstance.getVariable(paramName);
                    String variableValueString = variableValue == null ? "" : variableValue.toString(); 
                    replacements.put(paramName, variableValueString);
                }
            }
        }
        for (Map.Entry<String, String> replacement: replacements.entrySet()) {
            s = s.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }
        
        return s;
    }
}
