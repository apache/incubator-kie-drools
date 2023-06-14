/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.python;

import java.util.Collections;
import java.util.Map;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;

import jep.Interpreter;
import jep.SharedInterpreter;

import static org.kie.kogito.serverless.workflow.SWFConstants.PYTHON;
import static org.kie.kogito.serverless.workflow.SWFConstants.SCRIPT;

public class PythonWorkItemHandler extends WorkflowWorkItemHandler {
    private static ThreadLocal<Interpreter> interpreter = new ThreadLocal<>();

    private static Interpreter interpreter() {
        Interpreter py = interpreter.get();
        if (py == null) {
            py = new SharedInterpreter();
            interpreter.set(py);
        }
        return py;
    }

    @Override
    public String getName() {
        return PYTHON;
    }

    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters) {
        Interpreter py = interpreter();
        String source = (String) parameters.remove(SCRIPT);
        parameters.forEach(py::set);
        py.exec(source);
        return Collections.emptyMap();
    }

    public static Object getValue(String key) {
        return interpreter().getValue(key);
    }

    public void close() {
        Interpreter py = interpreter.get();
        if (py != null) {
            interpreter.remove();
            py.close();
        }
    }
}
