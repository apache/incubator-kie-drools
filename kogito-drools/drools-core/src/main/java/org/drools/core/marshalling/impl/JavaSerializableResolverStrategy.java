/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.marshalling.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;

public class JavaSerializableResolverStrategy
        implements
        ObjectMarshallingStrategy {

    private ObjectMarshallingStrategyAcceptor acceptor;

    public JavaSerializableResolverStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        this.acceptor = acceptor;
    }

    public Object read(ObjectInputStream os) throws IOException,
            ClassNotFoundException {
        return os.readObject();
    }

    public void write(ObjectOutputStream os,
                      Object object) throws IOException {
        os.writeObject(object);
    }

    public boolean accept(Object object) {
        return this.acceptor.accept(object);
    }

    public byte[] marshal(Context context,
                          ObjectOutputStream os,
                          Object object) {
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream()) {
            os = new ObjectOutputStream(bs);
            write(os, object);
            return bs.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object unmarshal(Context context,
                            ObjectInputStream is,
                            byte[] object,
                            ClassLoader classloader) {
        try (ByteArrayInputStream bs = new ByteArrayInputStream(object)) {
            is = new ObjectInputStream(bs) {
                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
                    return Class.forName(desc.getName(), true, classloader);
                }
            };
            return read(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Context createContext() {
        // no need for context
        return null;
    }

    @Override
    public String toString() {
        return "JavaSerializableResolverStrategy{" +
                "acceptor=" + acceptor +
                '}';
    }
}
