/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.jobs.service.exception.JobValidationException;
import org.kie.kogito.jobs.service.model.Recipient;

@ApplicationScoped
public class RecipientInstanceValidator {

    private final RecipientValidatorProvider recipientValidatorProvider;

    @Inject
    public RecipientInstanceValidator(RecipientValidatorProvider recipientValidatorProvider) {
        this.recipientValidatorProvider = recipientValidatorProvider;
    }

    public void validate(Recipient recipient) {
        if (recipient == null) {
            throw new JobValidationException(Recipient.class.getName() + " instance can not be null.");
        }
        if (recipient.getRecipient() == null) {
            throw new JobValidationException(org.kie.kogito.jobs.service.api.Recipient.class + " instance can not be null.");
        }
        recipientValidatorProvider.getValidator(recipient.getRecipient())
                .ifPresent(validator -> validator.validate(recipient.getRecipient(), new ValidatorContext()));
    }
}
