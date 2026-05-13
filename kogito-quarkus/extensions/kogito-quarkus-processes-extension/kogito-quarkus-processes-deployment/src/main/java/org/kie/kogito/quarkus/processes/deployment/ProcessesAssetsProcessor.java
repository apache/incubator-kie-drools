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
package org.kie.kogito.quarkus.processes.deployment;

import org.kie.kogito.core.process.incubation.quarkus.support.QuarkusHumanTaskService;
import org.kie.kogito.core.process.incubation.quarkus.support.QuarkusProcessIdFactory;
import org.kie.kogito.core.process.incubation.quarkus.support.QuarkusStatefulProcessService;
import org.kie.kogito.core.process.incubation.quarkus.support.QuarkusStraightThroughProcessService;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorEventBody;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLAEventBody;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableEventBody;
import org.kie.kogito.quarkus.processes.workitems.QuarkusConfigResolver;
import org.kie.kogito.quarkus.workflow.KogitoBeanProducer;
import org.kie.kogito.quarkus.workflow.deployment.WorkflowProcessor;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

/**
 * Main class of the Kogito processes extension
 */
public class ProcessesAssetsProcessor extends WorkflowProcessor {

    @BuildStep
    @Override
    public FeatureBuildItem featureBuildItem() {
        return new FeatureBuildItem("kogito-processes");
    }

    @BuildStep
    public ReflectiveClassBuildItem eventsApiReflection() {
        return new ReflectiveClassBuildItem(true, true,
                ProcessInstanceErrorEventBody.class.getName(),
                ProcessInstanceErrorDataEvent.class.getName(),
                ProcessInstanceNodeEventBody.class.getName(),
                ProcessInstanceNodeDataEvent.class.getName(),
                ProcessInstanceSLAEventBody.class.getName(),
                ProcessInstanceSLADataEvent.class.getName(),
                ProcessInstanceStateEventBody.class.getName(),
                ProcessInstanceStateDataEvent.class.getName(),
                ProcessInstanceVariableEventBody.class.getName(),
                ProcessInstanceVariableDataEvent.class.getName(),
                UserTaskInstanceDataEvent.class.getName(),
                UserTaskInstanceAssignmentEventBody.class.getName(),
                UserTaskInstanceAssignmentDataEvent.class.getName(),
                UserTaskInstanceAttachmentEventBody.class.getName(),
                UserTaskInstanceAttachmentDataEvent.class.getName(),
                UserTaskInstanceCommentEventBody.class.getName(),
                UserTaskInstanceCommentDataEvent.class.getName(),
                UserTaskInstanceDeadlineEventBody.class.getName(),
                UserTaskInstanceDeadlineDataEvent.class.getName(),
                UserTaskInstanceStateEventBody.class.getName(),
                UserTaskInstanceStateDataEvent.class.getName(),
                UserTaskInstanceVariableEventBody.class.getName(),
                UserTaskInstanceVariableDataEvent.class.getName());

    }

    @BuildStep
    @Override
    public AdditionalBeanBuildItem additionalBeans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(
                        QuarkusStraightThroughProcessService.class,
                        QuarkusStatefulProcessService.class,
                        QuarkusHumanTaskService.class,
                        QuarkusProcessIdFactory.class,
                        KogitoBeanProducer.class,
                        QuarkusConfigResolver.class)
                .build();
    }

}
