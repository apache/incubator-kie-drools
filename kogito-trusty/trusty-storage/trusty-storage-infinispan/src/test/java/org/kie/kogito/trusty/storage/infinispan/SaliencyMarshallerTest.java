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

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.FeatureImportance;
import org.kie.kogito.trusty.storage.api.model.Saliency;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.ListTestField;

import static org.kie.kogito.trusty.storage.api.model.Saliency.FEATURE_IMPORTANCE_FIELD;

public class SaliencyMarshallerTest extends MarshallerTestTemplate<Saliency> {

    private static final List<AbstractTestField<Saliency, ?>> TEST_FIELD_LIST = List.of(
            new ListTestField<>(FEATURE_IMPORTANCE_FIELD, Collections.emptyList(), Saliency::getFeatureImportance, Saliency::setFeatureImportance, FeatureImportance.class)
    );

    public SaliencyMarshallerTest() {
        super(Saliency.class);
    }

    @Override
    protected Saliency buildEmptyObject() {
        return new Saliency();
    }

    @Override
    protected MessageMarshaller<Saliency> buildMarshaller() {
        return new SaliencyMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<Saliency, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
