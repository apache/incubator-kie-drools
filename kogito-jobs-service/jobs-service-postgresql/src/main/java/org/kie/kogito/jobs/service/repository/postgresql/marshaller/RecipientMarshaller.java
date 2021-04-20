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

package org.kie.kogito.jobs.service.repository.postgresql.marshaller;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.jobs.service.model.job.Recipient;
import org.kie.kogito.jobs.service.model.job.Recipient.HTTPRecipient;

import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class RecipientMarshaller {

    private static final String CLASS_TYPE = "classType";

    public JsonObject marshall(Recipient recipient) {
        if (recipient instanceof HTTPRecipient) {
            return JsonObject.mapFrom(new HTTPRecipientAccessor((HTTPRecipient) recipient))
                    .put(CLASS_TYPE, recipient.getClass().getName());
        }
        return null;
    }

    public Recipient unmarshall(JsonObject jsonObject) {
        String classType = Optional.ofNullable(jsonObject).map(o -> (String) o.remove(CLASS_TYPE)).orElse(null);
        if (HTTPRecipient.class.getName().equals(classType)) {
            return jsonObject.mapTo(HTTPRecipientAccessor.class).to();
        }
        return null;
    }

    static class HTTPRecipientAccessor {

        private String endpoint;

        public HTTPRecipientAccessor() {
        }

        public HTTPRecipientAccessor(HTTPRecipient recipient) {
            this.endpoint = recipient.getEndpoint();
        }

        public HTTPRecipient to() {
            return new HTTPRecipient(this.endpoint);
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
    }
}
