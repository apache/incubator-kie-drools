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

import java.util.Arrays;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.PayloadData;
import org.kie.kogito.jobs.service.api.Recipient;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class RecipientValidatorProviderTest {

    @Test
    void getValidator() {
        RecipientValidator1 validator1 = Mockito.mock(RecipientValidator1.class);
        doReturn(true).when(validator1).accept(any(Recipient1.class));

        RecipientValidator2 validator2 = Mockito.mock(RecipientValidator2.class);
        doReturn(true).when(validator2).accept(any(Recipient2.class));

        Instance<RecipientValidator> validatorsInstance = mock(Instance.class);
        Stream<RecipientValidator> validators = Arrays.stream(new RecipientValidator[] { validator1, validator2 });
        doReturn(validators).when(validatorsInstance).stream();

        RecipientValidatorProvider provider = new RecipientValidatorProvider(validatorsInstance);

        Recipient1 recipient1 = new Recipient1();
        Recipient2 recipient2 = new Recipient2();
        assertThat(provider.getValidator(recipient1))
                .isNotEmpty()
                .hasValue(validator1);
        assertThat(provider.getValidator(recipient2))
                .isNotEmpty()
                .hasValue(validator2);
    }

    interface RecipientValidator1 extends RecipientValidator {
    };

    interface RecipientValidator2 extends RecipientValidator {
    }

    static class Recipient1 extends Recipient {
        @Override
        public PayloadData getPayload() {
            return null;
        }
    }

    static class Recipient2 extends Recipient {
        @Override
        public PayloadData getPayload() {
            return null;
        }
    }
}
