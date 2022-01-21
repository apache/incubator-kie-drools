/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2.test;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.instance.WorkItemManagerFactory;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.kie.kogito.process.workitems.InternalKogitoWorkItemManager;
import org.kie.kogito.process.workitems.impl.KogitoDefaultWorkItemManager;

public class TestWorkItemManagerFactory implements WorkItemManagerFactory {

    public InternalKogitoWorkItemManager createWorkItemManager(InternalKnowledgeRuntime kruntime) {
        return new KogitoDefaultWorkItemManager(InternalProcessRuntime.asKogitoProcessRuntime(kruntime));
    }

}
