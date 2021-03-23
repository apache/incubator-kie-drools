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

package org.kie.kogito.index.quarkus;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.index.resources.DataIndexInfinispanResource;
import org.kie.kogito.resources.ConditionalQuarkusTestResource;

public class DataIndexInfinispanQuarkusTestResource extends ConditionalQuarkusTestResource {

    public static final String KOGITO_DATA_INDEX_SERVICE_URL = "kogito.data-index-service.url";

    public DataIndexInfinispanQuarkusTestResource() {
        super(new DataIndexInfinispanResource());
    }

    @Override
    public Map<String, String> start() {
        Map<String, String> properties = super.start();
        if (properties.isEmpty()) {
            return properties;
        }

        properties = new HashMap<>(properties);
        properties.putAll(((DataIndexInfinispanResource) getTestResource()).getProperties());
        return properties;
    }

    @Override
    protected String getKogitoPropertyValue() {
        return "http://localhost:" + getTestResource().getMappedPort();
    }

    @Override
    protected String getKogitoProperty() {
        return KOGITO_DATA_INDEX_SERVICE_URL;
    }

}
