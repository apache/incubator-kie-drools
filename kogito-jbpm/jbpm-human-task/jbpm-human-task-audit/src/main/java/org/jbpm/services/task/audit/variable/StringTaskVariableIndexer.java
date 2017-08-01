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

package org.jbpm.services.task.audit.variable;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.services.task.audit.impl.model.TaskVariableImpl;
import org.kie.internal.task.api.TaskVariable;
import org.kie.internal.task.api.TaskVariableIndexer;

/**
 * Simple and default indexer for all task variables. It will keep the same name of the variable
 * and will generate string representation based on <code>variable.toString()</code> method
 *
 * this indexer is always added at the end of the chain of indexers to allow custom implementations take
 * precedence over this one.
 */
public class StringTaskVariableIndexer implements TaskVariableIndexer {

    @Override
    public boolean accept(Object variable) {
        return true;
    }

    @Override
    public List<TaskVariable> index(String name, Object variable) {
        List<TaskVariable> indexed = new ArrayList<TaskVariable>();
        
        TaskVariableImpl taskVariable = new TaskVariableImpl();
        taskVariable.setName(name);
        taskVariable.setValue(variable.toString());
        
        indexed.add(taskVariable);
        
        return indexed;
    }

}
