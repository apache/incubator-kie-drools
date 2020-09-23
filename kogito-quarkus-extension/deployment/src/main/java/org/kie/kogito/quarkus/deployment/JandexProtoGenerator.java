/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.quarkus.deployment;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.infinispan.protostream.annotations.ProtoEnumValue;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Type;
import org.jboss.jandex.Type.Kind;
import org.kie.kogito.codegen.process.persistence.proto.AbstractProtoGenerator;
import org.kie.kogito.codegen.process.persistence.proto.Proto;
import org.kie.kogito.codegen.process.persistence.proto.ProtoEnum;
import org.kie.kogito.codegen.process.persistence.proto.ProtoMessage;

public class JandexProtoGenerator extends AbstractProtoGenerator<ClassInfo> {

    private static final DotName ENUM_VALUE_ANNOTATION = DotName.createSimple(ProtoEnumValue.class.getName());
    private final IndexView index;
    private final DotName generatedAnnotation;
    private final DotName variableInfoAnnotation;

    public JandexProtoGenerator(IndexView index, DotName generatedAnnotation, DotName variableInfoAnnotation) {
        this.index = index;
        this.generatedAnnotation = generatedAnnotation;
        this.variableInfoAnnotation = variableInfoAnnotation;
    }

    public Proto generate(String packageName, Collection<ClassInfo> dataModel, String... headers) {
        try {
            Proto proto = new Proto(packageName, headers);

            for (ClassInfo clazz : dataModel) {
                if (clazz.superName() != null && Enum.class.getName().equals(clazz.superName().toString())) {
                    enumFromClass(proto, clazz, null);
                } else {
                    messageFromClass(proto, clazz, index, null, null, null);
                }
            }
            return proto;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating proto for data model", e);
        }
    }

    @Override
    public Proto generate(String messageComment, String fieldComment, String packageName, ClassInfo dataModel,
                          String... headers) {
        try {
            Proto proto = new Proto(packageName, headers);
            if (dataModel.superName() != null && Enum.class.getName().equals(dataModel.superName().toString())) {
                enumFromClass(proto, dataModel, null);
            } else {
                messageFromClass(proto, dataModel, index, packageName, messageComment, fieldComment);
            }
            return proto;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating proto for data model", e);
        }
    }

    protected ProtoMessage messageFromClass(Proto proto, ClassInfo clazz, IndexView index, String packageName,
                                            String messageComment, String fieldComment) {

        if (isHidden(clazz)) {
            // since class is marked as hidden skip processing of that class
            return null;
        }

        String name = clazz.simpleName();
        String altName = getReferenceOfModel(clazz, "name");
        if (altName != null) {

            name = altName;
        }
        ProtoMessage message = new ProtoMessage(name, packageName == null ? clazz.name().prefix().toString() : packageName);
        for (FieldInfo pd : clazz.fields()) {
            String completeFieldComment = fieldComment;
            // ignore static and/or transient fields
            if (Modifier.isStatic(pd.flags()) || Modifier.isTransient(pd.flags())) {
                continue;
            }

            AnnotationInstance variableInfo = pd.annotation(variableInfoAnnotation);

            if (variableInfo != null) {
                completeFieldComment = fieldComment + "\n @VariableInfo(tags=\"" + variableInfo.value("tags").asString()
                        + "\")";
            }

            String fieldTypeString = pd.type().name().toString();

            DotName fieldType = pd.type().name();
            String protoType;
            if (pd.type().kind() == Kind.PARAMETERIZED_TYPE) {
                fieldTypeString = "Collection";

                List<Type> typeParameters = pd.type().asParameterizedType().arguments();
                if (typeParameters.isEmpty()) {
                    throw new IllegalArgumentException("Field " + pd.name() + " of class " + clazz.name().toString()
                                                               + " uses collection without type information");
                }
                fieldType = typeParameters.get(0).name();
                protoType = protoType(fieldType.toString());
            } else {
                protoType = protoType(fieldTypeString);
            }

            if (protoType == null) {
                ClassInfo classInfo = index.getClassByName(fieldType);
                if (classInfo == null) {
                    throw new IllegalStateException("Cannot find class info in jandex index for " + fieldType);
                }
                if (!isHidden(classInfo)) {
                    if (classInfo.superName() != null && Enum.class.getName().equals(classInfo.superName().toString())) {
                        ProtoEnum another = enumFromClass(proto, classInfo, packageName);
                        protoType = another.getName();
                    } else {
                        ProtoMessage another = messageFromClass(proto, classInfo, index, packageName,
                                                                messageComment, fieldComment);
                        protoType = another.getName();
                    }
                }
            }

            message.addField(applicabilityByType(fieldTypeString), protoType, pd.name()).setComment(completeFieldComment);
        }
        message.setComment(messageComment);
        proto.addMessage(message);
        return message;
    }

