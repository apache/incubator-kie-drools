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
package org.kie.kogito.job.http.recipient.deployment;

import org.junit.jupiter.api.Test;
import org.kie.kogito.job.http.recipient.HttpJobExecutor;
import org.kie.kogito.job.http.recipient.HttpRecipientValidator;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.api.schedule.cron.CronSchedule;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class JobHttpRecipientProcessorTest {

    private final JobHttpRecipientProcessor processor = new JobHttpRecipientProcessor();

    @Test
    void feature() {
        assertThat(processor.feature().getName()).isEqualTo("job-http-recipient");
    }

    @Test
    void additionalBeans() {
        AdditionalBeanBuildItem additionalBeans = processor.additionalBeans();
        assertThat(additionalBeans.getBeanClasses()).containsExactlyInAnyOrder(
                HttpJobExecutor.class.getName(),
                HttpRecipientValidator.class.getName());
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
                HttpRecipient.class.getName(),
                HttpRecipientPayloadData.class.getName(),
                HttpRecipientStringPayloadData.class.getName(),
                HttpRecipientBinaryPayloadData.class.getName(),
                HttpRecipientJsonPayloadData.class.getName(),
                CronSchedule.class.getName(),
                TimerSchedule.class.getName(),
                TemporalUnit.class.getName());
    }
}
