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
package org.kie.kogito.persistence.protobuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.infinispan.protostream.AnnotationMetadataCreator;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.AnnotationElement;
import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.kie.kogito.persistence.api.schema.AttributeDescriptor;
import org.kie.kogito.persistence.api.schema.EntityIndexDescriptor;
import org.kie.kogito.persistence.api.schema.IndexDescriptor;

import static java.util.stream.Collectors.toSet;

class ProtoIndexParser implements AnnotationMetadataCreator<EntityIndexDescriptor, Descriptor> {

    static final String INDEXED_ANNOTATION = "Indexed";
    static final String INDEXED_INDEX_ATTRIBUTE = "index";

    static final String FIELDS_ANNOTATION = "Fields";
    static final String FIELD_ANNOTATION = "Field";
    static final String FIELD_NAME_ATTRIBUTE = "name";
    static final String FIELD_INDEX_ATTRIBUTE = "index";
    static final String FIELD_STORE_ATTRIBUTE = "store";
    static final String FIELD_INDEX_NULL_AS_ATTRIBUTE = "indexNullAs";

    static final String SORTABLE_ANNOTATION = "SortableField";

    static final String INDEX_YES = "Index.YES";
    static final String INDEX_NO = "Index.NO";

    static final String STORE_YES = "Store.YES";
    static final String STORE_NO = "Store.NO";

    static final String DO_NOT_INDEX_NULL = "__DO_NOT_INDEX_NULL__";
    static final String DEFAULT_NULL_TOKEN = "__DEFAULT_NULL_TOKEN__";

    static Configuration.Builder configureBuilder() {
        Configuration.Builder builder = Configuration.builder();
        builder.annotationsConfig()
                .annotation(INDEXED_ANNOTATION, AnnotationElement.AnnotationTarget.MESSAGE)
                .attribute(INDEXED_INDEX_ATTRIBUTE)
                .type(AnnotationElement.AttributeType.STRING)
                .defaultValue("")
                .metadataCreator(new ProtoIndexParser())
                .annotation(FIELD_ANNOTATION, AnnotationElement.AnnotationTarget.FIELD)
                .repeatable(FIELDS_ANNOTATION)
                .attribute(FIELD_NAME_ATTRIBUTE)
                .type(AnnotationElement.AttributeType.STRING)
                .defaultValue("")
                .attribute(FIELD_INDEX_ATTRIBUTE)
                .type(AnnotationElement.AttributeType.IDENTIFIER)
                .allowedValues(INDEX_YES, INDEX_NO)
                .defaultValue(INDEX_YES)
                .attribute(FIELD_STORE_ATTRIBUTE)
                .type(AnnotationElement.AttributeType.IDENTIFIER)
                .allowedValues(STORE_YES, STORE_NO)
                .defaultValue(STORE_NO)
                .attribute(FIELD_INDEX_NULL_AS_ATTRIBUTE)
                .type(AnnotationElement.AttributeType.STRING)
                .allowedValues(DO_NOT_INDEX_NULL, DEFAULT_NULL_TOKEN)
                .defaultValue(DO_NOT_INDEX_NULL)
                .annotation(SORTABLE_ANNOTATION, AnnotationElement.AnnotationTarget.FIELD);
        return builder;
    }

    @Override
    public EntityIndexDescriptor create(Descriptor annotatedDescriptor, AnnotationElement.Annotation annotation) {
        String name = annotatedDescriptor.getFullName();

        List<IndexDescriptor> indexes = new ArrayList<>(annotatedDescriptor.getFields().size());
        List<AttributeDescriptor> fields = new ArrayList<>(annotatedDescriptor.getFields().size());
        for (FieldDescriptor fd : annotatedDescriptor.getFields()) {
            AnnotationElement.Annotation fieldAnnotation = fd.getAnnotations().get(FIELD_ANNOTATION);
            if (fieldAnnotation != null) {
                String fieldName = Optional.ofNullable((String) fieldAnnotation.getAttributeValue(FIELD_NAME_ATTRIBUTE).getValue())
                        .filter(a -> !a.isEmpty()).orElseGet(fd::getName);

                boolean isIndexed = INDEX_YES.equals(fieldAnnotation.getAttributeValue(FIELD_INDEX_ATTRIBUTE).getValue());

                fields.add(createAttributeDescriptor(fd, fieldName));

                if (isIndexed) {
                    indexes.add(new IndexDescriptor(fieldName, List.of(fieldName)));
                }
            }
        }

        return new EntityIndexDescriptor(name, indexes, fields);
    }

    static Map<String, EntityIndexDescriptor> createEntityIndexDescriptors(FileDescriptor desc, Map<String, EntityIndexDescriptor> entityIndexes) {
        desc.getMessageTypes().forEach(mDesc -> {
            String typeName = mDesc.getFullName();
            EntityIndexDescriptor entityIndex = entityIndexes.get(typeName);
            if (entityIndex != null) {
                // Add the fields without index
                Set<String> fieldNames = entityIndex.getAttributeDescriptors().stream().map(AttributeDescriptor::getName).collect(toSet());
                mDesc.getFields().stream().filter(fDesc -> !fieldNames.contains(fDesc.getName()))
                        .forEach(fDesc -> entityIndex.getAttributeDescriptors().add(createAttributeDescriptor(fDesc, null)));
            }
        });

        return entityIndexes;
    }

    static AttributeDescriptor createAttributeDescriptor(FieldDescriptor fDesc, String name) {
        String fieldName = (Objects.isNull(name) || name.isEmpty()) ? fDesc.getName() : name;

        boolean isPrimitiveType = !Objects.isNull(fDesc.getType()) && !Objects.isNull(fDesc.getJavaType());

        String typeName = fDesc.getTypeName();

        return new AttributeDescriptor(fieldName, typeName, isPrimitiveType);
    }
}
