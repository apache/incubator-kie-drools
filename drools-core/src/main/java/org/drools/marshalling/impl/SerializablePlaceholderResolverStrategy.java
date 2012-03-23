/*
 * Copyright 2010 JBoss Inc
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

package org.drools.marshalling.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.drools.common.DroolsObjectInputStream;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;

public class SerializablePlaceholderResolverStrategy
    implements
    ObjectMarshallingStrategy {

    private int index;
    
    private ObjectMarshallingStrategyAcceptor acceptor;
    
    public SerializablePlaceholderResolverStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        this.acceptor = acceptor;
    }
    
    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Object read(ObjectInputStream os) throws IOException,
                                                       ClassNotFoundException {
        return  os.readObject();
    }

    public void write(ObjectOutputStream os,
                      Object object) throws IOException {
        os.writeObject( object );
    }

    public boolean accept(Object object) {
        return acceptor.accept( object );
    }

    public byte[] marshal(ObjectOutputStream os,
                          Object object) throws IOException {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( buff );
        oos.writeObject( object );
        oos.close();
        return buff.toByteArray();
    }

    public Object unmarshal(ObjectInputStream is,
                            byte[] object, 
                            ClassLoader classloader) throws IOException, ClassNotFoundException {
        return new DroolsObjectInputStream( new ByteArrayInputStream( object ), classloader ).readObject();
    }

}
