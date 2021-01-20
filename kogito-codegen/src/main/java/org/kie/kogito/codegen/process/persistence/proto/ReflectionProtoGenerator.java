/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process.persistence.proto;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.infinispan.protostream.annotations.ProtoEnumValue;
import org.kie.internal.kogito.codegen.Generated;
import org.kie.internal.kogito.codegen.VariableInfo;
import org.kie.kogito.codegen.GeneratedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionProtoGenerator extends AbstractProtoGenerator<Class<?>> {

    private ReflectionProtoGenerator(Class<?> persistenceClass, Collection<Class<?>> modelClasses, Collection<Class<?>> dataClasses) {
        super(persistenceClass, modelClasses, dataClasses);
    }

    @Override
    public Proto protoOfDataClasses(String packageName, String... headers) {
        try {
            Proto proto = new Proto(packageName, headers);
            for (Class<?> clazz : dataClasses) {
                if (clazz.isEnum()) {
                    enumFromClass(proto, clazz, null);
                } else {
                    messageFromClass(proto, clazz, null, null, null);
                }
            }
            return proto;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating proto for data model", e);
        }
    }

    @Override
    public Proto generate(String messageComment, String fieldComment, String packageName, Class<?> dataModel, String... headers) {
        try {
            Proto proto = new Proto(packageName, headers);
            if (dataModel.isEnum()) {
                enumFromClass(proto, dataModel, null);
            } else {
                messageFromClass(proto, dataModel, packageName, messageComment, fieldComment);
            }
            return proto;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating proto for model class " + dataModel, e);
        }
    }

    @Override
    public Collection<String> getPersistenceClassParams() {
        Collection<String> parameters = new ArrayList<>();
        if (persistenceClass != null) {
            for (Type t : persistenceClass.getConstructors()[0].getGenericParameterTypes()) {
                parameters.add(t.getTypeName());
            }
        }
        return parameters;
    }

    protected ProtoMessage messageFromClass(Proto proto, Class<?> clazz, String packageName, String messageComment, String fieldComment) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        String name = beanInfo.getBeanDescriptor().getBeanClass().getSimpleName();

        Generated generatedData = clazz.getAnnotation(Generated.class);
        if (generatedData != null) {
            name = generatedData.name().isEmpty() ? name : generatedData.name();
            if (generatedData.hidden()) {
                // since class is marked as hidden skip processing of that class
                return null;
            }
        }

        ProtoMessage message = new ProtoMessage(name, packageName == null ? clazz.getPackage().getName() : packageName);

        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            String completeFieldComment = fieldComment;
            if (pd.getName().equals("class")) {
                continue;
            }
            // ignore static and/or transient fields
            int mod = clazz.getDeclaredField(pd.getName()).getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
                continue;
            }

            VariableInfo varInfo = clazz.getDeclaredField(pd.getName()).getAnnotation(VariableInfo.class);
            if (varInfo != null) {
                completeFieldComment = fieldComment + "\n @VariableInfo(tags=\"" + varInfo.tags() + "\")";
            }

            String fieldTypeString = pd.getPropertyType().getCanonicalName();
            Class<?> fieldType = pd.getPropertyType();
            String protoType;
            if (Collection.class.isAssignableFrom(pd.getPropertyType())) {
                fieldTypeString = "Collection";
                Field f = clazz.getDeclaredField(pd.getName());
                Type type = f.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType ptype = (ParameterizedType) type;
                    fieldType = (Class<?>) ptype.getActualTypeArguments()[0];
                    protoType = protoType(fieldType.getCanonicalName());
                } else {
                    throw new IllegalArgumentException("Field " + f.getName() + " of class " + clazz + " uses collection without type information");
                }
            } else {
                protoType = protoType(fieldTypeString);
            }

            if (protoType == null) {
                if (fieldType.isEnum()) {
                    protoType = enumFromClass(proto, fieldType, packageName).getName();
                } else {
                    protoType = messageFromClass(proto, fieldType, packageName, messageComment, fieldComment).getName();
                }
            }

            message.addField(applicabilityByType(fieldTypeString), protoType, pd.getName()).setComment(completeFieldComment);
        }
        message.setComment(messageComment);
        proto.addMessage(message);
        return message;
    }

    protected ProtoEnum enumFromClass(Proto proto, Class<?> clazz, String packageName) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        String name = beanInfo.getBeanDescriptor().getBeanClass().getSimpleName();

        Generated generatedData = clazz.getAnnotation(Generated.class);
        if (generatedData != null) {
            name = generatedData.name().isEmpty() ? name : generatedData.name();
            if (generatedData.hidden()) {
                // since class is marked as hidden skip processing of that class
                return null;
            }
        }

        ProtoEnum modelEnum = new ProtoEnum(name, packageName == null ? clazz.getPackage().getName() : packageName);
        Stream.of(clazz.getDeclaredFields())
                .filter(f -> !f.getName().startsWith("$"))
                .forEach(f -> addEnumField(f, modelEnum));
        proto.addEnum(modelEnum);
        return modelEnum;
    }

    private void addEnumField(Field field, ProtoEnum pEnum) {
        ProtoEnumValue protoEnumValue = field.getAnnotation(ProtoEnumValue.class);
        Integer ordinal = null;
        if (protoEnumValue != null) {
            ordinal = protoEnumValue.number();
        }
        if (ordinal == null) {
            ordinal = pEnum.getFields()
                    .values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(-1) + 1;
        }
        pEnum.addField(field.getName(), ordinal);
    }

    @Override
    protected Optional<GeneratedFile> generateModelClassProto(Class<?> modelClazz) {

        Generated generatedData = modelClazz.getAnnotation(Generated.class);
        if (generatedData != null) {

            String processId = generatedData.reference();
            Proto modelProto = generate("@Indexed",
                                        INDEX_COMMENT,
                                        modelClazz.getPackage().getName() + "." + processId, modelClazz, "import \"kogito-index.proto\";",
                                        "import \"kogito-types.proto\";",
                                        "option kogito_model = \"" + generatedData.name() + "\";",
                                        "option kogito_id = \"" + processId + "\";");
            if (modelProto.getMessages().isEmpty()) {
                // no messages, nothing to do
                return Optional.empty();
            }
            ProtoMessage modelMessage = modelProto.getMessages().stream().filter(msg -> msg.getName().equals(generatedData.name())).findFirst().orElseThrow(() -> new IllegalStateException("Unable to find model message"));
            modelMessage.addField("optional", "org.kie.kogito.index.model.KogitoMetadata", "metadata").setComment(INDEX_COMMENT);

            return Optional.of(generateProtoFiles(processId, modelProto));
        }
        return Optional.empty();
    }

    public static Builder<Class<?>, ReflectionProtoGenerator> builder() {
        return new ReflectionProtoGeneratorBuilder();
    }

    private static class ReflectionProtoGeneratorBuilder extends AbstractProtoGeneratorBuilder<Class<?>, ReflectionProtoGenerator> {

        private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionProtoGeneratorBuilder.class);

        private ReflectionProtoGeneratorBuilder() {
        }

        @Override
        protected Collection<Class<?>> extractDataClasses(Collection<Class<?>> modelClasses) {
            if(dataClasses != null || modelClasses == null) {
                LOGGER.info("Using provided dataClasses instead of extracting from modelClasses");
                return dataClasses;
            }
            Set<Class<?>> dataModelClasses = new HashSet<>();
            try {
                for (Class<?> modelClazz : modelClasses) {

                    BeanInfo beanInfo = Introspector.getBeanInfo(modelClazz);
                    for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                        Class<?> propertyType = pd.getPropertyType();
                        if (propertyType.getCanonicalName().startsWith("java.lang")
                                || propertyType.getCanonicalName().equals(Date.class.getCanonicalName())) {
                            continue;
                        }

                        dataModelClasses.add(propertyType);
                    }
                }
                return dataModelClasses;
            } catch (IntrospectionException e) {
                throw new IllegalStateException("Error during bean introspection", e);
            }
        }

        @Override
        public ReflectionProtoGenerator build(Collection<Class<?>> modelClasses) {
            return new ReflectionProtoGenerator(persistenceClass, modelClasses, extractDataClasses(modelClasses));
        }
    }
}
