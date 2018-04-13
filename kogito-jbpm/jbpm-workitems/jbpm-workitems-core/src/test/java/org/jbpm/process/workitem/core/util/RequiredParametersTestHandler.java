/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.workitem.core.util;

import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

@Wid(widfile = "TestHandler.wid", name = "TestHandler",
        displayName = "TestHandler",
        defaultHandler = "mvel: org.jbpm.process.workitem.core.util.RequiredParametersTestHandler()",
        parameters = {
                @WidParameter(name = "firstParam", required = true),
                @WidParameter(name = "secondParam"),
                @WidParameter(name = "thirdParam", required = true),
        })
public class RequiredParametersTestHandler extends AbstractLogOrThrowWorkItemHandler {

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager workItemManager) {
        try {
            RequiredParameterValidator.validate(this.getClass(),
                                                workItem);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public void abortWorkItem(WorkItem wi,
                              WorkItemManager wim) {
    }
}
