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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is an abstract implementation of the {@link DataEvent} that contains basic common attributes referring to
 * kogito processes metadata. This class can be extended mainly by Services that need to publish events to be
 * indexed by the Data-Index service.
 *
 * @param <T> the payload
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractDataEvent<T> implements DataEvent<T> {

    /**
     * String prefix for Kogito CloudEvents type fields.
     * Since this is a required field, the constructor will fill them with this default value.
     * Ideally, callers would use #TYPE_FORMAT to fill this field using the process name and the signal node name, e.g: process.travelagency.visaapproved
     */
    public static final String TYPE_PREFIX = "process";
    public static final String TYPE_FORMAT = TYPE_PREFIX + ".%s.%s";
    /**
     * String format for Kogito CloudEvents source fields.
     * Since this is a required field, the constructor will fill them with default value, e.g.: /process/travelAgency/0982-1223-3121-1212
     */
    public static final String SOURCE_FORMAT = "/process/%s/%s";
    private static final String SPEC_VERSION = "1.0";
    @JsonProperty("specversion")
    private String specVersion;
    private String id;
    private String source;
    private String type;
    private String time;
    private String subject;
    @JsonProperty("datacontenttype")
    private String dataContentType;
    @JsonProperty("dataschema")
    private String dataSchema;

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
        this.specVersion = SPEC_VERSION;
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

        this.ensureRequiredFields();
    }

    public AbstractDataEvent(String type,
                             String source,
                             T body,
                             String kogitoProcessinstanceId,
                             String kogitoRootProcessinstanceId,
                             String kogitoProcessId,
                             String kogitoRootProcessId,
                             String kogitoAddons,
                             String subject,
                             String dataContentType,
                             String dataSchema) {
        this(type, source, body, kogitoProcessinstanceId, kogitoRootProcessinstanceId, kogitoProcessId, kogitoRootProcessId, kogitoAddons);
        this.subject = subject;
        this.dataContentType = dataContentType;
        this.dataSchema = dataSchema;
    }

    protected void ensureRequiredFields() {
        if (this.type == null || this.type.isEmpty()) {
            this.type = TYPE_PREFIX;
        }
        if (this.source == null || this.source.isEmpty()) {
            this.source = String.format(SOURCE_FORMAT, kogitoProcessId, kogitoProcessinstanceId);
        }
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getSpecVersion() {
        return specVersion;
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

    @Override
    public String getDataContentType() {
        return dataContentType;
    }

    @Override
    public String getDataSchema() {
        return dataSchema;
    }

    @Override
    public String getSubject() {
        return subject;
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
