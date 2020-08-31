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

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.FeatureImportance;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.DoubleTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import static org.kie.kogito.trusty.storage.api.model.FeatureImportance.FEATURE_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.FeatureImportance.SCORE_FIELD;

public class FeatureImportanceMarshallerTest extends MarshallerTestTemplate<FeatureImportance> {

    private static final List<AbstractTestField<FeatureImportance, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(FEATURE_ID_FIELD, "ID", FeatureImportance::getFeatureId, FeatureImportance::setFeatureId),
            new DoubleTestField<>(SCORE_FIELD, 0.2, FeatureImportance::getScore, FeatureImportance::setScore)
    );

    public FeatureImportanceMarshallerTest() {
        super(FeatureImportance.class);
    }

    @Override
    protected FeatureImportance buildEmptyObject() {
        return new FeatureImportance();
    }

    @Override
    protected MessageMarshaller<FeatureImportance> buildMarshaller() {
        return new FeatureImportanceMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<FeatureImportance, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
