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
package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;
import java.util.ArrayList;

import org.kie.kogito.trusty.storage.api.model.FeatureImportance;
import org.kie.kogito.trusty.storage.api.model.Saliency;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SaliencyMarshaller extends AbstractModelMarshaller<Saliency> {

    public SaliencyMarshaller(ObjectMapper mapper) {
        super(mapper, Saliency.class);
    }

    @Override
    public Saliency readFrom(ProtoStreamReader reader) throws IOException {
        return new Saliency(
                reader.readString(Saliency.OUTCOME_ID_FIELD),
                reader.readString(Saliency.OUTCOME_NAME_FIELD),
                reader.readCollection(Saliency.FEATURE_IMPORTANCE_FIELD, new ArrayList<>(), FeatureImportance.class));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Saliency input) throws IOException {
        writer.writeString(Saliency.OUTCOME_ID_FIELD, input.getOutcomeId());
        writer.writeString(Saliency.OUTCOME_NAME_FIELD, input.getOutcomeName());
        writer.writeCollection(Saliency.FEATURE_IMPORTANCE_FIELD, input.getFeatureImportance(), FeatureImportance.class);
    }
}
