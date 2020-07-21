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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.tracing.decision.event.common.MessageLevel;
import org.kie.kogito.trusty.storage.api.model.Message;
import org.kie.kogito.trusty.storage.api.model.MessageExceptionField;

public class MessageMarshaller extends AbstractModelMarshaller<Message> {

    public MessageMarshaller(ObjectMapper mapper) {
        super(mapper, Message.class);
    }

    @Override
    public Message readFrom(ProtoStreamReader reader) throws IOException {
        return new Message(
                enumFromString(reader.readString(Message.LEVEL_FIELD), MessageLevel.class),
                reader.readString(Message.CATEGORY_FIELD),
                reader.readString(Message.TYPE_FIELD),
                reader.readString(Message.SOURCE_ID_FIELD),
                reader.readString(Message.TEXT_FIELD),
                reader.readObject(Message.EXCEPTION_FIELD, MessageExceptionField.class)
        );
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Message input) throws IOException {
        writer.writeString(Message.LEVEL_FIELD, stringFromEnum(input.getLevel()));
        writer.writeString(Message.CATEGORY_FIELD, input.getCategory());
        writer.writeString(Message.TYPE_FIELD, input.getType());
        writer.writeString(Message.SOURCE_ID_FIELD, input.getSourceId());
        writer.writeString(Message.TEXT_FIELD, input.getText());
        writer.writeObject(Message.EXCEPTION_FIELD, input.getException(), MessageExceptionField.class);
    }

    @Override
    public String getTypeName() {
        /*
          The org.kie.kogito.trusty.storage.api.model.Message model is mapped to the ExecutionMessage
          protobuf definition as a workaround for an error in infinispan protostream library:
          using the Message name throws an exception during serialization.
         */
        return String.format("%s.%s", getJavaClass().getPackageName(), "ExecutionMessage");
    }
}