    protected ProtoEnum enumFromClass(Proto proto, ClassInfo clazz, String packageName) {
        String name = clazz.simpleName();
        String altName = getReferenceOfModel(clazz, "name");
        if (altName != null) {
            name = altName;
        }

        ProtoEnum modelEnum = new ProtoEnum(name, packageName == null ? clazz.name().prefix().toString() : packageName);
        clazz.fields().stream()
                .filter(f -> !f.name().startsWith("$"))
                .forEach(f -> addEnumField(f, modelEnum));
        proto.addEnum(modelEnum);
        return modelEnum;
    }

    private void addEnumField(FieldInfo field, ProtoEnum pEnum) {
        AnnotationInstance annotation = field.annotation(ENUM_VALUE_ANNOTATION);
        Integer ordinal = null;
        if (annotation != null) {
            AnnotationValue number = annotation.value("number");
            if (number != null) {
                ordinal = number.asInt();
            }
        }
        if (ordinal == null) {
            ordinal = pEnum.getFields()
                    .values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(-1) + 1;
        }
        pEnum.addField(field.name(), ordinal);
    }

    @Override
    public Collection<ClassInfo> extractDataClasses(Collection<ClassInfo> input, String targetDirectory) {
        Set<ClassInfo> dataModelClasses = new HashSet<>();
        try {
            for (ClassInfo modelClazz : input) {

                for (FieldInfo pd : modelClazz.fields()) {

                    if (pd.type().name().toString().startsWith("java.lang")
                            || pd.type().name().toString().equals(Date.class.getCanonicalName())) {
                        continue;
                    }

                    dataModelClasses.add(index.getClassByName(pd.type().name()));
                }

                generateModelClassProto(modelClazz, targetDirectory);
            }

            this.generateProtoListing(targetDirectory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return dataModelClasses;
    }

    protected void generateModelClassProto(ClassInfo modelClazz, String targetDirectory) throws Exception {

        String processId = getReferenceOfModel(modelClazz, "reference");
        String name = getReferenceOfModel(modelClazz, "name");

        if (processId != null) {

            Proto modelProto = generate("@Indexed",
                                        INDEX_COMMENT,
                                        modelClazz.name().prefix().toString() + "." + processId, modelClazz,
                                        "import \"kogito-index.proto\";",
                                        "import \"kogito-types.proto\";",
                                        "option kogito_model = \"" + name + "\";",
                                        "option kogito_id = \"" + processId + "\";");

            if (modelProto.getMessages().isEmpty()) {
                // no messages, nothing to do
                return;
            }

            ProtoMessage modelMessage = modelProto.getMessages().stream().filter(msg -> msg.getName().equals(name)).findFirst()
                    .orElseThrow(() -> new IllegalStateException("Unable to find model message"));
            modelMessage.addField("optional", "org.kie.kogito.index.model.KogitoMetadata", "metadata")
                    .setComment(INDEX_COMMENT);

            this.writeFilesToFS(processId, targetDirectory, modelProto);
        }
    }

    protected String getReferenceOfModel(ClassInfo modelClazz, String name) {
        AnnotationInstance generatedData = modelClazz.classAnnotation(generatedAnnotation);

        if (generatedData != null) {

            return generatedData.value(name).asString();
        }

        return null;
    }

    protected boolean isHidden(ClassInfo modelClazz) {
        AnnotationInstance generatedData = modelClazz.classAnnotation(generatedAnnotation);

        if (generatedData != null) {

            return generatedData.value("hidden").asBoolean();
        }

        return false;
    }
}
