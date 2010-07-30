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

package org.drools.command;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;

public interface CommandFactoryService {
    Command newInsert(Object object);

    Command newInsert(Object object,
                      String outIdentifier,
                      String entryPoint);

    Command newInsertElements(Collection objects);
    
    Command newInsertElements(Collection objects, String outIdentifier, boolean returnObject, String entryPoint);

    Command newRetract(FactHandle factHandle);

    Setter newSetter(String accessor,
                     String value);

    Command newModify(FactHandle factHandle,
                      List<Setter> setters);

    Command newFireAllRules();

    Command newFireAllRules(int max);

    Command newGetObject(FactHandle factHandle);

    Command newGetObjects();

    Command newGetObjects(ObjectFilter filter);

    Command newSetGlobal(String identifie,
                         Object object);

    Command newSetGlobal(String identifier,
                         Object object,
                         boolean out);

    Command newSetGlobal(String identifier,
                         Object object,
                         String outIdentifier);

    Command newGetGlobal(String identifier);

    Command newGetGlobal(String identifier,
                         String outIdentifier);

    Command newStartProcess(String processId);

    Command newStartProcess(String processId,
                            Map<String, Object> parameters);

    Command newSignalEvent(String type,
                           Object event);

    Command newSignalEvent(long processInstanceId,
                           String type,
                           Object event);

    Command newCompleteWorkItem(long workItemId,
                                Map<String, Object> results);

    Command newQuery(String identifier,
                     String name);

    Command newQuery(String identifier,
                     String name,
                     Object[] arguments);

    BatchExecutionCommand newBatchExecution(List< ? extends Command> commands, String lookup);

    Command newAbortWorkItem(long workItemId);

}
