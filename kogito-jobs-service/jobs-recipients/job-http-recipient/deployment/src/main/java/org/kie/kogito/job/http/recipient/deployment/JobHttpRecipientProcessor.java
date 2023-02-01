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
package org.kie.kogito.job.http.recipient.deployment;

import org.kie.kogito.job.http.recipient.HttpJobExecutor;
import org.kie.kogito.job.http.recipient.HttpRecipientValidator;
import org.kie.kogito.job.http.recipient.JobHttpRecipientConfiguration;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class JobHttpRecipientProcessor {

    private static final String FEATURE = "job-http-recipient";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem additionalBeans() {
        return new AdditionalBeanBuildItem(HttpJobExecutor.class, HttpRecipientValidator.class);
    }

    /**
     * Http Recipient configuration
     */
    JobHttpRecipientConfiguration configuration;
}
