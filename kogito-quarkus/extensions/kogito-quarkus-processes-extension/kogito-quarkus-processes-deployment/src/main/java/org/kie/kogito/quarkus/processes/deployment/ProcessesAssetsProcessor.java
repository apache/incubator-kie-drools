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
package org.kie.kogito.quarkus.processes.deployment;

import org.kie.kogito.core.process.incubation.quarkus.support.QuarkusHumanTaskService;
import org.kie.kogito.core.process.incubation.quarkus.support.QuarkusProcessIdFactory;
import org.kie.kogito.core.process.incubation.quarkus.support.QuarkusStatefulProcessService;
import org.kie.kogito.core.process.incubation.quarkus.support.QuarkusStraightThroughProcessService;
import org.kie.kogito.event.process.AttachmentEventBody;
import org.kie.kogito.event.process.CommentEventBody;
import org.kie.kogito.event.process.MilestoneEventBody;
import org.kie.kogito.event.process.NodeInstanceEventBody;
import org.kie.kogito.event.process.ProcessDataEvent;
import org.kie.kogito.event.process.ProcessErrorEventBody;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceEventBody;
import org.kie.kogito.event.process.UserTaskDeadlineDataEvent;
import org.kie.kogito.event.process.UserTaskDeadlineEventBody;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceEventBody;
import org.kie.kogito.event.process.VariableInstanceDataEvent;
import org.kie.kogito.event.process.VariableInstanceEventBody;
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
                AttachmentEventBody.class.getName(),
                CommentEventBody.class.getName(),
                MilestoneEventBody.class.getName(),
                NodeInstanceEventBody.class.getName(),
                ProcessDataEvent.class.getName(),
                ProcessErrorEventBody.class.getName(),
                ProcessInstanceDataEvent.class.getName(),
                ProcessInstanceEventBody.class.getName(),
                UserTaskDeadlineDataEvent.class.getName(),
                UserTaskDeadlineEventBody.class.getName(),
                UserTaskInstanceDataEvent.class.getName(),
                UserTaskInstanceEventBody.class.getName(),
                VariableInstanceDataEvent.class.getName(),
                VariableInstanceEventBody.class.getName());
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
                        KogitoBeanProducer.class)
                .build();
    }

}
