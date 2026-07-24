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
package org.kie.kogito.jobs.service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.exception.JobValidationException;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobDetailsBuilder;
import org.kie.kogito.jobs.service.model.Recipient;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.timer.impl.PointInTimeTrigger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class JobDetailsValidatorTest {

    private static final String CALLBACK_ENDPOINT = "http://localhost:8080/callback";
    private static final String ID = "id";

    private JobDetailsValidator jobDetailsValidator;
    private Recipient recipient;
    private RecipientInstanceValidator recipientInstanceValidator;

    @BeforeEach
    void setUp() {
        recipientInstanceValidator = mock(RecipientInstanceValidator.class);
        jobDetailsValidator = new JobDetailsValidator(recipientInstanceValidator);
        recipient = new RecipientInstance(HttpRecipient.builder()
                .forStringPayload().url(CALLBACK_ENDPOINT)
                .payload(HttpRecipientStringPayloadData.from("{\"name\":\"Arthur\"}"))
                .build());
    }

    @Test
    void testValidateSuccess() {
        JobDetails job = new JobDetailsBuilder()
                .id(ID)
                .correlationId(ID)
                .recipient(recipient)
                .trigger(new PointInTimeTrigger())
                .build();
        assertThat(jobDetailsValidator.validateToCreate(job)).isEqualTo(job);
    }

    @Test
    void testValidateMissingId() {
        JobDetails job = new JobDetailsBuilder()
                .correlationId(ID)
                .recipient(recipient)
                .trigger(new PointInTimeTrigger())
                .build();
        assertThatThrownBy(() -> jobDetailsValidator.validateToCreate(job)).isInstanceOf(JobValidationException.class);
    }

    @Test
    void testValidateMissingPayload() {
        JobDetails job = new JobDetailsBuilder()
                .recipient(recipient)
                .trigger(new PointInTimeTrigger())
                .build();
        assertThatThrownBy(() -> jobDetailsValidator.validateToCreate(job)).isInstanceOf(JobValidationException.class);
    }

    @Test
    void testValidateMissingRecipientEndpoint() {
        JobDetails job = new JobDetailsBuilder()
                .id(ID)
                .correlationId(ID)
                .trigger(new PointInTimeTrigger())
                .build();
        assertThatThrownBy(() -> jobDetailsValidator.validateToCreate(job)).isInstanceOf(JobValidationException.class);
    }

    @Test
    void testValidateMissingRecipient() {
        JobDetails job = new JobDetailsBuilder()
                .id(ID)
                .correlationId(ID)
                .recipient(null)
                .trigger(new PointInTimeTrigger())
                .build();
        assertThatThrownBy(() -> jobDetailsValidator.validateToCreate(job)).isInstanceOf(JobValidationException.class);
    }

    @Test
    void testValidateMissingTrigger() {
        JobDetails job = new JobDetailsBuilder()
                .id(ID)
                .correlationId(ID)
                .recipient(recipient)
                .build();
        assertThatThrownBy(() -> jobDetailsValidator.validateToCreate(job)).isInstanceOf(JobValidationException.class);
    }

    @Test
    void testValidateToMergeSuccess() {
        JobDetails job = new JobDetailsBuilder()
                .trigger(new PointInTimeTrigger())
                .build();
        assertThat(jobDetailsValidator.validateToMerge(job)).isEqualTo(job);
    }

    @Test
    void testValidateToMergeWithId() {
        JobDetails job = new JobDetailsBuilder()
                .id(ID)
                .correlationId(ID)
                .trigger(new PointInTimeTrigger())
                .build();
        assertThatThrownBy(() -> jobDetailsValidator.validateToMerge(job)).isInstanceOf(JobValidationException.class);
    }

    @Test
    void testValidateToMergeWithRecipient() {
        JobDetails job = new JobDetailsBuilder()
                .recipient(recipient)
                .trigger(new PointInTimeTrigger())
                .build();
        assertThatThrownBy(() -> jobDetailsValidator.validateToMerge(job)).isInstanceOf(JobValidationException.class);
    }
}