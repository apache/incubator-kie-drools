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

package org.kie.kogito.jobs.service.api.recipient.sink;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.kie.kogito.jobs.service.api.Recipient;

import io.cloudevents.CloudEvent;

@Schema(description = "Recipient definition that delivers a cloud event to a knative sink.", allOf = { Recipient.class })
public class SinkRecipient extends Recipient<CloudEvent> {

    public enum ContentMode {
        BINARY,
        STRUCTURED
    }

    @Schema(description = "Url of the knative sink that will receive the cloud event.", required = true)
    private String sinkUrl;
    @Schema(description = "Content mode for the event transfer to knative sink.", required = true, defaultValue = "BINARY")
    private ContentMode contentMode = ContentMode.BINARY;

    public SinkRecipient() {
        // marshalling constructor.
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

    @Override
    public String toString() {
        return "SinkRecipient{" +
                "sinkUrl='" + sinkUrl + '\'' +
                ", contentMode='" + contentMode + '\'' +
                "} " + super.toString();
    }

    public static Builder builder() {
        return new Builder(new SinkRecipient());
    }

    public static class Builder {

        private final SinkRecipient recipient;

        private Builder(SinkRecipient recipient) {
            this.recipient = recipient;
        }

        public Builder payload(CloudEvent payload) {
            recipient.setPayload(payload);
            return this;
        }

        public Builder sinkUrl(String sinkUrl) {
            recipient.setSinkUrl(sinkUrl);
            return this;
        }

        public Builder contentMode(ContentMode contentMode) {
            recipient.setContentMode(contentMode);
            return this;
        }

        public SinkRecipient build() {
            return recipient;
        }
    }
}
