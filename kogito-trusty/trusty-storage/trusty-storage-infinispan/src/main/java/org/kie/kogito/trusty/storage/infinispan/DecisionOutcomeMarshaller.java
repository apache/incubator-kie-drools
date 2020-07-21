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
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.Message;
import org.kie.kogito.trusty.storage.api.model.TypedValue;

public class DecisionOutcomeMarshaller extends AbstractModelMarshaller<DecisionOutcome> {

    public DecisionOutcomeMarshaller(ObjectMapper mapper) {
        super(mapper, DecisionOutcome.class);
    }

    @Override
    public DecisionOutcome readFrom(ProtoStreamReader reader) throws IOException {
        return new DecisionOutcome(
                reader.readString(DecisionOutcome.OUTCOME_ID_FIELD),
                reader.readString(DecisionOutcome.OUTCOME_NAME_FIELD),
                reader.readString(DecisionOutcome.EVALUATION_STATUS_FIELD),
                reader.readObject(DecisionOutcome.OUTCOME_RESULT_FIELD, TypedValue.class),
                reader.readCollection(DecisionOutcome.OUTCOME_INPUTS_FIELD, new ArrayList<>(), TypedValue.class),
                reader.readCollection(DecisionOutcome.MESSAGES_FIELD, new ArrayList<>(), Message.class)
        );
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, DecisionOutcome input) throws IOException {
        writer.writeString(DecisionOutcome.OUTCOME_ID_FIELD, input.getOutcomeId());
        writer.writeString(DecisionOutcome.OUTCOME_NAME_FIELD, input.getOutcomeName());
        writer.writeString(DecisionOutcome.EVALUATION_STATUS_FIELD, input.getEvaluationStatus());
        writer.writeObject(DecisionOutcome.OUTCOME_RESULT_FIELD, input.getOutcomeResult(), TypedValue.class);
        writer.writeCollection(DecisionOutcome.OUTCOME_INPUTS_FIELD, input.getOutcomeInputs(), TypedValue.class);
        writer.writeCollection(DecisionOutcome.MESSAGES_FIELD, input.getMessages(), Message.class);
    }
}
