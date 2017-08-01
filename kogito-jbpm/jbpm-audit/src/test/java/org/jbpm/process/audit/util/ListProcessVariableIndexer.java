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

package org.jbpm.process.audit.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.internal.process.ProcessVariableIndexer;

/**
 * Simple (test purpose) linked list process variable indexer that creates variable log
 * for each entry of the list where its name is suffixed with index number e.g. [0] 
 *
 */
public class ListProcessVariableIndexer implements ProcessVariableIndexer {

    @Override
    public boolean accept(Object variable) {
        if (variable instanceof LinkedList<?>) {
            return true;
        }
        return false;
    }

    @Override
    public List<VariableInstanceLog> index(String name, Object variable) {
        List<VariableInstanceLog> indexed = new ArrayList<VariableInstanceLog>();
        
        List<?> listVariable = (List<?>) variable;
        int index = 0;
        
        for (Object listElement : listVariable) {
        
            org.jbpm.process.audit.VariableInstanceLog processVariable = new org.jbpm.process.audit.VariableInstanceLog();
            processVariable.setVariableId(name +"[" + index + "]");
            processVariable.setValue(listElement.toString());
            
            indexed.add(processVariable);
            index++;
        }
        
        return indexed;
    }

}
