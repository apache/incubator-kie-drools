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

import java.util.List;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.DoubleTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel.FEATURE_NAME_FIELD;
import static org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel.SCORE_FIELD;

public class FeatureImportanceModelMarshallerTest extends MarshallerTestTemplate<FeatureImportanceModel> {

    private static final List<AbstractTestField<FeatureImportanceModel, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(FEATURE_NAME_FIELD, "test", FeatureImportanceModel::getFeatureName, FeatureImportanceModel::setFeatureName),
            new DoubleTestField<>(SCORE_FIELD, 0.2, FeatureImportanceModel::getScore, FeatureImportanceModel::setScore));

    public FeatureImportanceModelMarshallerTest() {
        super(FeatureImportanceModel.class);
    }

    @Override
    protected FeatureImportanceModel buildEmptyObject() {
        return new FeatureImportanceModel();
    }

    @Override
    protected MessageMarshaller<FeatureImportanceModel> buildMarshaller() {
        return new FeatureImportanceModelMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<FeatureImportanceModel, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
