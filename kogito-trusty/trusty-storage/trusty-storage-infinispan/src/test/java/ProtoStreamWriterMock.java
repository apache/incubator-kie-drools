/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.infinispan.protostream.ImmutableSerializationContext;
import org.infinispan.protostream.MessageMarshaller;

public class ProtoStreamWriterMock implements MessageMarshaller.ProtoStreamWriter {

    private Map<String, Object> document = new HashMap<>();

    public List<String> getWrittenFieldNames() {
        return new ArrayList<>(document.keySet());
    }

    @Override
    public ImmutableSerializationContext getSerializationContext() {
        return null;
    }

    @Override
    public void writeInt(String fieldName, int value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeInt(String fieldName, Integer value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeInts(String fieldName, int[] values) throws IOException {
        document.put(fieldName, values);
    }

    @Override
    public void writeLong(String fieldName, long value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeLong(String fieldName, Long value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeLongs(String fieldName, long[] values) throws IOException {
        document.put(fieldName, values);
    }

    @Override
    public void writeDate(String fieldName, Date value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeInstant(String fieldName, Instant value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeDouble(String fieldName, double value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeDouble(String fieldName, Double value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeDoubles(String fieldName, double[] values) throws IOException {
        document.put(fieldName, values);
    }

    @Override
    public void writeFloat(String fieldName, float value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeFloat(String fieldName, Float value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeFloats(String fieldName, float[] values) throws IOException {
        document.put(fieldName, values);
    }

    @Override
    public void writeBoolean(String fieldName, boolean value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeBoolean(String fieldName, Boolean value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeBooleans(String fieldName, boolean[] values) throws IOException {
        document.put(fieldName, values);
    }

    @Override
    public void writeString(String fieldName, String value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeBytes(String fieldName, byte[] value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public void writeBytes(String fieldName, InputStream input) throws IOException {
        document.put(fieldName, input);
    }

    @Override
    public <E> void writeObject(String fieldName, E value, Class<? extends E> clazz) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public <E extends Enum<E>> void writeEnum(String fieldName, E value, Class<E> clazz) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public <E extends Enum<E>> void writeEnum(String fieldName, E value) throws IOException {
        document.put(fieldName, value);
    }

    @Override
    public <E> void writeCollection(String fieldName, Collection<? super E> collection, Class<E> elementClass) throws IOException {
        document.put(fieldName, collection);
    }

    @Override
    public <E> void writeArray(String fieldName, E[] array, Class<? extends E> elementClass) throws IOException {
        document.put(fieldName, array);
    }
}
