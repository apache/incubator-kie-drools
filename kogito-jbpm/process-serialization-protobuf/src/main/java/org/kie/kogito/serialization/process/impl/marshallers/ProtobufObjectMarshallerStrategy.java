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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

import org.kie.kogito.serialization.process.ObjectMarshallerStrategy;
import org.kie.kogito.serialization.process.ProcessInstanceMarshallerException;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.InvalidProtocolBufferException;

public class ProtobufObjectMarshallerStrategy implements ObjectMarshallerStrategy {

    @Override
    public Integer order() {
        return 1;
    }

    @Override
    public boolean acceptForMarshalling(Object value) {
        return true;
    }

    @Override
    public boolean acceptForUnmarshalling(Any value) {
        return value.is(BytesValue.class);
    }

    @Override
    public Any marshall(Object unmarshalled) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(stream)) {
            out.writeObject(unmarshalled);
            return Any.pack(BytesValue.of(ByteString.copyFrom(stream.toByteArray())));
        } catch (IOException e) {
            throw new ProcessInstanceMarshallerException("Not possible to unmarshall value: " + unmarshalled, e);
        }
    }

    @Override
    public Object unmarshall(Any data) {
        try {
            BytesValue storedValue = data.unpack(BytesValue.class);
            if (ByteString.EMPTY.equals(storedValue.getValue())) {
                return null;
            }
            return readObject(storedValue.getValue().toByteArray());
        } catch (InvalidProtocolBufferException e1) {
            throw new ProcessInstanceMarshallerException("Unexpected error during protobuf object unmarshalling", e1);
        }
    }

    private Object readObject(byte[] data) {
        try (InputStream is = new ByteArrayInputStream(data); ObjectInputStream ois = new ObjectInputStream(is) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                try {
                    return Class.forName(desc.getName(), false, Thread.currentThread().getContextClassLoader());
                } catch (ClassNotFoundException ex) {
                    return super.resolveClass(desc);
                }
            }
        }) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new ProcessInstanceMarshallerException("Unexpected error while trying to read object", e);
        }
    }

}
