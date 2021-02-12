/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.repository.infinispan.marshaller;

import java.io.IOException;

import org.kie.kogito.jobs.service.model.job.Recipient;
import org.kie.kogito.jobs.service.model.job.Recipient.HTTPRecipient;

public class RecipientMarshaller extends BaseMarshaller<Recipient> {

    @Override
    public String getTypeName() {
        return getPackage() + ".Recipient";
    }

    @Override
    public Class<? extends Recipient> getJavaClass() {
        return Recipient.class;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Recipient recipient) throws IOException {
        HTTPRecipient httpRecipient = (HTTPRecipient) recipient;
        writer.writeString("endpoint", httpRecipient.getEndpoint());
    }

    @Override
    public Recipient readFrom(ProtoStreamReader reader) throws IOException {
        String endpoint = reader.readString("endpoint");
        return new HTTPRecipient(endpoint);
    }
}