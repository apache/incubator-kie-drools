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

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.model.job.Recipient;

import io.vertx.core.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RecipientMarshallerTest {

    RecipientMarshaller marshaller = new RecipientMarshaller();

    @Test
    void marshall() {
        Recipient recipient = new Recipient.HTTPRecipient("test");
        JsonObject jsonObject = marshaller.marshall(recipient);
        assertEquals(new JsonObject()
                .put("endpoint", "test")
                .put("classType", Recipient.HTTPRecipient.class.getName()),
                jsonObject);
    }

    @Test
    void marshallNull() {
        JsonObject jsonObject = marshaller.marshall(null);
        assertNull(jsonObject);
    }

    @Test
    void unmarshall() {
        JsonObject jsonObject = new JsonObject()
                .put("endpoint", "test")
                .put("classType", Recipient.HTTPRecipient.class.getName());
        Recipient recipient = marshaller.unmarshall(jsonObject);
        assertEquals(new Recipient.HTTPRecipient("test"), recipient);
    }

    @Test
    void unmarshallInvalid() {
        JsonObject jsonObject = new JsonObject().put("endpoint", "test");
        Recipient recipient = marshaller.unmarshall(jsonObject);
        assertNull(recipient);
    }

    @Test
    void unmarshallNull() {
        Recipient recipient = marshaller.unmarshall(null);
        assertNull(recipient);
    }
}
