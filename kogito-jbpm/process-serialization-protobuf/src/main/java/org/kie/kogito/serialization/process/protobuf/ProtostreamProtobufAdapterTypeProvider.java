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

package org.kie.kogito.serialization.process.protobuf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.EnumDescriptor;
import org.infinispan.protostream.descriptors.EnumValueDescriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.impl.SerializationContextImpl;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumValueDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;

/**
 * This creates a bridge between protostream and protobuf so protostream descriptors can be used as protobuf descriptors
 *
 */
public class ProtostreamProtobufAdapterTypeProvider implements ProtobufTypeProvider {

    public Collection<Descriptors.Descriptor> descriptors() {
        try {
            return build().stream().flatMap(e -> e.getMessageTypes().stream()).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Conversion protostream protobuf type not possible", e);
        }
    }

    private Collection<String> protostreamDescriptors() {
        return Arrays.asList("META-INF/kogito-types.proto", "META-INF/application-types.proto");
    }

    private List<com.google.protobuf.Descriptors.FileDescriptor> build() throws IOException, DescriptorValidationException {
        SerializationContextImpl context = buildSerializationContext();

        // we transform protostream to protobuf descriptors
        List<com.google.protobuf.Descriptors.FileDescriptor> protos = new ArrayList<>();
        Map<String, FileDescriptor> descriptors = context.getFileDescriptors();
        for (Map.Entry<String, FileDescriptor> entry : descriptors.entrySet()) {
            com.google.protobuf.Descriptors.FileDescriptor[] deps = protos.toArray(new com.google.protobuf.Descriptors.FileDescriptor[protos.size()]);
            protos.add(Descriptors.FileDescriptor.buildFrom(buildMessageTypes(entry.getValue()),deps));
            protos.add(Descriptors.FileDescriptor.buildFrom(buildEnumTypes(entry.getValue()),deps));
        }
        return protos;
    }

    private SerializationContextImpl buildSerializationContext() throws IOException, DescriptorValidationException {
        SerializationContextImpl context = new SerializationContextImpl(Configuration.builder().build());
        for (String protoFile : protostreamDescriptors()) {
            try (InputStream is = ProtostreamProtobufAdapterTypeProvider.class.getClassLoader().getResourceAsStream(protoFile)) {
                if (is == null) {
                    continue;
                }
                int idx = protoFile.indexOf(File.separator);
                String fileName = idx >= 0 && idx + 1 < protoFile.length() ? protoFile.substring(idx + 1) : protoFile;
                FileDescriptorSource source = new FileDescriptorSource().addProtoFile(fileName, is);
                context.registerProtoFiles(source);
            }
        }
        return context;
    }

    private FileDescriptorProto buildEnumTypes(FileDescriptor descriptor) {
        FileDescriptorProto.Builder protoFileBuilder = FileDescriptorProto.newBuilder();
        protoFileBuilder.setPackage(descriptor.getPackage());
        protoFileBuilder.setName(descriptor.getName());
        for (EnumDescriptor enumDescriptor : descriptor.getEnumTypes()) {
            protoFileBuilder.addEnumType(buildEnum(enumDescriptor));
        }
        return protoFileBuilder.build();
    }

    private EnumDescriptorProto buildEnum(EnumDescriptor enumDescriptor) {
        EnumDescriptorProto.Builder enumBuilder = EnumDescriptorProto.newBuilder();
        enumBuilder.setName(enumDescriptor.getName());
        for(EnumValueDescriptor enumValueDescriptor : enumDescriptor.getValues()) {
            enumBuilder.addValue(buildEnumValue(enumValueDescriptor));
        }
        return enumBuilder.build();
    }

    private EnumValueDescriptorProto buildEnumValue(EnumValueDescriptor enumValueDescriptor) {
        EnumValueDescriptorProto.Builder enumValueBuilder = EnumValueDescriptorProto.newBuilder();
        enumValueBuilder.setName(enumValueDescriptor.getName());
        enumValueBuilder.setNumber(enumValueDescriptor.getNumber());
        return enumValueBuilder.build();
    }

    private FileDescriptorProto buildMessageTypes(FileDescriptor descriptor) {
        FileDescriptorProto.Builder protoFileBuilder = FileDescriptorProto.newBuilder();
        protoFileBuilder.setPackage(descriptor.getPackage());
        protoFileBuilder.setName(descriptor.getName());
        for (Descriptor message : descriptor.getMessageTypes()) {
            protoFileBuilder.addMessageType(buildMessage(message));
        }
        return protoFileBuilder.build();
    }

    private DescriptorProto buildMessage(Descriptor descriptor) {
        DescriptorProto.Builder messageBuilder = DescriptorProto.newBuilder();
        messageBuilder.setName(descriptor.getName());
        for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            messageBuilder.addField(buildFieldDescriptor(fieldDescriptor));
        }
        return messageBuilder.build();
    }

    private FieldDescriptorProto buildFieldDescriptor(FieldDescriptor descriptor) {
        FieldDescriptorProto.Builder fieldBuilder = FieldDescriptorProto.newBuilder();
        fieldBuilder.setName(descriptor.getName());
        fieldBuilder.setNumber(descriptor.getNumber());
        fieldBuilder.setType(buildFieldTypeDescriptor(descriptor.getType()));
        EnumSet<FieldDescriptorProto.Type> set = EnumSet.of(FieldDescriptorProto.Type.TYPE_ENUM, FieldDescriptorProto.Type.TYPE_MESSAGE);
        if(set.contains(fieldBuilder.getType())) {
            fieldBuilder.setTypeName(descriptor.getTypeName());
        }
        fieldBuilder.setProto3Optional(!descriptor.isRequired());
        return fieldBuilder.build();
    }

    private FieldDescriptorProto.Type buildFieldTypeDescriptor(org.infinispan.protostream.descriptors.Type type) {
        switch (type) {
            case BOOL:
                return FieldDescriptorProto.Type.TYPE_BOOL;
            case BYTES:
                return FieldDescriptorProto.Type.TYPE_BYTES;
            case DOUBLE:
                return FieldDescriptorProto.Type.TYPE_DOUBLE;
            case ENUM:
                return FieldDescriptorProto.Type.TYPE_ENUM;
            case FIXED32:
                return FieldDescriptorProto.Type.TYPE_FIXED32;
            case FIXED64:
                return FieldDescriptorProto.Type.TYPE_FIXED64;
            case FLOAT:
                return FieldDescriptorProto.Type.TYPE_FLOAT;
            case GROUP:
                return FieldDescriptorProto.Type.TYPE_GROUP;
            case INT32:
                return FieldDescriptorProto.Type.TYPE_INT32;
            case INT64:
                return FieldDescriptorProto.Type.TYPE_INT64;
            case MESSAGE:
                return FieldDescriptorProto.Type.TYPE_MESSAGE;
            case SFIXED32:
                return FieldDescriptorProto.Type.TYPE_SFIXED32;
            case SFIXED64:
                return FieldDescriptorProto.Type.TYPE_SFIXED64;
            case SINT32:
                return FieldDescriptorProto.Type.TYPE_SINT32;
            case SINT64:
                return FieldDescriptorProto.Type.TYPE_SINT64;
            case STRING:
                return FieldDescriptorProto.Type.TYPE_STRING;
            case UINT32:
                return FieldDescriptorProto.Type.TYPE_UINT32;
            case UINT64:
                return FieldDescriptorProto.Type.TYPE_UINT64;
            default:
                throw new RuntimeException("Conversion protostream protobuf type not found");
        }
    }

}
