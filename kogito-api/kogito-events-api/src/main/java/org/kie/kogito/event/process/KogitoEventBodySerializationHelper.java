/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.event.process;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;

import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class KogitoEventBodySerializationHelper {

    private KogitoEventBodySerializationHelper() {
    }

    public static String readUTF(DataInput in) throws IOException {
        boolean isNotNull = in.readBoolean();
        return isNotNull ? in.readUTF() : null;
    }

    public static void writeUTF(DataOutput out, String string) throws IOException {
        if (string == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeUTF(string);
        }
    }

    public static void writeDate(DataOutput out, Date date) throws IOException {
        if (date == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeLong(date.getTime());
        }
    }

    public static Date readDate(DataInput in) throws IOException {
        boolean isNotNull = in.readBoolean();
        return isNotNull ? new Date(in.readLong()) : null;
    }

    public static void writeTime(DataOutput out, OffsetDateTime date) throws IOException {
        if (date == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeLong(date.toInstant().toEpochMilli());
        }
    }

    public static OffsetDateTime readTime(DataInput in) throws IOException {
        boolean isNotNull = in.readBoolean();
        return isNotNull ? Instant.ofEpochMilli(in.readLong()).atOffset(ZoneOffset.UTC) : null;
    }

    public static void writeUTFCollection(DataOutput out, Collection<String> collection) throws IOException {
        if (collection == null) {
            writeInt(out, -1);
        } else {
            writeInt(out, collection.size());
            for (String item : collection) {
                writeUTF(out, item);
            }
        }
    }

    public static <T extends Collection<String>> T readUTFCollection(DataInput in, T holder) throws IOException {
        int size = readInt(in);
        if (size == -1) {
            return null;
        }
        while (size-- > 0) {
            holder.add(readUTF(in));
        }
        return holder;
    }

    private enum SerType {

        NULL(KogitoEventBodySerializationHelper::writeNull, KogitoEventBodySerializationHelper::readNull),
        JSON(KogitoEventBodySerializationHelper::writeJson, KogitoEventBodySerializationHelper::readJson),
        DEFAULT(KogitoEventBodySerializationHelper::writeJson, KogitoEventBodySerializationHelper::readDefault),
        STRING(KogitoEventBodySerializationHelper::writeString, DataInput::readUTF),
        INT(KogitoEventBodySerializationHelper::writeInt, DataInput::readInt),
        SHORT(KogitoEventBodySerializationHelper::writeShort, DataInput::readShort),
        LONG(KogitoEventBodySerializationHelper::writeLong, DataInput::readLong),
        BYTE(KogitoEventBodySerializationHelper::writeByte, DataInput::readByte),
        BOOLEAN(KogitoEventBodySerializationHelper::writeBoolean, DataInput::readBoolean),
        FLOAT(KogitoEventBodySerializationHelper::writeFloat, DataInput::readFloat),
        DOUBLE(KogitoEventBodySerializationHelper::writeDouble, DataInput::readDouble);

        final ObjectWriter writer;
        final ObjectReader reader;

        SerType(ObjectWriter writer, ObjectReader reader) {
            this.writer = writer;
            this.reader = reader;
        }

        ObjectWriter writer() {
            return writer;
        }

        ObjectReader reader() {
            return reader;
        }

        static SerType fromType(Class<?> type) {
            if (JsonNode.class.isAssignableFrom(type)) {
                return JSON;
            } else if (String.class.isAssignableFrom(type)) {
                return STRING;
            } else if (Boolean.class.isAssignableFrom(type)) {
                return BOOLEAN;
            } else if (Integer.class.isAssignableFrom(type)) {
                return INT;
            } else if (Short.class.isAssignableFrom(type)) {
                return SHORT;
            } else if (Byte.class.isAssignableFrom(type)) {
                return BYTE;
            } else if (Long.class.isAssignableFrom(type)) {
                return LONG;
            } else if (Float.class.isAssignableFrom(type)) {
                return FLOAT;
            } else if (Double.class.isAssignableFrom(type)) {
                return DOUBLE;
            } else {
                return DEFAULT;
            }
        }

        static SerType fromObject(Object obj) {
            return obj == null ? NULL : fromType(obj.getClass());
        }
    }

    private static void writeType(DataOutput out, SerType type) throws IOException {
        out.writeByte(type.ordinal());
    }

    private static SerType readType(DataInput in) throws IOException {
        return SerType.values()[in.readByte()];
    }

    public static void writeObject(DataOutput out, Object obj) throws IOException {
        SerType type = SerType.fromObject(obj);
        writeType(out, type);
        type.writer().accept(out, obj);
    }

    public static Object readObject(DataInput in) throws IOException {
        return readType(in).reader().apply(in);
    }

    @FunctionalInterface
    private static interface ObjectWriter {
        void accept(DataOutput out, Object obj) throws IOException;
    }

    private static interface ObjectReader {
        Object apply(DataInput out) throws IOException;
    }

    private static void writeString(DataOutput out, Object obj) throws IOException {
        out.writeUTF((String) obj);
    }

    private static void writeBoolean(DataOutput out, Object obj) throws IOException {
        out.writeBoolean((Boolean) obj);
    }

    private static void writeInt(DataOutput out, Object obj) throws IOException {
        out.writeInt((Integer) obj);
    }

    private static void writeLong(DataOutput out, Object obj) throws IOException {
        out.writeInt((Integer) obj);
    }

    private static void writeShort(DataOutput out, Object obj) throws IOException {
        out.writeShort((Short) obj);
    }

    private static void writeByte(DataOutput out, Object obj) throws IOException {
        out.writeByte((Byte) obj);
    }

    private static void writeFloat(DataOutput out, Object obj) throws IOException {
        out.writeFloat((Float) obj);
    }

    private static void writeDouble(DataOutput out, Object obj) throws IOException {
        out.writeDouble((Double) obj);
    }

    private static void writeNull(DataOutput out, Object obj) {
        // do nothing
    }

    private static Object readNull(DataInput in) {
        return null;
    }

    public static void writeInteger(DataOutput out, Integer integer) throws IOException {
        if (integer == null) {
            writeType(out, SerType.NULL);
        } else {
            writeInt(out, integer.intValue());
        }
    }

    public static Integer readInteger(DataInput in) throws IOException {
        SerType type = readType(in);
        return type == SerType.NULL ? null : readInt(in, type);
    }

    public static void writeInt(DataOutput out, int size) throws IOException {
        if (size < Byte.MAX_VALUE) {
            writeType(out, SerType.BYTE);
            out.writeByte((byte) size);
        } else if (size < Short.MAX_VALUE) {
            writeType(out, SerType.SHORT);
            out.writeShort((short) size);
        } else {
            writeType(out, SerType.INT);
            out.writeInt(size);
        }
    }

    public static int readInt(DataInput in) throws IOException {
        SerType type = readType(in);
        return readInt(in, type);
    }

    private static int readInt(DataInput in, SerType type) throws IOException {
        switch (type) {
            case INT:
                return in.readInt();
            case SHORT:
                return in.readShort();
            case BYTE:
                return in.readByte();
            default:
                throw new IOException("Stream corrupted. Read unrecognized type " + type);
        }
    }

    private static void writeJson(DataOutput out, Object obj) throws IOException {
        byte[] bytes = ObjectMapperFactory.get().writeValueAsBytes(obj);
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private static Object readJson(DataInput in) throws IOException {
        return readJson(in, JsonNode.class);
    }

    private static Object readDefault(DataInput in) throws IOException {
        return readJson(in, Object.class);
    }

    private static Object readJson(DataInput in, Class<?> type) throws IOException {
        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);
        return ObjectMapperFactory.get().readValue(bytes, type);
    }

    public static Date toDate(OffsetDateTime time) {
        return time == null ? null : Date.from(time.toInstant());
    }
}
