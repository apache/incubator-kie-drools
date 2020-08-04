/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.process.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.config.Configuration;
import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.infinispan.protostream.descriptors.Option;
import org.infinispan.protostream.impl.SerializationContextImpl;
import org.kie.kogito.codegen.BodyDeclarationComparator;

import static com.github.javaparser.StaticJavaParser.parse;

public class MarshallerGenerator {

    private ClassLoader classLoader;

    public MarshallerGenerator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public List<CompilationUnit> generateFromClasspath(String protoFile) throws IOException {
        FileDescriptorSource proto = FileDescriptorSource.fromResources(protoFile);

        return generate(proto);
    }

    public List<CompilationUnit> generate(String content) throws IOException {
        FileDescriptorSource proto = FileDescriptorSource.fromString(UUID.randomUUID().toString(), content);

        return generate(proto);
    }

    public List<CompilationUnit> generate(FileDescriptorSource proto) throws IOException {
        List<CompilationUnit> units = new ArrayList<>();
        CompilationUnit parsedClazzFile = parse(this.getClass().getResourceAsStream("/class-templates/persistence/MessageMarshallerTemplate.java"));

        SerializationContext serializationContext = new SerializationContextImpl(Configuration.builder().build());
        serializationContext.registerProtoFiles(FileDescriptorSource.fromResources(classLoader, "kogito-types.proto"));
        serializationContext.registerProtoFiles(proto);

        Map<String, FileDescriptor> descriptors = serializationContext.getFileDescriptors();

        for (Entry<String, FileDescriptor> entry : descriptors.entrySet()) {

            FileDescriptor d = entry.getValue();

            if (d.getPackage().equals("kogito")) {
                continue;
            }

            List<Descriptor> messages = d.getMessageTypes();

            for (Descriptor msg : messages) {

                CompilationUnit clazzFile = parsedClazzFile.clone();
                units.add(clazzFile);

                String javaType = packageFromOption(d, msg) + "." + msg.getName();

                clazzFile.setPackageDeclaration(d.getPackage());
                ClassOrInterfaceDeclaration clazz = clazzFile.findFirst(ClassOrInterfaceDeclaration.class, sl -> true).orElseThrow(() -> new RuntimeException("No class found"));
                clazz.setName(msg.getName() + "MessageMarshaller");
                clazz.getImplementedTypes(0).setTypeArguments(NodeList.nodeList(new ClassOrInterfaceType(null, javaType)));

                MethodDeclaration getJavaClassMethod = clazz.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("getJavaClass")).orElseThrow(() -> new RuntimeException("No getJavaClass method found"));
                getJavaClassMethod.setType(new ClassOrInterfaceType(null, new SimpleName(Class.class.getName()), NodeList.nodeList(new ClassOrInterfaceType(null, javaType))));
                BlockStmt getJavaClassMethodBody = new BlockStmt();
                getJavaClassMethodBody.addStatement(new ReturnStmt(new NameExpr(javaType + ".class")));
                getJavaClassMethod.setBody(getJavaClassMethodBody);

                MethodDeclaration getTypeNameMethod = clazz.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("getTypeName")).orElseThrow(() -> new RuntimeException("No getTypeName method found"));
                BlockStmt getTypeNameMethodBody = new BlockStmt();
                getTypeNameMethodBody.addStatement(new ReturnStmt(new StringLiteralExpr(msg.getFullName())));
                getTypeNameMethod.setBody(getTypeNameMethodBody);

                MethodDeclaration readFromMethod = clazz.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("readFrom")).orElseThrow(() -> new RuntimeException("No readFrom method found"));
                readFromMethod.setType(javaType);
                readFromMethod.setBody(new BlockStmt());

                MethodDeclaration writeToMethod = clazz.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("writeTo")).orElseThrow(() -> new RuntimeException("No writeTo method found"));
                writeToMethod.getParameter(1).setType(javaType);
                writeToMethod.setBody(new BlockStmt());

                ClassOrInterfaceType classType = new ClassOrInterfaceType(null, javaType);

                // read method
                VariableDeclarationExpr instance = new VariableDeclarationExpr(new VariableDeclarator(classType, "value", new ObjectCreationExpr(null, classType, NodeList.nodeList())));
                readFromMethod.getBody().ifPresent(b -> b.addStatement(instance));

                for (FieldDescriptor field : msg.getFields()) {

                    String protoStreamMethodType = protoStreamMethodType(field.getTypeName());
                    MethodCallExpr write = null;
                    MethodCallExpr read = null;
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

                            read = new MethodCallExpr(new NameExpr("reader"), "readCollection")
                                    .addArgument(new StringLiteralExpr(field.getName()))
                                    .addArgument(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ArrayList.class.getCanonicalName()), NodeList.nodeList()))
                                    .addArgument(new NameExpr(customTypeName + ".class"));
                            write = new MethodCallExpr(new NameExpr("writer"), "writeCollection")
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
        }

        return units;
    }

    protected String packageNameForMessage(FileDescriptor d, String messageName) {

        List<Descriptor> messages = d.getMessageTypes();

        for (Descriptor msg : messages) {
            if (messageName.equals(msg.getName())) {

                return packageFromOption(d, msg) + "." + messageName;
            }
        }

        throw new IllegalArgumentException("No message found with name" + messageName);
    }

    protected String packageFromOption(FileDescriptor d, Descriptor msg) {
        Option customPackage = msg.getOption("java_package");

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
}
