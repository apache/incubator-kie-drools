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
package org.kie.kogito.job.http.recipient;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.utils.ModelUtil;
import org.kie.kogito.jobs.service.validation.RecipientValidator;
import org.kie.kogito.jobs.service.validation.ValidationException;
import org.kie.kogito.jobs.service.validation.ValidatorContext;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HttpRecipientValidator implements RecipientValidator {

    private long maxTimeoutInMillis;

    public HttpRecipientValidator(@ConfigProperty(name = "kogito.job.recipient.http.max-timeout-in-millis") long maxTimeoutInMillis) {
        this.maxTimeoutInMillis = maxTimeoutInMillis;
    }

    @Override
    public boolean accept(Recipient<?> recipient) {
        return recipient instanceof HttpRecipient;
    }

    @Override
    public void validate(Recipient<?> recipient, ValidatorContext context) {
        if (!(recipient instanceof HttpRecipient)) {
            throw new ValidationException("Recipient must be a non-null instance of: " + HttpRecipient.class + ".");
        }
        HttpRecipient<?> httpRecipient = (HttpRecipient<?>) recipient;
        if (ConversionUtils.isEmpty(httpRecipient.getUrl())) {
            throw new ValidationException("HttpRecipient url must have a non empty value.");
        }
        try {
            new URL(httpRecipient.getUrl());
        } catch (MalformedURLException e) {
            throw new ValidationException("HttpRecipient must have a valid url.", e);
        }
        if (context.getJob() != null) {
            Long timeoutInMillis = ModelUtil.getExecutionTimeoutInMillis(context.getJob());
            if (timeoutInMillis != null && timeoutInMillis > maxTimeoutInMillis) {
                throw new ValidationException("Job executionTimeout configuration can not exceed the HttpRecipient max-timeout-in-millis: " + maxTimeoutInMillis +
                        ", but is: " + timeoutInMillis + ".");
            }
        }
    }
}
