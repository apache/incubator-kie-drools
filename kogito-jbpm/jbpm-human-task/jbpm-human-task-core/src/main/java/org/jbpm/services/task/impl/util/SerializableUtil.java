/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.impl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableUtil {

    private SerializableUtil() {
        // static methods
    }

    public static byte[] serialize( Serializable object ) throws IOException {
        return serialize(object, Thread.currentThread().getContextClassLoader());
    }

    public static byte[] serialize( Serializable object, ClassLoader classLoader ) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] result;
        try {
            out = new ObjectOutputStream(outputStream);
            out.writeObject(object);
            result = outputStream.toByteArray();
        } finally {
            try {
                if( out != null ) {
                    out.close();
                }
            } catch( IOException ex ) {
                // ignore close exception
            }
            try {
                outputStream.close();
            } catch( IOException ex ) {
                // ignore close exception
            }
        }
        return result;
    }

    public static Serializable deserialize( byte[] byteArray ) throws IOException, ClassNotFoundException {
        return deserialize(byteArray, Thread.currentThread().getContextClassLoader());
    }

    public static Serializable deserialize( byte[] byteArray, ClassLoader classLoader ) throws IOException, ClassNotFoundException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(byteArray);
        ObjectInput objectIn = null;
        Serializable result;
        try {
            objectIn = new ObjectInputStream(byteArrayIn);
            result = (Serializable) objectIn.readObject();
        } finally {
            try {
                byteArrayIn.close();
            } catch( IOException ex ) {
                // ignore close exception
            }
            try {
                if( objectIn != null ) {
                    objectIn.close();
                }
            } catch( IOException ex ) {
                // ignore close exception
            }
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
        return result;
    }

}
