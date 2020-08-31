/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.Saliency;

public class ExplainabilityResultItemMarshaller extends AbstractModelMarshaller<ExplainabilityResultItem> {

    public ExplainabilityResultItemMarshaller(ObjectMapper mapper) {
        super(mapper, ExplainabilityResultItem.class);
    }

    @Override
    public ExplainabilityResultItem readFrom(ProtoStreamReader reader) throws IOException {
        return new ExplainabilityResultItem(
                reader.readString(ExplainabilityResultItem.ID_FIELD),
                reader.readObject(ExplainabilityResultItem.SALIENCY_FIELD, Saliency.class)
        );
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, ExplainabilityResultItem input) throws IOException {
        writer.writeString(ExplainabilityResultItem.ID_FIELD, input.getId());
        writer.writeObject(ExplainabilityResultItem.SALIENCY_FIELD, input.getSaliency(), Saliency.class);
    }

    @Override
    public String getTypeName() {
        return String.format("%s.%s", ExplainabilityResult.class.getPackageName(), ExplainabilityResultItem.class.getSimpleName());
    }
}

/**
 * This is an utility class used for ExplainabilityResult proto serialization (see decision.proto). It is needed to support
 * the handling of a Map<String, Saliency>
 */
class ExplainabilityResultItem {

    public static final String ID_FIELD = "id";
    public static final String SALIENCY_FIELD = "saliency";

    @JsonProperty(ID_FIELD)
    private String id;

    @JsonProperty(SALIENCY_FIELD)
    private Saliency saliency;

    public ExplainabilityResultItem() {
    }

    public ExplainabilityResultItem(String id, Saliency saliency) {
        this.id = id;
        this.saliency = saliency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Saliency getSaliency() {
        return saliency;
    }

    public void setSaliency(Saliency saliency) {
        this.saliency = saliency;
    }
}
