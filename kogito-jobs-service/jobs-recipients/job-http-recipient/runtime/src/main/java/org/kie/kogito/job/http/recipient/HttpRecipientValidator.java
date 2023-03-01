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

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.validator.RecipientValidator;
import org.kie.kogito.jobs.service.validator.ValidationException;

@ApplicationScoped
public class HttpRecipientValidator implements RecipientValidator {

    @Override
    public boolean accept(Recipient<?> recipient) {
        return recipient instanceof HttpRecipient;
    }

    @Override
    public boolean validate(Recipient<?> recipient) {
        if (!(recipient instanceof HttpRecipient)) {
            throw new IllegalArgumentException("Recipient must be a non-null instance of: " + HttpRecipient.class);
        }
        HttpRecipient<?> httpRecipient = (HttpRecipient<?>) recipient;
        if (StringUtils.isBlank(httpRecipient.getUrl())) {
            throw new IllegalArgumentException("HttpRecipient url must have a non empty value.");
        }
        try {
            new URL(httpRecipient.getUrl());
        } catch (MalformedURLException e) {
            throw new ValidationException("HttpRecipient must have a valid url.", e);
        }
        return true;
    }
}
