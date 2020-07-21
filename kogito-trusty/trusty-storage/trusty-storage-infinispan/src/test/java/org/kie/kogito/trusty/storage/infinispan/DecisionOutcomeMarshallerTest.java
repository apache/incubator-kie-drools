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
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.Message;
import org.kie.kogito.trusty.storage.api.model.TypedValue;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.CollectionTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.ObjectTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import static org.kie.kogito.trusty.storage.api.model.DecisionOutcome.EVALUATION_STATUS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DecisionOutcome.MESSAGES_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DecisionOutcome.OUTCOME_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DecisionOutcome.OUTCOME_INPUTS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DecisionOutcome.OUTCOME_NAME_FIELD;
import static org.kie.kogito.trusty.storage.api.model.DecisionOutcome.OUTCOME_RESULT_FIELD;

public class DecisionOutcomeMarshallerTest extends MarshallerTestTemplate<DecisionOutcome> {

    private static final List<AbstractTestField<DecisionOutcome, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(OUTCOME_ID_FIELD, "test-id", DecisionOutcome::getOutcomeId, DecisionOutcome::setOutcomeId),
            new StringTestField<>(OUTCOME_NAME_FIELD, "test name", DecisionOutcome::getOutcomeName, DecisionOutcome::setOutcomeName),
            new StringTestField<>(EVALUATION_STATUS_FIELD, "SUCCEEDED", DecisionOutcome::getEvaluationStatus, DecisionOutcome::setEvaluationStatus),
            new ObjectTestField<>(OUTCOME_RESULT_FIELD, null, DecisionOutcome::getOutcomeResult, DecisionOutcome::setOutcomeResult, TypedValue.class),
            new CollectionTestField<>(OUTCOME_INPUTS_FIELD, Collections.emptyList(), DecisionOutcome::getOutcomeInputs, DecisionOutcome::setOutcomeInputs, TypedValue.class),
            new CollectionTestField<>(MESSAGES_FIELD, Collections.emptyList(), DecisionOutcome::getMessages, DecisionOutcome::setMessages, Message.class)
    );

    public DecisionOutcomeMarshallerTest() {
        super(DecisionOutcome.class);
    }

    @Override
    protected DecisionOutcome buildEmptyObject() {
        return new DecisionOutcome();
    }

    @Override
    protected MessageMarshaller<DecisionOutcome> buildMarshaller() {
        return new DecisionOutcomeMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<DecisionOutcome, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
