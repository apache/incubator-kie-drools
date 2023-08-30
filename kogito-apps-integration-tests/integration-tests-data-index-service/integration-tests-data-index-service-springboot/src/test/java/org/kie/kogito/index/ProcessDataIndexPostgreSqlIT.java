/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index;

import org.junit.jupiter.api.BeforeEach;
import org.kie.kogito.KogitoApplication;
import org.kie.kogito.index.spring.DataIndexPostgreSqlSpringTestResource;
import org.kie.kogito.index.spring.KogitoServiceRandomPortSpringTestResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import io.restassured.RestAssured;

import static org.kie.kogito.index.test.Constants.KOGITO_DATA_INDEX_SERVICE_URL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { KogitoApplication.class })
@ContextConfiguration(initializers = { KogitoServiceRandomPortSpringTestResource.class, DataIndexPostgreSqlSpringTestResource.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ProcessDataIndexPostgreSqlIT extends AbstractProcessDataIndexIT {

    @LocalServerPort
    private int httpPort;

    @Value("${" + KOGITO_DATA_INDEX_SERVICE_URL + "}")
    private String dataIndexUrl;

    @Override
    public String getDataIndexURL() {
        return dataIndexUrl;
    }

    @Override
    public boolean validateDomainData() {
        return false;
    }

    @BeforeEach
    public void setup() {
        RestAssured.port = httpPort;
    }

}
