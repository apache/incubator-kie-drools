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
package org.kie.kogito.codegen.process.persistence.marshaller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.drools.util.StringUtils;
import org.infinispan.protostream.EnumMarshaller;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.EnumDescriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.descriptors.Option;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.BodyDeclarationComparator;
import org.kie.kogito.codegen.process.persistence.ExclusionTypeUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.EQUALS;
import static java.lang.String.format;
import static org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator.KOGITO_JAVA_CLASS_OPTION;

public abstract class AbstractMarshallerGenerator<T> implements MarshallerGenerator {

    public static final String TEMPLATE_PERSISTENCE_FOLDER = "/class-templates/persistence/";
    private static final String JAVA_PACKAGE_OPTION = "java_package";
    private static final String STATE_PARAM = "state";

    private final KogitoBuildContext context;
    protected final Collection<T> modelClasses;

    public AbstractMarshallerGenerator(KogitoBuildContext context, Collection<T> rawDataClasses) {
        this.context = context;
        this.modelClasses = rawDataClasses == null ? Collections.emptyList() : rawDataClasses;
    }

    @Override
    public List<CompilationUnit> generate(String content) throws IOException {
        FileDescriptorSource proto = FileDescriptorSource.fromString(UUID.randomUUID().toString(), content);
        return generate(proto);
    }

    public List<CompilationUnit> generate(FileDescriptorSource proto) throws IOException {
        List<CompilationUnit> units = new ArrayList<>();
        TemplatedGenerator generator = TemplatedGenerator.builder()
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .withTemplateBasePath(TEMPLATE_PERSISTENCE_FOLDER)
                .build(context, "MessageMarshaller");

        Predicate<String> typeExclusions = ExclusionTypeUtils.createTypeExclusions();

        // filter types that don't require to create a marshaller
        Predicate<Descriptor> packagePredicate = (msg) -> !msg.getFileDescriptor().getPackage().equals("kogito");
        Predicate<Descriptor> jacksonPredicate = (msg) -> !typeExclusions.test(packageFromOption(msg.getFileDescriptor(), msg) + "." + msg.getName());

        Predicate<Descriptor> predicate = packagePredicate.and(jacksonPredicate);

        CompilationUnit parsedClazzFile = generator.compilationUnitOrThrow();

        SerializationContext serializationContext = new SerializationContextImpl(Configuration.builder().build());
        FileDescriptorSource kogitoTypesDescriptor = new FileDescriptorSource().addProtoFile("kogito-types.proto", context.getClassLoader().getResourceAsStream("META-INF/kogito-types.proto"));
        serializationContext.registerProtoFiles(kogitoTypesDescriptor);
        serializationContext.registerProtoFiles(proto);

        Map<String, FileDescriptor> descriptors = serializationContext.getFileDescriptors();

        for (Entry<String, FileDescriptor> entry : descriptors.entrySet()) {

            FileDescriptor d = entry.getValue();
            List<Descriptor> messages = d.getMessageTypes().stream().filter(predicate).collect(Collectors.toList());

            for (Descriptor msg : messages) {

                CompilationUnit clazzFile = parsedClazzFile.clone();
                units.add(clazzFile);

                String javaType = packageFromOption(d, msg) + "." + msg.getName();

                clazzFile.setPackageDeclaration(d.getPackage());
                ClassOrInterfaceDeclaration clazz = clazzFile.findFirst(ClassOrInterfaceDeclaration.class, sl -> true)
                        .orElseThrow(() -> new InvalidTemplateException(generator, "No class found"));
                clazz.setName(msg.getName() + "MessageMarshaller");
                clazz.getImplementedTypes(0).setTypeArguments(NodeList.nodeList(new ClassOrInterfaceType(null, javaType)));

                MethodDeclaration getJavaClassMethod =
                        clazz.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("getJavaClass"))
                                .orElseThrow(() -> new InvalidTemplateException(generator, "No getJavaClass method found"));
                getJavaClassMethod.setType(new ClassOrInterfaceType(null, new SimpleName(Class.class.getName()), NodeList.nodeList(new ClassOrInterfaceType(null, javaType))));
                BlockStmt getJavaClassMethodBody = new BlockStmt();
                getJavaClassMethodBody.addStatement(new ReturnStmt(new NameExpr(javaType + ".class")));
                getJavaClassMethod.setBody(getJavaClassMethodBody);

                MethodDeclaration getTypeNameMethod =
                        clazz.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("getTypeName"))
                                .orElseThrow(() -> new InvalidTemplateException(generator, "No getTypeName method found"));
                BlockStmt getTypeNameMethodBody = new BlockStmt();
                getTypeNameMethodBody.addStatement(new ReturnStmt(new StringLiteralExpr(msg.getFullName())));
                getTypeNameMethod.setBody(getTypeNameMethodBody);

                MethodDeclaration readFromMethod =
                        clazz.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("readFrom"))
                                .orElseThrow(() -> new InvalidTemplateException(generator, "No readFrom method found"));
                readFromMethod.setType(javaType);
                readFromMethod.setBody(new BlockStmt());

