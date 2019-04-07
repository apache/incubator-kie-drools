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

package org.jbpm.services.task.audit.service;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.services.task.audit.impl.model.TaskVariableImpl;
import org.jbpm.services.task.audit.service.objects.Person;
import org.kie.internal.task.api.TaskVariable;
import org.kie.internal.task.api.TaskVariableIndexer;


public class PersonTaskVariablesIndexer implements TaskVariableIndexer {

    @Override
    public boolean accept(Object variable) {
        if (variable instanceof Person) {
            return true;
        }
        return false;
    }

    @Override
    public List<TaskVariable> index(String name, Object variable) {
        
        Person person = (Person) variable;
        List<TaskVariable> indexed = new ArrayList<TaskVariable>();
        
        TaskVariableImpl personNameVar = new TaskVariableImpl();
        personNameVar.setName("person.name");
        personNameVar.setValue(person.getName());
        
        indexed.add(personNameVar);
        
        TaskVariableImpl personAgeVar = new TaskVariableImpl();
        personAgeVar.setName("person.age");
        personAgeVar.setValue(person.getAge()+"");
        
        indexed.add(personAgeVar);
        
        return indexed;
    }

}
