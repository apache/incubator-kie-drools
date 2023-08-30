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

package org.kie.kogito.index.test.containers;

/**
 * This container wraps Data Index Service container
 */
public class DataIndexMongoDBContainer extends AbstractDataIndexContainer {

    public static final String NAME = "data-index-service-mongodb";

    public DataIndexMongoDBContainer() {
        super(NAME);
    }

    public void setMongoDBURL(String mongoURL) {
        addEnv("QUARKUS_MONGODB_CONNECTION_STRING", mongoURL);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }

}
