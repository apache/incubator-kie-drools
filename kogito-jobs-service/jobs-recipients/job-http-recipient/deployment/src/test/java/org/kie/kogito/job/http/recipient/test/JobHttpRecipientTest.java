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
package org.kie.kogito.job.http.recipient.test;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kie.kogito.job.http.recipient.HttpJobExecutor;
import org.kie.kogito.job.recipient.common.http.HTTPRequest.HTTPMethod;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.RecipientInstance;

import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.common.QuarkusTestResource;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTestResource(HttpRecipientResourceMock.class)
class JobHttpRecipientTest {

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    HttpJobExecutor httpJobExecutor;

    @ConfigProperty(name = HttpRecipientResourceMock.MOCK_SERVICE_URL)
    String mockServiceUrl;

    @Test
    void httpExecutorTest() {
        testRequest(HTTPMethod.DELETE);
        testRequest(HTTPMethod.GET);
        testRequest(HTTPMethod.POST);
        testRequest(HTTPMethod.PUT);
        testRequest(HTTPMethod.PATCH);
    }

    private void testRequest(HTTPMethod method) {
        HttpRecipient<?> httpRecipient = HttpRecipient.builder()
                .forStringPayload()
                .method(method.name())
                .url(mockServiceUrl + "/" + HttpRecipientResourceMock.RESOURCE_URL)
                .build();
        JobDetails job = JobDetails.builder().id("12345").recipient(new RecipientInstance(httpRecipient)).build();
        UniAssertSubscriber<JobExecutionResponse> tester = httpJobExecutor.execute(job)
                .invoke(response -> assertThat(response.getJobId()).isEqualTo(job.getId()))
                .invoke(response -> assertThat(response.getCode()).isEqualTo("200"))
                .invoke(response -> assertThat(response.getMessage()).isEqualTo(method.name()))
                .subscribe().withSubscriber(UniAssertSubscriber.create());
        tester.awaitItem();
    }
}
