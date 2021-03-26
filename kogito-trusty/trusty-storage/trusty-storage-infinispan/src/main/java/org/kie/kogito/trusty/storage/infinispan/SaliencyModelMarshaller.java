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

import org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SaliencyModelMarshaller extends AbstractModelMarshaller<SaliencyModel> {

    public SaliencyModelMarshaller(ObjectMapper mapper) {
        super(mapper, SaliencyModel.class);
    }

    @Override
    public SaliencyModel readFrom(ProtoStreamReader reader) throws IOException {
        return new SaliencyModel(
                reader.readString(SaliencyModel.OUTCOME_ID_FIELD),
                reader.readString(SaliencyModel.OUTCOME_NAME_FIELD),
                reader.readCollection(SaliencyModel.FEATURE_IMPORTANCE_FIELD, new ArrayList<>(), FeatureImportanceModel.class));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, SaliencyModel input) throws IOException {
        writer.writeString(SaliencyModel.OUTCOME_ID_FIELD, input.getOutcomeId());
        writer.writeString(SaliencyModel.OUTCOME_NAME_FIELD, input.getOutcomeName());
        writer.writeCollection(SaliencyModel.FEATURE_IMPORTANCE_FIELD, input.getFeatureImportance(), FeatureImportanceModel.class);
    }
}
