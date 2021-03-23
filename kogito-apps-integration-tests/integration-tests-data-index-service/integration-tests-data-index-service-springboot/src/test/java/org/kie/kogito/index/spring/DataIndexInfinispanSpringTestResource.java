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

package org.kie.kogito.index.spring;

import java.util.List;

import org.kie.kogito.index.resources.DataIndexInfinispanResource;
import org.kie.kogito.resources.ConditionalSpringBootTestResource;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;

import static java.util.stream.Collectors.toList;

public class DataIndexInfinispanSpringTestResource extends ConditionalSpringBootTestResource {

    public static final String KOGITO_DATA_INDEX_SERVICE_URL = "kogito.data-index-service.url";

    public DataIndexInfinispanSpringTestResource() {
        super(new DataIndexInfinispanResource());
    }

    @Override
    protected void updateContextProperty(ConfigurableApplicationContext ctx, String key, String value) {
        List<String> props = ((DataIndexInfinispanResource) getTestResource()).getProperties().entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue()).collect(toList());
        props.add(key + "=" + value);
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(ctx, props.toArray(new String[props.size()]));
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