                MethodDeclaration writeToMethod =
                        clazz.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("writeTo"))
                                .orElseThrow(() -> new InvalidTemplateException(generator, "No writeTo method found"));
                writeToMethod.getParameter(1).setType(javaType);
                writeToMethod.setBody(new BlockStmt());

                ClassOrInterfaceType classType = new ClassOrInterfaceType(null, javaType);

                // read method
                VariableDeclarationExpr instance = new VariableDeclarationExpr(new VariableDeclarator(classType, "value", new ObjectCreationExpr(null, classType, NodeList.nodeList())));
                readFromMethod.getBody().ifPresent(b -> b.addStatement(instance));

                for (FieldDescriptor field : msg.getFields()) {

                    String protoStreamMethodType = protoStreamMethodType(field.getTypeName());
                    Expression write = null;
                    Expression read = null;
                    if (protoStreamMethodType != null && !field.isRepeated()) {
                        // has a mapped type
                        read = new MethodCallExpr(new NameExpr("reader"), "read" + protoStreamMethodType)
                                .addArgument(new StringLiteralExpr(field.getName()));
                        String accessor = protoStreamMethodType.equals("Boolean") ? "is" : "get";
                        write = new MethodCallExpr(new NameExpr("writer"), "write" + protoStreamMethodType)
                                .addArgument(new StringLiteralExpr(field.getName()))
                                .addArgument(new MethodCallExpr(new NameExpr("t"), accessor + StringUtils.ucFirst(field.getName())));
                    } else {
                        // custom types 
                        String customTypeName = javaTypeForMessage(d, field.getTypeName(), serializationContext);

                        if (field.isRepeated()) {
                            if (null == customTypeName || customTypeName.isEmpty()) {
                                customTypeName = primaryTypeClassName(field.getTypeName());
                            }

                            String writeMethod;

                            if (isArray(javaType, field)) {
                                writeMethod = "writeArray";
                                read = new MethodCallExpr(new NameExpr("reader"), "readArray")
                                        .addArgument(new StringLiteralExpr(field.getName()))
                                        .addArgument(new NameExpr(customTypeName + ".class"));
                            } else {
                                writeMethod = "writeCollection";
                                read = new MethodCallExpr(new NameExpr("reader"), "readCollection")
                                        .addArgument(new StringLiteralExpr(field.getName()))
                                        .addArgument(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ArrayList.class.getCanonicalName()), NodeList.nodeList()))
                                        .addArgument(new NameExpr(customTypeName + ".class"));
                            }

                            write = new MethodCallExpr(new NameExpr("writer"), writeMethod)
                                    .addArgument(new StringLiteralExpr(field.getName()))
                                    .addArgument(new MethodCallExpr(new NameExpr("t"), "get" + StringUtils.ucFirst(field.getName())))
                                    .addArgument(new NameExpr(customTypeName + ".class"));
                        } else {

                            read = new MethodCallExpr(new NameExpr("reader"), "readObject")
                                    .addArgument(new StringLiteralExpr(field.getName()))
                                    .addArgument(new NameExpr(customTypeName + ".class"));
                            write = new MethodCallExpr(new NameExpr("writer"), "writeObject")
                                    .addArgument(new StringLiteralExpr(field.getName()))
                                    .addArgument(new MethodCallExpr(new NameExpr("t"), "get" + StringUtils.ucFirst(field.getName())))
                                    .addArgument(new NameExpr(customTypeName + ".class"));
                        }

                        if (customTypeName.equals(Serializable.class.getName())) {
                            String fieldClazz = (String) field.getOptionByName(KOGITO_JAVA_CLASS_OPTION).getValue();
                            if (fieldClazz == null) {
                                throw new IllegalArgumentException(format("Serializable proto field '%s' is missing value for option %s", field.getName(), KOGITO_JAVA_CLASS_OPTION));
                            } else {
                                read = new CastExpr().setExpression(new EnclosedExpr(read)).setType(fieldClazz);
                                int argumentIndex = 1;
                                MethodCallExpr writeMethod = null;
                                if (write instanceof MethodCallExpr &&
                                        (writeMethod = (MethodCallExpr) write).getArguments() != null &&
                                        writeMethod.getArguments().size() > argumentIndex) {
                                    Expression argument = writeMethod.getArgument(argumentIndex);
                                    write = writeMethod.setArgument(
                                            argumentIndex,
                                            new CastExpr().setExpression(new EnclosedExpr(argument)).setType(fieldClazz));
                                }
                            }
                        }
                    }

                    MethodCallExpr setter = new MethodCallExpr(new NameExpr("value"), "set" + StringUtils.ucFirst(field.getName())).addArgument(read);
                    readFromMethod.getBody().ifPresent(b -> b.addStatement(setter));

                    // write method
                    writeToMethod
                            .getBody()
                            .orElseThrow(() -> new NoSuchElementException("A method declaration doesn't contain a body!"))
                            .addStatement(write);
                }

                readFromMethod.getBody().ifPresent(b -> b.addStatement(new ReturnStmt(new NameExpr("value"))));
                clazz.getMembers().sort(new BodyDeclarationComparator());
            }

            for (EnumDescriptor msg : d.getEnumTypes()) {
                CompilationUnit compilationUnit = new CompilationUnit();
                units.add(compilationUnit);

                String javaType = packageFromOption(d, msg) + "." + msg.getName();

                ClassOrInterfaceDeclaration classDeclaration = compilationUnit.setPackageDeclaration(d.getPackage())
                        .addClass(msg.getName() + "EnumMarshaller").setPublic(true);
                classDeclaration.addImplementedType(EnumMarshaller.class).getImplementedTypes(0)
                        .setTypeArguments(NodeList.nodeList(new ClassOrInterfaceType(null, javaType)));
                classDeclaration.addMethod("getTypeName", PUBLIC)
                        .setType(String.class)
                        .setBody(new BlockStmt().addStatement(new ReturnStmt(new StringLiteralExpr(msg.getFullName()))));
                classDeclaration.addMethod("getJavaClass", PUBLIC)
                        .setType(new ClassOrInterfaceType(null, new SimpleName(Class.class.getName()), NodeList.nodeList(new ClassOrInterfaceType(null, javaType))))
                        .setBody(new BlockStmt().addStatement(new ReturnStmt(new ClassExpr(new ClassOrInterfaceType(null, javaType)))));

                BlockStmt encodeBlock = new BlockStmt()
                        .addStatement(
                                new IfStmt(
                                        new BinaryExpr(new NullLiteralExpr(), new NameExpr(STATE_PARAM), EQUALS),
                                        new ThrowStmt(new ObjectCreationExpr(
                                                null,
                                                new ClassOrInterfaceType(null, IllegalArgumentException.class.getName()),
                                                NodeList.nodeList(new StringLiteralExpr("Invalid value provided to enum")))),
                                        null))
                        .addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(STATE_PARAM), "ordinal")));
                classDeclaration.addMethod("encode", PUBLIC)
                        .setType("int")
                        .addParameter(javaType, STATE_PARAM)
                        .setBody(encodeBlock);

                MethodDeclaration decode = classDeclaration.addMethod("decode", PUBLIC)
                        .setType(javaType)
                        .addParameter("int", "value");
                SwitchStmt decodeSwitch = new SwitchStmt().setSelector(new NameExpr("value"));
                msg.getValues().forEach(v -> {
                    SwitchEntry dEntry = new SwitchEntry();
                    dEntry.getLabels().add(new IntegerLiteralExpr(v.getNumber()));
                    dEntry.addStatement(new ReturnStmt(new NameExpr(javaType + "." + v.getName())));
                    decodeSwitch.getEntries().add(dEntry);
                });
                decodeSwitch.getEntries()
                        .add(new SwitchEntry().addStatement(
                                new ThrowStmt(new ObjectCreationExpr(
                                        null,
                                        new ClassOrInterfaceType(null, IllegalArgumentException.class.getName()),
                                        NodeList.nodeList(new StringLiteralExpr("Invalid value provided to enum"))))));
                decode.setBody(new BlockStmt().addStatement(decodeSwitch));
            }
        }

        return units;
    }

    protected String packageFromOption(FileDescriptor d, Descriptor msg) {
        Option option = msg.getOptions().stream()
                .filter(o -> JAVA_PACKAGE_OPTION.equals(o.getName()))
                .findAny()
                .orElse(null);
        return packageFromOption(d, option);
    }

    protected String packageFromOption(FileDescriptor d, EnumDescriptor msg) {
        return packageFromOption(d, msg.getOption(JAVA_PACKAGE_OPTION));
    }

    private String packageFromOption(FileDescriptor d, Option customPackage) {
        return (customPackage == null ? d.getPackage() : customPackage.getValue().toString());
    }

    protected String javaTypeForMessage(FileDescriptor d, String messageName, SerializationContext serializationContext) {
        Map<String, FileDescriptor> descriptors = serializationContext.getFileDescriptors();
        for (Entry<String, FileDescriptor> entry : descriptors.entrySet()) {

            List<Descriptor> messages = entry.getValue().getMessageTypes();

            for (Descriptor msg : messages) {
                if (messageName.equals(msg.getName())) {
                    return packageFromOption(d, msg) + "." + messageName;
                } else if (messageName.equals(msg.getFullName())) {
                    return packageFromOption(d, msg) + "." + msg.getName();
                }
            }
            List<EnumDescriptor> enums = entry.getValue().getEnumTypes();
            for (EnumDescriptor msg : enums) {
                if (messageName.equals(msg.getName())) {
                    return packageFromOption(d, msg) + "." + messageName;
                } else if (messageName.equals(msg.getFullName())) {
                    return packageFromOption(d, msg) + "." + msg.getName();
                }
            }
        }
        return null;
    }

    protected String protoStreamMethodType(String type) {
        String methodReader = null;

        switch (type) {
            case "string":
                methodReader = "String";
                break;
            case "int32":
                methodReader = "Int";
                break;
            case "int64":
                methodReader = "Long";
                break;
            case "double":
                methodReader = "Double";
                break;
            case "float":
                methodReader = "Float";
                break;
            case "bool":
                methodReader = "Boolean";
                break;
            case "bytes":
                methodReader = "Bytes";
                break;
            default:
                methodReader = null;
        }

        return methodReader;
    }

    protected String primaryTypeClassName(String type) {
        String className = null;

        switch (type) {
            case "string":
                className = "String";
                break;
            case "int32":
                className = "Integer";
                break;
            case "int64":
                className = "Long";
                break;
            case "double":
                className = "Double";
                break;
            case "float":
                className = "Float";
                break;
            case "bool":
                className = "Boolean";
                break;
            default:
                className = null;
        }

        return className;
    }

    protected abstract boolean isArray(String javaType, FieldDescriptor field);
}
