/*
 * Copyright 2019 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.remote.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationUtil {

    private SerializationUtil() {}

    public static byte[] serialize(Object obj) {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(obj);
            }
            return b.toByteArray();
        } catch (IOException io) {
            throw new IllegalArgumentException(io.getMessage(), io);
        }
    }

    public static <T> T deserialize(byte[] bytez) {
        try {
            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(bytez));
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
