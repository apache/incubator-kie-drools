/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.it.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.kie.kogito.KogitoApplication;
import org.kie.kogito.test.resources.JobServiceSpringBootTestResource;
import org.kie.kogito.test.resources.KogitoServiceRandomPortSpringBootTestResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import io.restassured.RestAssured;

import static org.kie.kogito.test.resources.JobServiceSpringBootTestResource.JOBS_SERVICE_URL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = { KogitoApplication.class })
@ContextConfiguration(initializers = { KogitoServiceRandomPortSpringBootTestResource.class, JobServiceSpringBootTestResource.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ProcessTimerIT extends BaseProcessTimerIT {

    @Value("${server.port}")
    private int httpPort;

    @Value("${" + JOBS_SERVICE_URL + "}")
    private String jobServiceUrl;

    @BeforeEach
    public void setup() {
        RestAssured.port = httpPort;
        healthCheck();
    }

    @Override
    public String jobServiceUrl() {
        return jobServiceUrl;
    }
}
