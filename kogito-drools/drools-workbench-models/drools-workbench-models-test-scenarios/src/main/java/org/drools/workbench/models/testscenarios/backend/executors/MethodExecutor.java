/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.backend.executors;


import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.MVELSafeHelper;
import org.drools.workbench.models.testscenarios.shared.CallFieldValue;
import org.drools.workbench.models.testscenarios.shared.CallMethod;

public class MethodExecutor {

    private final Map<String, Object> populatedData;

    public MethodExecutor(Map<String, Object> populatedData) {
        this.populatedData = populatedData;
    }

    private String build(CallMethod callMethod, Map<String, Object> vars) {
        StringBuilder methodCallAsSting = new StringBuilder();
        methodCallAsSting.append("__fact__." + callMethod.getMethodName());
        methodCallAsSting.append("(");

        for (int i = 0; i < callMethod.getCallFieldValues().length; i++) {
            CallFieldValue field = callMethod.getCallFieldValues()[i];
            if (field.hasValue()) {
                String variableId = String.format("__val%d__", i);

                vars.put(
                        variableId,
                        getFieldValue(field));

                methodCallAsSting.append(variableId);

                if (isThisTheLastVariable(callMethod, i)) {
                    methodCallAsSting.append(",");
                }
            }
        }

        methodCallAsSting.append(")");

        return methodCallAsSting.toString();
    }

    private boolean isThisTheLastVariable(CallMethod callMethod, int i) {
        return i < callMethod.getCallFieldValues().length - 1;
    }

    private Object getFieldValue(CallFieldValue field) {
        Object val;
        if (isTheValueAPreviouslyDefinedObject(field)) {
            // eval the val into existence
            val = populatedData.get(field.value.substring(1));
        } else {
            val = field.value;
        }
        return val;
    }

    private boolean isTheValueAPreviouslyDefinedObject(CallFieldValue field) {
        return field.value.startsWith("=");
    }

    public Object executeMethod(CallMethod callMethod) {
        Map<String, Object> vars = initVars(callMethod);
        MVELSafeHelper.getEvaluator().eval(
                                        build(callMethod, vars),
                                        vars);

        return populatedData.get(callMethod.getVariable());
    }

    private Map<String, Object> initVars(CallMethod callMethod) {
        Map<String, Object> vars = new HashMap<String, Object>();

        vars.put(
                "__fact__",
                populatedData.get(callMethod.getVariable()));
        return vars;
    }
}
