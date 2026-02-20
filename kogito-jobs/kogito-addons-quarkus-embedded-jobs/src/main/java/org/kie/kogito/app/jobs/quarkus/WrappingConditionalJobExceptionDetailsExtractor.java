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
package org.kie.kogito.app.jobs.quarkus;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.app.jobs.integrations.DefaultJobExceptionDetailsExtractor;
import org.kie.kogito.app.jobs.integrations.JobExceptionDetailsExtractor;
import org.kie.kogito.jobs.service.model.JobExecutionExceptionDetails;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * CDI producer for JobExceptionDetailsExtractor.
 * This is the ONLY source of JobExceptionDetailsExtractor beans in the application.
 *
 * The producer enforces the kogito.jobs-service.exception-details-enabled property:
 * - When disabled (false, default): Returns null (no exception details extracted)
 * - When enabled (true): Returns custom implementation if available, otherwise DefaultJobExceptionDetailsExtractor
 *
 * This ensures sensitive exception details are never exposed unless explicitly enabled.
 *
 * To provide a custom implementation, create an ApplicationScoped bean:
 *
 * <pre>
 * {
 *     &#64;code
 *     &#64;ApplicationScoped
 *     public class MyCustomExtractor implements JobExceptionDetailsExtractor {
 *         // implementation
 *     }
 * }
 * </pre>
 *
 * The producer will automatically detect and use it when the feature is enabled.
 */
@ApplicationScoped
public class WrappingConditionalJobExceptionDetailsExtractor implements JobExceptionDetailsExtractor {

    @Inject
    @ConfigProperty(name = "kogito.jobs-service.exception-details-enabled", defaultValue = "false")
    Boolean exceptionDetailsEnabled;

    @Inject
    @Any // Injects all other implementations
    Instance<JobExceptionDetailsExtractor> allExtractors;

    private JobExceptionDetailsExtractor delegate;

    @PostConstruct
    void init() {
        // Resolve the delegate once during initialization
        if (exceptionDetailsEnabled) {
            delegate = allExtractors.stream()
                    .filter(impl -> !(impl instanceof WrappingConditionalJobExceptionDetailsExtractor))
                    .findFirst()
                    .orElse(new DefaultJobExceptionDetailsExtractor());
        }
    }

    @Override
    public JobExecutionExceptionDetails extractExceptionDetails(Exception e) {
        // If disabled or no delegate, return null
        if (delegate == null) {
            return null;
        }

        // Use the pre-resolved delegate
        return delegate.extractExceptionDetails(e);
    }
}
