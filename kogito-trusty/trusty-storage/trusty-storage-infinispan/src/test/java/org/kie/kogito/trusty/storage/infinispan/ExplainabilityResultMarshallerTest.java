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
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.MapToListTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import static org.kie.kogito.trusty.storage.api.model.ExplainabilityResult.EXECUTION_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.ExplainabilityResult.SALIENCIES_FIELD;

public class ExplainabilityResultMarshallerTest extends MarshallerTestTemplate<ExplainabilityResult> {

    private static final List<AbstractTestField<ExplainabilityResult, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(EXECUTION_ID_FIELD, "ID", ExplainabilityResult::getExecutionId, ExplainabilityResult::setExecutionId),
            new MapToListTestField<>(
                    SALIENCIES_FIELD, Collections.emptyMap(),
                    ExplainabilityResult::getSaliencies, ExplainabilityResult::setSaliencies,
                    ExplainabilityResultItem.class,
                    ExplainabilityResultMarshaller::mapToList, ExplainabilityResultMarshaller::listToMap
            )
    );

    public ExplainabilityResultMarshallerTest() {
        super(ExplainabilityResult.class);
    }

    @Override
    protected ExplainabilityResult buildEmptyObject() {
        return new ExplainabilityResult();
    }

    @Override
    protected MessageMarshaller<ExplainabilityResult> buildMarshaller() {
        return new ExplainabilityResultMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<ExplainabilityResult, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
