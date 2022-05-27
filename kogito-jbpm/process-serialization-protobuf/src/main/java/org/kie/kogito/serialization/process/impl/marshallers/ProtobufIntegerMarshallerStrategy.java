/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.serialization.process.impl.marshallers;

import org.kie.kogito.serialization.process.ObjectMarshallerStrategy;
import org.kie.kogito.serialization.process.ProcessInstanceMarshallerException;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;

public class ProtobufIntegerMarshallerStrategy implements ObjectMarshallerStrategy {

    @Override
    public boolean acceptForMarshalling(Object value) {
        return Integer.class.equals(value.getClass());
    }

    @Override
    public boolean acceptForUnmarshalling(Any value) {
        return value.is(Int32Value.class);
    }

    @Override
    public Any marshall(Object unmarshalled) {
        return Any.pack(Int32Value.of((Integer) unmarshalled));
    }

    @Override
    public Object unmarshall(Any data) {
        try {
            Int32Value storedValue = data.unpack(Int32Value.class);
            return storedValue.getValue();
        } catch (InvalidProtocolBufferException e1) {
            throw new ProcessInstanceMarshallerException("Error trying to unmarshalling a int value", e1);
        }
    }
}
