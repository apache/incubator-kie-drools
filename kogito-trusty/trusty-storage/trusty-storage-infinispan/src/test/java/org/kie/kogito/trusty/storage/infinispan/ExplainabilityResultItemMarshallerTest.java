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
import org.kie.kogito.trusty.storage.api.model.Saliency;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.ObjectTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import static org.kie.kogito.trusty.storage.infinispan.ExplainabilityResultItem.ID_FIELD;
import static org.kie.kogito.trusty.storage.infinispan.ExplainabilityResultItem.SALIENCY_FIELD;

public class ExplainabilityResultItemMarshallerTest extends MarshallerTestTemplate<ExplainabilityResultItem> {

    private static final List<AbstractTestField<ExplainabilityResultItem, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(ID_FIELD, "ID", ExplainabilityResultItem::getId, ExplainabilityResultItem::setId),
            new ObjectTestField<>(SALIENCY_FIELD, null, ExplainabilityResultItem::getSaliency, ExplainabilityResultItem::setSaliency, Saliency.class)
    );

    public ExplainabilityResultItemMarshallerTest() {
        super(ExplainabilityResultItem.class);
    }

    @Override
    protected ExplainabilityResultItem buildEmptyObject() {
        return new ExplainabilityResultItem();
    }

    @Override
    protected MessageMarshaller<ExplainabilityResultItem> buildMarshaller() {
        return new ExplainabilityResultItemMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<ExplainabilityResultItem, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
