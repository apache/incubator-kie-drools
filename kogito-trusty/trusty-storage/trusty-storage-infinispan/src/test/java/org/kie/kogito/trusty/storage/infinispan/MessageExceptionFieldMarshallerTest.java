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
import org.kie.kogito.trusty.storage.api.model.MessageExceptionField;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.ObjectTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import static org.kie.kogito.trusty.storage.api.model.MessageExceptionField.CAUSE_FIELD;
import static org.kie.kogito.trusty.storage.api.model.MessageExceptionField.CLASS_NAME_FIELD;
import static org.kie.kogito.trusty.storage.api.model.MessageExceptionField.MESSAGE_FIELD;

public class MessageExceptionFieldMarshallerTest extends MarshallerTestTemplate<MessageExceptionField> {

    private static final List<AbstractTestField<MessageExceptionField, ?>> TEST_FIELD_LIST = List.of(
            new StringTestField<>(CLASS_NAME_FIELD, "class name", MessageExceptionField::getClassName, MessageExceptionField::setClassName),
            new StringTestField<>(MESSAGE_FIELD, "message", MessageExceptionField::getMessage, MessageExceptionField::setMessage),
            new ObjectTestField<>(CAUSE_FIELD, new MessageExceptionField("cause", "cause message", null), MessageExceptionField::getCause, MessageExceptionField::setCause, MessageExceptionField.class)
    );

    public MessageExceptionFieldMarshallerTest() {
        super(MessageExceptionField.class);
    }

    @Override
    protected MessageExceptionField buildEmptyObject() {
        return new MessageExceptionField();
    }

    @Override
    protected MessageMarshaller<MessageExceptionField> buildMarshaller() {
        return new MessageExceptionFieldMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<MessageExceptionField, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
