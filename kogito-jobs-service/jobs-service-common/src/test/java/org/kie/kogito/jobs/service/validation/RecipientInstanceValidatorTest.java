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

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.service.model.Recipient;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecipientInstanceValidatorTest {

    @Mock
    private RecipientValidatorProvider recipientValidatorProvider;

    @Mock
    private RecipientValidatorProviderTest.RecipientValidator1 validator1;

    @Mock
    private RecipientValidatorProviderTest.Recipient1 recipient1;

    @Mock
    private Recipient recipient;

    private RecipientInstanceValidator recipientInstanceValidator;

    @BeforeEach
    void setUp() {
        recipientInstanceValidator = new RecipientInstanceValidator(recipientValidatorProvider);
        lenient().doReturn(Optional.of(validator1)).when(recipientValidatorProvider).getValidator(recipient1);
    }

    @Test
    void validateNull() {
        validateWithError(null, Recipient.class.getName());
    }

    @Test
    void validateNullRecipient() {
        doReturn(null).when(recipient).getRecipient();
        validateWithError(recipient, org.kie.kogito.jobs.service.api.Recipient.class + " instance can not be null");
    }

    @Test
    void validateSuccessful() {
        doReturn(recipient1).when(recipient).getRecipient();
        recipientInstanceValidator.validate(recipient);
        verify(validator1).validate(eq(recipient1), any());
    }

    @Test
    void validateUnSuccessful() {
        String error = "Validation has failed!";
        doThrow(new RuntimeException(error)).when(validator1).validate(eq(recipient1), any());
        doReturn(recipient1).when(recipient).getRecipient();
        validateWithError(recipient, error);
        verify(validator1).validate(eq(recipient1), any());
    }

    private void validateWithError(Recipient recipient, String expectedMessage) {
        assertThatThrownBy(() -> recipientInstanceValidator.validate(recipient)).hasMessageStartingWith(expectedMessage);
    }
}
