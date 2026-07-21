/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.jobs.service.api.recipient.sink;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.api.recipient.sink.serialization.ContentModeDeserializer;
import org.kie.kogito.jobs.service.api.recipient.sink.serialization.ContentModeSerializer;
import org.kie.kogito.jobs.service.api.serialization.SpecVersionDeserializer;
import org.kie.kogito.jobs.service.api.serialization.SpecVersionSerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.cloudevents.SpecVersion;

import static org.kie.kogito.jobs.service.api.Recipient.PAYLOAD_PROPERTY;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient.CE_DATACONTENTTYPE;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient.CE_DATASCHEMA;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient.CE_EXTENSIONS;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient.CE_SOURCE;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient.CE_SPECVERSION;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient.CE_SUBJECT;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient.CE_TYPE;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient.CONTENT_MODE_PROPERTY;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient.SINK_URL_PROPERTY;
import static org.kie.kogito.jobs.service.api.utils.EventUtils.validateExtensionName;

@Schema(description = "Recipient definition that delivers a cloud event to a knative sink.",
        allOf = { Recipient.class },
        requiredProperties = { SINK_URL_PROPERTY, CONTENT_MODE_PROPERTY, CE_SPECVERSION, CE_SOURCE, CE_TYPE })
@JsonPropertyOrder({ SINK_URL_PROPERTY, CONTENT_MODE_PROPERTY,
        CE_SPECVERSION, CE_SOURCE, CE_TYPE, CE_SUBJECT, CE_DATACONTENTTYPE, CE_DATASCHEMA, CE_EXTENSIONS,
        PAYLOAD_PROPERTY })
public class SinkRecipient<T extends SinkRecipientPayloadData<?>> extends Recipient<T> {

    static final String SINK_URL_PROPERTY = "sinkUrl";
    static final String CONTENT_MODE_PROPERTY = "contentMode";

    static final String CE_SPECVERSION = "ce-specversion";
    static final String CE_SOURCE = "ce-source";
    static final String CE_TYPE = "ce-type";
    static final String CE_SUBJECT = "ce-subject";
    static final String CE_DATACONTENTTYPE = "ce-datacontenttype";
    static final String CE_DATASCHEMA = "ce-dataschema";
    static final String CE_EXTENSIONS = "ce-extensions";

    public static final SpecVersion SPEC_VERSION = SpecVersion.V1;

    @Schema(enumeration = { "binary", "structured" })
    public enum ContentMode {
        BINARY("binary"),
        STRUCTURED("structured");

        private final String stringValue;

        ContentMode(String stringValue) {
            this.stringValue = stringValue;
        }

        @Override
        public String toString() {
            return this.stringValue;
        }

        public static ContentMode parse(String stringValue) {
            switch (stringValue) {
                case "binary":
                    return BINARY;
                case "structured":
                    return STRUCTURED;
                default:
                    throw new IllegalArgumentException("Invalid content mode: " + stringValue);
            }
        }
    }

    @Schema(description = "Url of the knative sink that will receive the cloud event.")
    private String sinkUrl;

    @JsonDeserialize(using = ContentModeDeserializer.class)
    @JsonSerialize(using = ContentModeSerializer.class)
    private ContentMode contentMode = ContentMode.BINARY;

    @JsonDeserialize(using = SpecVersionDeserializer.class)
    @JsonSerialize(using = SpecVersionSerializer.class)
    @JsonProperty(CE_SPECVERSION)
    private SpecVersion ceSpecVersion = SPEC_VERSION;

    @JsonProperty(CE_SOURCE)
    private URI ceSource;

    @JsonProperty(CE_TYPE)
    private String ceType;

    @JsonProperty(CE_SUBJECT)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String ceSubject;

