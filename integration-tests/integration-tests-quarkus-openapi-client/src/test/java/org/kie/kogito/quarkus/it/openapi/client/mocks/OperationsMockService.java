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
package org.kie.kogito.quarkus.it.openapi.client.mocks;

public class OperationsMockService extends MockServiceConfigurer {

    private static final MockServerConfig MULTIPLICATION =
            new MockServerConfig(8282,
                    "{\"multiplication\": { \"leftElement\": \"68.0\", \"rightElement\": \"0.5556\", \"product\": \"37.808\" }}",
                    "/",
                    "multiplicationService");
    private static final MockServerConfig SUBTRACTION =
            new MockServerConfig(8181,
                    "{\"subtraction\": { \"leftElement\": \"100\", \"rightElement\": \"32\", \"difference\": \"68.0\" }}",
                    "/",
                    "subtractionService");

    public OperationsMockService() {
        super(MULTIPLICATION, SUBTRACTION);
    }

}
