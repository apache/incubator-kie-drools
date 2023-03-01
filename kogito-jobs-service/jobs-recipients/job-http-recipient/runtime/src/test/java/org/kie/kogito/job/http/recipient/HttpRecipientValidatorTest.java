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

package org.kie.kogito.job.http.recipient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRecipientValidatorTest {

    public static final String URL = "http://my_url";

    private HttpRecipientValidator validator;

    private HttpRecipient<?> recipient;

    @BeforeEach
    public void setUp() {
        validator = new HttpRecipientValidator();
        recipient = new HttpRecipient<>();
        recipient.setUrl(URL);
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
        assertThatNoException().isThrownBy(() -> validator.validate(recipient));
    }

    @Test
    void validateNull() {
        recipient = null;
        testUnsuccessfulValidation("Recipient must be a non-null instance of");
    }

    @Test
    void validateNullURL() {
        recipient.setUrl(null);
        testUnsuccessfulValidation("HttpRecipient url must have a non empty value.");
    }

    @Test
    void validateMalformedURL() {
        recipient.setUrl("bad url");
        testUnsuccessfulValidation("HttpRecipient must have a valid url.");
    }

    private void testUnsuccessfulValidation(String expectedError) {
        assertThatThrownBy(() -> validator.validate(recipient))
                .hasMessageStartingWith(expectedError);
    }
}
