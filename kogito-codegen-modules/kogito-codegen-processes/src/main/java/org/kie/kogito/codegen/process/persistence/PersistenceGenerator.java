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
package org.kie.kogito.codegen.process.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.infinispan.protostream.FileDescriptorSource;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.AbstractGenerator;
import org.kie.kogito.codegen.process.persistence.marshaller.MarshallerGenerator;
import org.kie.kogito.codegen.process.persistence.proto.Proto;
import org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class PersistenceGenerator extends AbstractGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceGenerator.class);

    /**
     * Type of persistence
     */
    public static final String FILESYSTEM_PERSISTENCE_TYPE = "filesystem";
    public static final String INFINISPAN_PERSISTENCE_TYPE = "infinispan";
    public static final String MONGODB_PERSISTENCE_TYPE = "mongodb";
    public static final String POSTGRESQL_PERSISTENCE_TYPE = "postgresql";
    public static final String KAFKA_PERSISTENCE_TYPE = "kafka";
    public static final String JDBC_PERSISTENCE_TYPE = "jdbc";
    public static final String DEFAULT_PERSISTENCE_TYPE = INFINISPAN_PERSISTENCE_TYPE;

    /**
     * Kogito persistence properties
     */
    // Generic
    /**
     * (boolean) enable/disable proto generation for DATA-INDEX; default to true
     */
    public static final String KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION = "kogito.persistence.data-index.proto.generation";
    public static final String KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION_DEFAULT = "true";
    /**
     * (boolean) enable/disable proto marshaller generation; default to true
     */
    public static final String KOGITO_PERSISTENCE_PROTO_MARSHALLER = "kogito.persistence.proto.marshaller";
    public static final String KOGITO_PERSISTENCE_PROTO_MARSHALLER_DEFAULT = "true";
    /**
     * (string) kind of persistence used; possible values: filesystem, infinispan, mongodb, postgresql, kafka, jdbc; default to infinispan
     */
    public static final String KOGITO_PERSISTENCE_TYPE = "kogito.persistence.type";

    /**
     * Constants used during codegen
     */
    protected static final String KOGITO_PROCESS_INSTANCE_PACKAGE = "org.kie.kogito.persistence";
    protected static final String JAVA = ".java";

    /**
     * Generic PersistenceGenerator constants
     */
    public static final String GENERATOR_NAME = "persistence";
    protected static final String CLASS_TEMPLATES_PERSISTENCE = "/class-templates/persistence/";
    private final ProtoGenerator protoGenerator;
    private final MarshallerGenerator marshallerGenerator;

    public PersistenceGenerator(KogitoBuildContext context, ProtoGenerator protoGenerator, MarshallerGenerator marshallerGenerator) {
        super(context, GENERATOR_NAME);
        this.protoGenerator = protoGenerator;
        this.marshallerGenerator = marshallerGenerator;
    }

    @Override
    public Optional<ApplicationSection> section() {
        return Optional.empty();
    }

    @Override
    protected Collection<GeneratedFile> internalGenerate() {
        switch (persistenceType()) {
            case INFINISPAN_PERSISTENCE_TYPE:
            case FILESYSTEM_PERSISTENCE_TYPE:
            case MONGODB_PERSISTENCE_TYPE:
            case JDBC_PERSISTENCE_TYPE:
            case KAFKA_PERSISTENCE_TYPE:
            case POSTGRESQL_PERSISTENCE_TYPE:
                return generateFiles();
            default:
                throw new IllegalArgumentException("Unknown persistenceType " + persistenceType());
        }
    }

    @Override
    public boolean isEmpty() {
        // PersistenceGenerator is a different type of generator without specific resources
        return !context().getAddonsConfig().usePersistence();
    }

    public String persistenceType() {
        return context().getApplicationProperty(KOGITO_PERSISTENCE_TYPE).orElse(PersistenceGenerator.DEFAULT_PERSISTENCE_TYPE);
    }

    protected Collection<GeneratedFile> generateFiles() {
        Collection<GeneratedFile> toReturn = new ArrayList<>();
        toReturn.addAll(generateProtoMarshaller());
        toReturn.addAll(generateProtoForDataIndex());
        return toReturn;
    }

    protected Collection<GeneratedFile> generateProtoMarshaller() {
        if (!hasProtoMarshaller(context())) {
            // TODO implement a validation check to verify that data classes implement Serializable
            LOGGER.debug("Proto marshaller generation is skipped because " + KOGITO_PERSISTENCE_PROTO_MARSHALLER + "=false");
            return Collections.emptyList();
        }
        Proto proto = protoGenerator.protoOfDataClasses(context().getPackageName(), "import \"kogito-types.proto\";");

        List<String> variableMarshallers = new ArrayList<>();

        String protoContent = proto.serialize();

        List<CompilationUnit> marshallers;
        try {
            marshallers = marshallerGenerator.generate(protoContent);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible to obtain marshaller CompilationUnits", e);
        }

        Collection<GeneratedFile> protoFiles = new ArrayList<>();
        try {
            String typesURI = "META-INF/kogito-types.proto";
            protoFiles.add(new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE,
                    typesURI,
                    new String(context().getClassLoader().getResourceAsStream(typesURI).readAllBytes(), StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot find kogito types protobuf!", e);
        }
        // generate proto files leads to problems as it has a reverse dependency of kogito-index
        String typesURI = "META-INF/application-types.proto";
        protoFiles.add(new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE,
                typesURI,
                protoContent));

        Collection<GeneratedFile> generatedFiles = new ArrayList<>(protoFiles);

        if (!marshallers.isEmpty()) {

            List<CompilationUnit> files = new ArrayList<>(marshallers);

            variableMarshallers.add("org.jbpm.flow.serialization.marshaller.StringProtostreamBaseMarshaller");
            variableMarshallers.add("org.jbpm.flow.serialization.marshaller.BooleanProtostreamBaseMarshaller");
            variableMarshallers.add("org.jbpm.flow.serialization.marshaller.DateProtostreamBaseMarshaller");
            variableMarshallers.add("org.jbpm.flow.serialization.marshaller.DoubleProtostreamBaseMarshaller");
            variableMarshallers.add("org.jbpm.flow.serialization.marshaller.FloatProtostreamBaseMarshaller");
            variableMarshallers.add("org.jbpm.flow.serialization.marshaller.IntegerProtostreamBaseMarshaller");
            variableMarshallers.add("org.jbpm.flow.serialization.marshaller.LongProtostreamBaseMarshaller");
            variableMarshallers.add("org.jbpm.flow.serialization.marshaller.InstantProtostreamBaseMarshaller");
            variableMarshallers.add("org.jbpm.flow.serialization.marshaller.SerializableProtostreamBaseMarshaller");

            for (CompilationUnit unit : files) {
                String packageName = unit.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
                Optional<ClassOrInterfaceDeclaration> clazz = unit.findFirst(ClassOrInterfaceDeclaration.class);
                clazz.ifPresent(c -> {
                    String clazzName = packageName + "." + c.getName().toString();
                    variableMarshallers.add(clazzName);
                    generatedFiles.add(new GeneratedFile(GeneratedFileType.SOURCE,
                            clazzName.replace('.', '/') + JAVA,
                            unit.toString()));
                });
            }

            // we build the marshaller for protostream
            TemplatedGenerator generatorProtostreamSerialization = TemplatedGenerator.builder().withTemplateBasePath(CLASS_TEMPLATES_PERSISTENCE)
                    .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                    .withPackageName(KOGITO_PROCESS_INSTANCE_PACKAGE)
                    .build(context(), "ProtostreamObjectMarshaller");
            CompilationUnit parsedClazzFile = generatorProtostreamSerialization.compilationUnitOrThrow();
            String packageName = parsedClazzFile.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
            ClassOrInterfaceDeclaration clazz = parsedClazzFile.findFirst(ClassOrInterfaceDeclaration.class)
                    .orElseThrow(() -> new InvalidTemplateException(generatorProtostreamSerialization, "Failed to find template for ProtostreamObjectMarshaller"));

            ConstructorDeclaration constructor = clazz.getDefaultConstructor()
                    .orElseThrow(() -> new InvalidTemplateException(generatorProtostreamSerialization, "Failed to find default constructor in template for ProtostreamObjectMarshaller"));

            // register protofiles and marshallers
            BlockStmt body = new BlockStmt();
            Expression newFileDescriptorSource = new ObjectCreationExpr(null, new ClassOrInterfaceType(null, FileDescriptorSource.class.getCanonicalName()), NodeList.nodeList());
            Expression getClassLoader = new MethodCallExpr(new MethodCallExpr(null, "getClass", NodeList.nodeList()), "getClassLoader", NodeList.nodeList());

            Expression chainExpression = newFileDescriptorSource;
            for (GeneratedFile generatedFile : protoFiles) {
                String path = generatedFile.relativePath();
                String name = generatedFile.path().getFileName().toString();
                if (!name.endsWith(".proto")) {
                    continue;
                }
                Expression getISKogito = new MethodCallExpr(getClassLoader, "getResourceAsStream", NodeList.nodeList(new StringLiteralExpr(path)));
                chainExpression =
                        new MethodCallExpr(new EnclosedExpr(chainExpression), "addProtoFile",
                                NodeList.nodeList(new StringLiteralExpr(name), getISKogito));
            }

            body.addStatement(new MethodCallExpr(new NameExpr("context"), "registerProtoFiles", NodeList.nodeList(chainExpression)));
            for (String baseMarshallers : variableMarshallers) {
                Expression newMarshallerExpr = new ObjectCreationExpr(null, new ClassOrInterfaceType(null, baseMarshallers), NodeList.nodeList());
                body.addStatement(new MethodCallExpr(new NameExpr("context"), "registerMarshaller", NodeList.nodeList(newMarshallerExpr)));
            }
            CatchClause catchClause = new CatchClause(new Parameter().setType(IOException.class).setName("e"), new BlockStmt(NodeList.nodeList(new ThrowStmt(new ObjectCreationExpr(
                    null, StaticJavaParser.parseClassOrInterfaceType("java.io.UncheckedIOException"),
                    NodeList.nodeList(new NameExpr("e")))))));
            TryStmt tryStmt = new TryStmt(body, NodeList.nodeList(catchClause), null);
            constructor.getBody().addStatement(tryStmt);
            String fqnProtoStreamMarshaller = packageName + "." + clazz.getName().toString();
            generatedFiles.add(new GeneratedFile(GeneratedFileType.SOURCE,
                    fqnProtoStreamMarshaller.replace('.', '/') + JAVA,
                    parsedClazzFile.toString()));

            String objectMarshallerStrategyServiceDescriptor = "";
            try {
                //try to find an existing ObjectMarshallerStrategy descriptor in the classpath to be appended to the ProtoStream generated one
                objectMarshallerStrategyServiceDescriptor =
                        new String(getClass().getResourceAsStream("/META-INF/services/org.jbpm.flow.serialization.ObjectMarshallerStrategy").readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                LOGGER.warn("No existing ObjectMarshallerStrategy found the the classpath to be included with the ProtoS generated one for SPI.");
            }
            objectMarshallerStrategyServiceDescriptor += "\n" + fqnProtoStreamMarshaller + "\n";

            generatedFiles.add(new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE,
                    "META-INF/services/org.jbpm.flow.serialization.ObjectMarshallerStrategy",
                    objectMarshallerStrategyServiceDescriptor));
        }
        return generatedFiles;
    }

    protected Collection<GeneratedFile> generateProtoForDataIndex() {
        if (!hasDataIndexProto(context())) {
            LOGGER.debug("Proto generation for data-index is skipped because " + KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION + "=false");
            return Collections.emptyList();
        }
        return protoGenerator.generateProtoFiles();
    }

    public static boolean hasProtoMarshaller(KogitoBuildContext context) {
        return "true".equalsIgnoreCase(context.getApplicationProperty(KOGITO_PERSISTENCE_PROTO_MARSHALLER)
                .orElse(KOGITO_PERSISTENCE_PROTO_MARSHALLER_DEFAULT));
    }

    public static boolean hasDataIndexProto(KogitoBuildContext context) {
        return "true".equalsIgnoreCase(context.getApplicationProperty(KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION)
                .orElse(KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION_DEFAULT));
    }

}
