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

import org.kie.kogito.serverless.workflow.utils.ConfigResolverHolder;

import jep.Interpreter;
import jep.SharedInterpreter;
import jep.python.PyObject;

public class PythonWorkItemHandlerUtils {

    private PythonWorkItemHandlerUtils() {
    }

    public static final String SEARCH_PATH_PROPERTY = "org.sonataflow.python.searchpath";
    private static final String PYTHON_SYS_PATH = "sys.path.append('%s')\n";

    private static final ThreadLocal<Interpreter> interpreter = new ThreadLocal<>();

    protected static Interpreter interpreter() {
        Interpreter py = interpreter.get();
        if (py == null) {
            py = new SharedInterpreter();
            interpreter.set(py);
            Collection<String> searchPath = ConfigResolverHolder.getConfigResolver().getIndexedConfigProperty(SEARCH_PATH_PROPERTY, String.class);
            if (!searchPath.isEmpty()) {
                StringBuilder sb = new StringBuilder("import sys\n");
                searchPath.forEach(path -> sb.append(String.format(PYTHON_SYS_PATH, path)));
                py.exec(sb.toString());
            }
        }
        return py;
    }

    protected static void closeInterpreter() {
        Interpreter py = interpreter.get();
        if (py != null) {
            interpreter.remove();
            py.close();
        }
    }

    protected static Object getValue(String key) {
        Object value = interpreter().getValue(key);
        return value instanceof PyObject ? new PyObject2JsonNode((PyObject) value) : value;
    }
}
