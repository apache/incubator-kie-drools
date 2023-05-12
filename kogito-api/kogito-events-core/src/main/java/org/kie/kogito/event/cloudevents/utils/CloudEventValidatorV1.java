/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.cloudevents.utils;

import java.util.List;

import static io.cloudevents.core.v1.CloudEventV1.DATACONTENTTYPE;
import static io.cloudevents.core.v1.CloudEventV1.DATASCHEMA;
import static io.cloudevents.core.v1.CloudEventV1.ID;
import static io.cloudevents.core.v1.CloudEventV1.SOURCE;
import static io.cloudevents.core.v1.CloudEventV1.SUBJECT;
import static io.cloudevents.core.v1.CloudEventV1.TIME;
import static io.cloudevents.core.v1.CloudEventV1.TYPE;

final class CloudEventValidatorV1 extends BaseCloudEventValidator {

    private static final CloudEventValidatorV1 instance = new CloudEventValidatorV1();

    private CloudEventValidatorV1() {
        super();
    }

    @Override
    protected List<String> getNonEmptyAttributes() {
        return List.of(ID, SOURCE, TYPE, DATACONTENTTYPE, DATASCHEMA, SUBJECT);
    }

    @Override
    protected String getRfc3339Attribute() {
        return TIME;
    }

    @Override
    protected String getRfc2046Attribute() {
        return DATACONTENTTYPE;
    }

    static CloudEventValidatorV1 getInstance() {
        return instance;
    }
}
