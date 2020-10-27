/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance.impl.actions;

import java.io.Serializable;

import org.jbpm.process.instance.impl.Action;
import org.kie.api.runtime.process.ProcessContext;

public class ProcessInstanceCompensationAction implements Action, Serializable {

    private static final long serialVersionUID = 1L;

    private final String activityRef;

    public ProcessInstanceCompensationAction(String activityRef) {
        this.activityRef = activityRef;
    }

    public void execute(ProcessContext context) throws Exception {
        context.getProcessInstance().signalEvent("Compensation", activityRef);
    }

    public String getActivityRef() {
        return activityRef;
    }
}
