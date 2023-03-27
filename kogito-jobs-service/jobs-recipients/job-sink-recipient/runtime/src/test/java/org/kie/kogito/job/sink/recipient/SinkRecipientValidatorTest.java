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

package org.kie.kogito.job.sink.recipient;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.kie.kogito.jobs.service.validation.ValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SinkRecipientValidatorTest {

    private static final long MAX_TIMEOUT = 3000;
    public static final String SINK_URL = "http://my_sink-url";
    public static final String CE_TYPE = "MY_CE_TYPE";
    public static final URI CE_SOURCE = URI.create("http://my_ce_source");

    public static final String MY_EXTENSION = "myextension";

    private SinkRecipientValidator validator;

    private SinkRecipient<?> recipient;

    @BeforeEach
    public void setUp() throws Exception {
        validator = new SinkRecipientValidator(MAX_TIMEOUT);
        recipient = new SinkRecipient<>();
        recipient.setSinkUrl(SINK_URL);
        recipient.setCeType(CE_TYPE);
        recipient.setCeSource(CE_SOURCE);
        recipient.addCeExtension(MY_EXTENSION, "some value");
    }

    @Test
    void acceptNonNull() {
        assertThat(validator.accept(recipient)).isTrue();
    }

    @Test
    void acceptNull() {
        assertThat(validator.accept(null)).isFalse();
    }

    @Test
    void validateSuccessful() {
        assertThatNoException().isThrownBy(() -> validator.validate(recipient, new ValidatorContext()));
    }

    @Test
    void validateNull() {
        recipient = null;
        testUnsuccessfulValidation("Recipient must be a non-null instance of");
    }

    @Test
    void validateNullSinkURL() {
        recipient.setSinkUrl(null);
        testUnsuccessfulValidation("SinkRecipient sinkUrl must have a non empty value.");
    }

    @Test
    void validateMalformedSinkURL() {
        recipient.setSinkUrl("bad url");
        testUnsuccessfulValidation("SinkRecipient must have a valid url.");
    }

    @Test
    void validateNullContentModeL() {
        recipient.setContentMode(null);
        testUnsuccessfulValidation("SinkRecipient contentMode must have a non null value.");
    }

    @Test
    void validateNullCeSpecVersion() {
        recipient.setCeSpecVersion(null);
        testUnsuccessfulValidation("SinkRecipient ce-specversion must have a non null value.");
    }

    @Test
    void validateNullOrEmptyCeType() {
        String error = "SinkRecipient ce-type must have a non empty value.";
        recipient.setCeType(null);
        testUnsuccessfulValidation(error);

        recipient.setCeType("");
        testUnsuccessfulValidation(error);

        recipient.setCeType("   ");
        testUnsuccessfulValidation(error);
    }

    @Test
    void validateNullCeSource() {
        recipient.setCeSource(null);
        testUnsuccessfulValidation("SinkRecipient ce-source must have a non null value.");
    }

    @Test
    void validateWrongExtensionName() {
        recipient.getCeExtensions().put("bad_name", "my_value");
        testUnsuccessfulValidation("Invalid attribute or extension name: 'bad_name'");
    }

    @Test
    void validateJobExecutionTimeoutOK() {
        Job job = Job.builder()
                .executionTimeout(3L)
                .executionTimeoutUnit(TemporalUnit.SECONDS)
                .build();
        validator.validate(recipient, new ValidatorContext(job));
    }

    @Test
    void validateJobExecutionTimeoutExceedsMaxTimeoutMillis() {
        Job job = Job.builder()
                .executionTimeout(MAX_TIMEOUT + 1)
                .build();
        testUnsuccessfulValidation("Job executionTimeout configuration can not exceed the SinkRecipient max-timeout-in-millis",
                new ValidatorContext(job));
    }

    @Test
    void validateJobExecutionTimeoutExceedsMaxTimeoutSeconds() {
        Job job = Job.builder()
                .executionTimeout(MAX_TIMEOUT)
                .executionTimeoutUnit(TemporalUnit.SECONDS)
                .build();
        testUnsuccessfulValidation("Job executionTimeout configuration can not exceed the SinkRecipient max-timeout-in-millis",
                new ValidatorContext(job));
    }

    private void testUnsuccessfulValidation(String expectedError) {
        testUnsuccessfulValidation(expectedError, new ValidatorContext());
    }

    private void testUnsuccessfulValidation(String expectedError, ValidatorContext context) {
        assertThatThrownBy(() -> validator.validate(recipient, context))
                .hasMessageStartingWith(expectedError);
    }
}
