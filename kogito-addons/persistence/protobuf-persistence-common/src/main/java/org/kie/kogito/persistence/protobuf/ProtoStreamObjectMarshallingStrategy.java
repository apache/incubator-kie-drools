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

package org.kie.kogito.persistence.protobuf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.infinispan.protostream.BaseMarshaller;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.ProtobufUtil;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.kogito.persistence.protobuf.marshallers.BooleanMessageMarshaller;
import org.kie.kogito.persistence.protobuf.marshallers.DateMessageMarshaller;
import org.kie.kogito.persistence.protobuf.marshallers.DoubleMessageMarshaller;
import org.kie.kogito.persistence.protobuf.marshallers.FloatMessageMarshaller;
import org.kie.kogito.persistence.protobuf.marshallers.IntegerMessageMarshaller;
import org.kie.kogito.persistence.protobuf.marshallers.LongMessageMarshaller;
import org.kie.kogito.persistence.protobuf.marshallers.StringMessageMarshaller;

public class ProtoStreamObjectMarshallingStrategy implements ObjectMarshallingStrategy {
    
    private SerializationContext serializationContext;
    private Map<String, Class<?>> typeToClassMapping = new ConcurrentHashMap<>();
    
    public ProtoStreamObjectMarshallingStrategy(String proto, BaseMarshaller<?>...marshallers) {
        serializationContext = new SerializationContextImpl(Configuration.builder().build());        
        
        try {
            serializationContext.registerProtoFiles(FileDescriptorSource.fromResources("kogito-types.proto"));
            registerMarshaller(new StringMessageMarshaller(),
                                new IntegerMessageMarshaller(),
                                new LongMessageMarshaller(),
                                new DoubleMessageMarshaller(),
                                new FloatMessageMarshaller(),
                                new BooleanMessageMarshaller(),
                                new DateMessageMarshaller());
            
            if (proto != null) {
                serializationContext.registerProtoFiles(FileDescriptorSource.fromString(UUID.randomUUID().toString(), proto));
                                
                registerMarshaller(marshallers);
                
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }

    @Override
    public boolean accept(Object object) {
        if (object == null) {
            return false;
        }
        return serializationContext.canMarshall(object.getClass());
    }


    @Override
    public byte[] marshal(Context context, ObjectOutputStream os, Object object) throws IOException {
        return ProtobufUtil.toByteArray(serializationContext, object);
                
    }

    @Override
    public Object unmarshal(String dataType, Context context, ObjectInputStream is, byte[] object, ClassLoader classloader) throws IOException, ClassNotFoundException {
        
        return ProtobufUtil.fromByteArray(serializationContext, object, serializationContext.getMarshaller(dataType).getJavaClass());
    }

    @Override
    public String getType(Class<?> clazz) {
        BaseMarshaller<?> marshaller = serializationContext.getMarshaller(clazz);
        if (marshaller == null) {
            throw new IllegalStateException("No marshaller found for class " + clazz.getCanonicalName());
        }
        return marshaller.getTypeName();
    }
    
    public void registerMarshaller(BaseMarshaller<?>... marshallers) {
        for (BaseMarshaller<?> marshaller : marshallers) {
            serializationContext.registerMarshaller(marshaller);
            
            typeToClassMapping.putIfAbsent(marshaller.getTypeName(), marshaller.getJavaClass());
        }
    }

    /*
     * Not used methods
     */    


    @Override
    public Context createContext() {
        return null;
    }

    @Override
    public void write(ObjectOutputStream os, Object object) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException();
    }
}