    @JsonProperty(CE_DATACONTENTTYPE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String ceDataContentType;

    @JsonProperty(CE_DATASCHEMA)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private URI ceDataSchema;

    @JsonProperty(CE_EXTENSIONS)
    private Map<String, Object> ceExtensions;

    @JsonProperty("payload")
    @Schema(ref = "#/components/schemas/SinkRecipientPayloadData")
    private T payload;

    public SinkRecipient() {
        // Marshalling constructor.
        this.ceExtensions = new HashMap<>();
    }

    public String getSinkUrl() {
        return sinkUrl;
    }

    public void setSinkUrl(String sinkUrl) {
        this.sinkUrl = sinkUrl;
    }

    public ContentMode getContentMode() {
        return contentMode;
    }

    public void setContentMode(ContentMode contentMode) {
        this.contentMode = contentMode;
    }

    public SpecVersion getCeSpecVersion() {
        return ceSpecVersion;
    }

    public void setCeSpecVersion(SpecVersion ceSpecVersion) {
        this.ceSpecVersion = ceSpecVersion;
    }

    public URI getCeSource() {
        return ceSource;
    }

    public void setCeSource(URI ceSource) {
        this.ceSource = ceSource;
    }

    public String getCeType() {
        return ceType;
    }

    public void setCeType(String ceType) {
        this.ceType = ceType;
    }

    public String getCeSubject() {
        return ceSubject;
    }

    public void setCeSubject(String ceSubject) {
        this.ceSubject = ceSubject;
    }

    public String getCeDataContentType() {
        return ceDataContentType;
    }

    public void setCeDataContentType(String ceDataContentType) {
        this.ceDataContentType = ceDataContentType;
    }

    public URI getCeDataSchema() {
        return ceDataSchema;
    }

    public void setCeDataSchema(URI ceDataSchema) {
        this.ceDataSchema = ceDataSchema;
    }

    public Map<String, Object> getCeExtensions() {
        return ceExtensions;
    }

    public void setCeExtensions(Map<String, Object> ceExtensions) {
        this.ceExtensions = ceExtensions != null ? ceExtensions : new HashMap<>();
    }

    public SinkRecipient<T> addCeExtension(String name, Object value) {
        validateExtensionName(name);
        ceExtensions.put(name, value);
        return this;
    }

    @Override
    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "SinkRecipient{" +
                "sinkUrl='" + sinkUrl + '\'' +
                ", contentMode=" + contentMode +
                ", ceSpecVersion=" + ceSpecVersion +
                ", ceSource=" + ceSource +
                ", ceType='" + ceType + '\'' +
                ", ceSubject='" + ceSubject + '\'' +
                ", ceDataContentType='" + ceDataContentType + '\'' +
                ", ceDataSchema=" + ceDataSchema +
                ", ceExtensions=" + ceExtensions +
                ", payload=" + payload +
                "} " + super.toString();
    }

    public static BuilderSelector builder() {
        return new BuilderSelector();
    }

    public static class BuilderSelector {

        private BuilderSelector() {

        }

        public SinkRecipient.Builder<SinkRecipientBinaryPayloadData> forBinaryPayload() {
            return new SinkRecipient.Builder<>(new SinkRecipient<>());
        }

        public SinkRecipient.Builder<SinkRecipientJsonPayloadData> forJsonPayload() {
            return new SinkRecipient.Builder<>(new SinkRecipient<>());
        }
    }

    public static class Builder<P extends SinkRecipientPayloadData<?>> {
        private final SinkRecipient<P> recipient;

        private Builder(SinkRecipient<P> recipient) {
            this.recipient = recipient;
        }

        public Builder<P> payload(P payload) {
            recipient.setPayload(payload);
            return this;
        }

        public Builder<P> sinkUrl(String sinkUrl) {
            recipient.setSinkUrl(sinkUrl);
            return this;
        }

        public Builder<P> contentMode(ContentMode contentMode) {
            recipient.setContentMode(contentMode);
            return this;
        }

        public Builder<P> ceSpecVersion(SpecVersion ceSpecVersion) {
            recipient.setCeSpecVersion(ceSpecVersion);
            return this;
        }

        public Builder<P> ceSource(URI ceSource) {
            recipient.setCeSource(ceSource);
            return this;
        }

        public Builder<P> ceEventType(String ceEventType) {
            recipient.setCeType(ceEventType);
            return this;
        }

        public Builder<P> ceSubject(String ceSubject) {
            recipient.setCeSubject(ceSubject);
            return this;
        }

        public Builder<P> ceDataContentType(String ceDataContentType) {
            recipient.setCeDataContentType(ceDataContentType);
            return this;
        }

        public Builder<P> ceDataSchema(URI ceDataSchema) {
            recipient.setCeDataSchema(ceDataSchema);
            return this;
        }

        public Builder<P> ceExtension(String name, Object value) {
            recipient.addCeExtension(name, value);
            return this;
        }

        public SinkRecipient<P> build() {
            return recipient;
        }
    }
}
