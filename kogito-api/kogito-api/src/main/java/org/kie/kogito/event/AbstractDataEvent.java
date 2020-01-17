/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.event;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * This is an abstract implementation of the {@link DataEvent} that contains basic common attributes referring to
 * kogito processes metadata. This class can be extended mainly by Services that need to publish events to be
 * indexed by the Data-Index service.
 *
 * @param <T> the payload
 */
public abstract class AbstractDataEvent<T> implements DataEvent<T> {

    private static final String SPEC_VERSION = "0.3";

    private String specversion;
    private String id;
    private String source;
    private String type;
    private String time;
    private T data;
    private String kogitoProcessinstanceId;
    private String kogitoRootProcessinstanceId;
    private String kogitoProcessId;
    private String kogitoRootProcessId;
    private String kogitoAddons;

    public AbstractDataEvent(String type,
                             String source,
                             T body,
                             String kogitoProcessinstanceId,
                             String kogitoRootProcessinstanceId,
                             String kogitoProcessId,
                             String kogitoRootProcessId,
                             String kogitoAddons) {
        this.specversion = SPEC_VERSION;
        this.id = UUID.randomUUID().toString();
        this.source = source;
        this.type = type;
        this.time = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.data = body;

        this.kogitoProcessinstanceId = kogitoProcessinstanceId;
        this.kogitoRootProcessinstanceId = kogitoRootProcessinstanceId;
        this.kogitoProcessId = kogitoProcessId;
        this.kogitoRootProcessId = kogitoRootProcessId;
        this.kogitoAddons = kogitoAddons;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getSpecversion() {
        return specversion;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public T getData() {
        return data;
    }

    public String getKogitoProcessinstanceId() {
        return kogitoProcessinstanceId;
    }

    public String getKogitoRootProcessinstanceId() {
        return kogitoRootProcessinstanceId;
    }

    public String getKogitoProcessId() {
        return kogitoProcessId;
    }

    public String getKogitoRootProcessId() {
        return kogitoRootProcessId;
    }

    public String getKogitoAddons() {
        return kogitoAddons;
    }
}
