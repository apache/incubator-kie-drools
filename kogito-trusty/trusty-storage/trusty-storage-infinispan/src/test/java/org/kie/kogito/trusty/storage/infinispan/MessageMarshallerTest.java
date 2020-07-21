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
import org.kie.kogito.tracing.decision.event.common.MessageLevel;
import org.kie.kogito.trusty.storage.api.model.Message;
import org.kie.kogito.trusty.storage.api.model.MessageExceptionField;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.EnumTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.ObjectTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import static org.kie.kogito.trusty.storage.api.model.Message.CATEGORY_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Message.EXCEPTION_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Message.LEVEL_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Message.SOURCE_ID_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Message.TEXT_FIELD;
import static org.kie.kogito.trusty.storage.api.model.Message.TYPE_FIELD;

public class MessageMarshallerTest extends MarshallerTestTemplate<Message> {

    private static final List<AbstractTestField<Message, ?>> TEST_FIELD_LIST = List.of(
            new EnumTestField<>(LEVEL_FIELD, MessageLevel.INFO, Message::getLevel, Message::setLevel, MessageLevel.class),
            new StringTestField<>(CATEGORY_FIELD, "testCategory", Message::getCategory, Message::setCategory),
            new StringTestField<>(TYPE_FIELD, "testType", Message::getType, Message::setType),
            new StringTestField<>(SOURCE_ID_FIELD, "testSourceId", Message::getSourceId, Message::setSourceId),
            new StringTestField<>(TEXT_FIELD, "test message text", Message::getText, Message::setText),
            new ObjectTestField<>(EXCEPTION_FIELD, new MessageExceptionField("exc", "exc message", null), Message::getException, Message::setException, MessageExceptionField.class)
    );

    public MessageMarshallerTest() {
        super(Message.class);
    }

    @Override
    protected Message buildEmptyObject() {
        return new Message();
    }

    @Override
    protected MessageMarshaller<Message> buildMarshaller() {
        return new org.kie.kogito.trusty.storage.infinispan.MessageMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<Message, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
