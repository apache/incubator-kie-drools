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
package org.kie.kogito.job.sink.recipient.deployment;

import org.kie.kogito.job.sink.recipient.SinkJobExecutor;
import org.kie.kogito.job.sink.recipient.SinkRecipientValidator;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientPayloadData;
import org.kie.kogito.jobs.service.api.schedule.cron.CronSchedule;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;

import io.cloudevents.SpecVersion;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class JobSinkRecipientProcessor {

    private static final String FEATURE = "job-sink-recipient";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem additionalBeans() {
        return new AdditionalBeanBuildItem(SinkJobExecutor.class, SinkRecipientValidator.class);
    }

    @BuildStep
    void contributeClassesToIndex(BuildProducer<AdditionalIndexedClassesBuildItem> additionalIndexedClasses) {
        // Ensure SinkRecipient related classes that represents Schema components, and that are not referenced directly
        // in the Jobs Service JAX-RS resources, are present in the index so that they can be picked up by the OpenAPI
        // annotations scanning. Otherwise, they won't be part of the generated OpenAPI document.
        additionalIndexedClasses.produce(new AdditionalIndexedClassesBuildItem(
                SinkRecipient.class.getName(),
                SinkRecipientPayloadData.class.getName(),
                SinkRecipientBinaryPayloadData.class.getName(),
                SinkRecipientJsonPayloadData.class.getName(),
                SinkRecipient.ContentMode.class.getName(),
                CronSchedule.class.getName(),
                TimerSchedule.class.getName(),
                TemporalUnit.class.getName(),
                SpecVersion.class.getName()));
    }
}
