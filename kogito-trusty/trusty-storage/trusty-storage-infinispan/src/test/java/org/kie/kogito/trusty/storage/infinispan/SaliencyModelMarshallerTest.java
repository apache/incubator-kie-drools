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

import java.util.Collections;
import java.util.List;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.ListTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.trusty.storage.api.model.SaliencyModel.FEATURE_IMPORTANCE_FIELD;
import static org.kie.kogito.trusty.storage.api.model.SaliencyModel.OUTCOME_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.SaliencyModel.OUTCOME_NAME_FIELD;

public class SaliencyModelMarshallerTest extends MarshallerTestTemplate<SaliencyModel> {

    private static final List<AbstractTestField<SaliencyModel, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(OUTCOME_ID_FIELD, "ID", SaliencyModel::getOutcomeId, SaliencyModel::setOutcomeId),
            new StringTestField<>(OUTCOME_NAME_FIELD, "test", SaliencyModel::getOutcomeName, SaliencyModel::setOutcomeName),
            new ListTestField<>(FEATURE_IMPORTANCE_FIELD, Collections.emptyList(), SaliencyModel::getFeatureImportance, SaliencyModel::setFeatureImportance, FeatureImportanceModel.class));

    public SaliencyModelMarshallerTest() {
        super(SaliencyModel.class);
    }

    @Override
    protected SaliencyModel buildEmptyObject() {
        return new SaliencyModel();
    }

    @Override
    protected MessageMarshaller<SaliencyModel> buildMarshaller() {
        return new SaliencyModelMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<SaliencyModel, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
