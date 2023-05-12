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

import static io.cloudevents.core.v03.CloudEventV03.DATACONTENTENCODING;
import static io.cloudevents.core.v03.CloudEventV03.DATACONTENTTYPE;
import static io.cloudevents.core.v03.CloudEventV03.ID;
import static io.cloudevents.core.v03.CloudEventV03.SUBJECT;
import static io.cloudevents.core.v03.CloudEventV03.TIME;
import static io.cloudevents.core.v03.CloudEventV03.TYPE;

final class CloudEventValidatorV03 extends BaseCloudEventValidator {

    private static final CloudEventValidatorV03 instance = new CloudEventValidatorV03();

    private CloudEventValidatorV03() {
        super();
    }

    @Override
    protected String getRfc3339Attribute() {
        return TIME;
    }

    @Override
    protected String getRfc2046Attribute() {
        return DATACONTENTTYPE;
    }

    @Override
    protected List<String> getNonEmptyAttributes() {
        return List.of(ID, TYPE, DATACONTENTTYPE, DATACONTENTENCODING, SUBJECT);
    }

    static CloudEventValidatorV03 getInstance() {
        return instance;
    }
}
