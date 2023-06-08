/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.job.sink.recipient.deployment;

import org.junit.jupiter.api.Test;
import org.kie.kogito.job.sink.recipient.SinkJobExecutor;
import org.kie.kogito.job.sink.recipient.SinkRecipientValidator;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientPayloadData;
import org.kie.kogito.jobs.service.api.schedule.cron.CronSchedule;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import io.cloudevents.SpecVersion;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class JobSinkRecipientProcessorTest {

    private final JobSinkRecipientProcessor processor = new JobSinkRecipientProcessor();

    @Test
    void feature() {
        assertThat(processor.feature().getName()).isEqualTo("job-sink-recipient");
    }

    @Test
    void additionalBeans() {
        AdditionalBeanBuildItem additionalBeans = processor.additionalBeans();
        assertThat(additionalBeans.getBeanClasses()).containsExactlyInAnyOrder(
                SinkJobExecutor.class.getName(),
                SinkRecipientValidator.class.getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    void contributeClassesToIndex() {
        BuildProducer<AdditionalIndexedClassesBuildItem> producer = Mockito.mock(BuildProducer.class);
        ArgumentCaptor<AdditionalIndexedClassesBuildItem> captor = ArgumentCaptor.forClass(AdditionalIndexedClassesBuildItem.class);
        processor.contributeClassesToIndex(producer);
        verify(producer).produce(captor.capture());
        AdditionalIndexedClassesBuildItem buildItem = captor.getValue();
        assertThat(buildItem).isNotNull();
        assertThat(buildItem.getClassesToIndex()).containsExactlyInAnyOrder(
                SinkRecipient.class.getName(),
                SinkRecipientPayloadData.class.getName(),
                SinkRecipientBinaryPayloadData.class.getName(),
                SinkRecipientJsonPayloadData.class.getName(),
                SinkRecipient.ContentMode.class.getName(),
                CronSchedule.class.getName(),
                TimerSchedule.class.getName(),
                TemporalUnit.class.getName(),
                SpecVersion.class.getName());
    }
}
