/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.serverless.workflow.python;

import java.util.Collection;
import java.util.Map;

import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.serverless.workflow.ServiceWorkItemHandler;

import com.fasterxml.jackson.databind.JsonNode;

import jep.Interpreter;

import static org.kie.kogito.serverless.workflow.SWFConstants.PYTHON_SVC;
import static org.kie.kogito.serverless.workflow.python.PythonWorkItemHandlerUtils.closeInterpreter;
import static org.kie.kogito.serverless.workflow.python.PythonWorkItemHandlerUtils.interpreter;

public class PythonServiceWorkItemHandler extends ServiceWorkItemHandler {

    @Override
    public String getName() {
        return PYTHON_SVC;
    }

    public void close() {
        closeInterpreter();
    }

    @Override
    protected Object invoke(String moduleName, String methodName,
            Object parameters) {
        Interpreter py = interpreter();
        // make sure module is imported
        py.exec("import " + moduleName);
        final String funcName = moduleName + "." + methodName;
        Object result;

        if (parameters instanceof JsonNode) {
            parameters = JsonObjectUtils.toJavaValue((JsonNode) parameters);
        }
        if (parameters instanceof Map) {
            // keyword args call
            result = py.invoke(funcName, (Map<String, Object>) parameters);
        } else if (parameters instanceof Collection) {
            // multiple unnamed parameters call
            result = py.invoke(funcName, ((Collection) parameters).toArray());
        } else {
            //single parameter call
            result = py.invoke(funcName, parameters);
        }
        return result;
    }
}
