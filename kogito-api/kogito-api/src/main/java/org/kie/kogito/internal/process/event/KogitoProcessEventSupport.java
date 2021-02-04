/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.internal.process.event;

import java.util.List;

import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.workitem.Transition;

public interface KogitoProcessEventSupport {

    void fireBeforeProcessStarted( ProcessInstance instance, KieRuntime kruntime );

    void fireAfterProcessStarted( ProcessInstance instance, KieRuntime kruntime );

    void fireBeforeProcessCompleted( ProcessInstance instance, KieRuntime kruntime );

    void fireAfterProcessCompleted( ProcessInstance instance, KieRuntime kruntime );

    void fireBeforeNodeTriggered( NodeInstance nodeInstance, KieRuntime kruntime );

    void fireAfterNodeTriggered( NodeInstance nodeInstance, KieRuntime kruntime );

    void fireBeforeNodeLeft( NodeInstance nodeInstance, KieRuntime kruntime );

    void fireAfterNodeLeft( NodeInstance nodeInstance, KieRuntime kruntime );

    void fireBeforeVariableChanged( String id, String instanceId, Object oldValue, Object newValue, List<String> tags,
                                    ProcessInstance processInstance, KogitoNodeInstance nodeInstance, KieRuntime kruntime );

    void fireAfterVariableChanged( String name, String id,Object oldValue, Object newValue, List<String> tags,
                                   ProcessInstance processInstance, KogitoNodeInstance nodeInstance, KieRuntime kruntime );

    void fireBeforeSLAViolated( ProcessInstance instance, KieRuntime kruntime );

    void fireAfterSLAViolated( ProcessInstance instance, KieRuntime kruntime );

    void fireBeforeSLAViolated( ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime );

    void fireAfterSLAViolated( ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime );

    void fireBeforeWorkItemTransition( ProcessInstance instance, KogitoWorkItem workitem, Transition<?> transition, KieRuntime kruntime );

    void fireAfterWorkItemTransition( ProcessInstance instance, KogitoWorkItem workitem, Transition<?> transition, KieRuntime kruntime );

    void fireOnSignal( ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime, String signalName, Object signalObject );

    void fireOnMessage( ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime, String messageName, Object messageObject );

    void reset();
}