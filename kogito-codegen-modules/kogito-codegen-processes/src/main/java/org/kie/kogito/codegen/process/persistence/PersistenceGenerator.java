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
package org.kie.kogito.codegen.process.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.infinispan.protostream.FileDescriptorSource;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.AbstractGenerator;
import org.kie.kogito.codegen.core.BodyDeclarationComparator;
import org.kie.kogito.codegen.process.persistence.proto.Proto;
import org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
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
    public static final String KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION = "kogito.persistence.data-index.proto.generation";
    public static final String KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION_DEFAULT = "true";
    public static final String KOGITO_PERSISTENCE_PROTO_MARSHALLER = "kogito.persistence.proto.marshaller";
    public static final String KOGITO_PERSISTENCE_PROTO_MARSHALLER_DEFAULT = "true";
    public static final String KOGITO_PERSISTENCE_TYPE = "kogito.persistence.type";
    public static final String KOGITO_PERSISTENCE_OPTIMISTIC_LOCK = "kogito.persistence.optimistic.lock";
    public static final String KOGITO_PERSISTENCE_AUTO_DDL = "kogito.persistence.auto.ddl";
    // PostgreSQL specific
    public static final String KOGITO_PERSISTENCE_QUERY_TIMEOUT = "kogito.persistence.query.timeout.millis";
    public static final String KOGITO_PERSISTENCE_POSTGRESQL_CONNECTION_URI = "kogito.persistence.postgresql.connection.uri";
    // MongoDB specific
    public static final String QUARKUS_MONGODB_DATABASE = "quarkus.mongodb.database";
    public static final String SPRING_DATA_MONGODB_DATABASE = "spring.data.mongodb.database";
    public static final String KOGITO_PERSISTENCE_TRANSACTION_ENABLED = "kogito.persistence.transaction.enabled";
    // File System specific
    public static final String KOGITO_PERSISTENCE_FILESYSTEM_PATH = "kogito.persistence.filesystem.path";
    // Kafka specific
    public static final String QUARKUS_KAFKA_STREAMS_TOPICS_PROP = "quarkus.kafka-streams.topics";

    /**
     * Constants used during codegen
     */
    protected static final String KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE = "org.kie.kogito.persistence.KogitoProcessInstancesFactory";
    protected static final String KOGITO_PROCESS_INSTANCE_FACTORY_IMPL = "KogitoProcessInstancesFactoryImpl";
    protected static final String KOGITO_PROCESS_INSTANCE_PACKAGE = "org.kie.kogito.persistence";
    protected static final String CLIENT = "client";
    protected static final String QUERY_TIMEOUT = "queryTimeout";
    protected static final String AUTO_DDL = "autoDDL";
    protected static final String KOGITO = "kogito";
    protected static final String OR_ELSE = "orElse";
    protected static final String JAVA = ".java";
    protected static final String OPTIMISTIC_LOCK = "lock";
    protected static final String PATH_NAME = "path";
    protected static final String MONGODB_DB_NAME = "dbName";
    protected static final String TRANSACTION_ENABLED = "enabled";
    protected static final String TEMPLATE_NAME = "templateName";
    protected static final String TRANSACTION_MANAGER_NAME = "transactionManager";
    protected static final String MONGODB_TRANSACTION_MANAGER_IMPL = "MongoDBTransactionManagerImpl";
    protected static final String MONGODB_TRANSACTION_MANAGER_PACKAGE = "org.kie.kogito.mongodb.transaction";
    protected static final String MONGODB_TRANSACTION_MANAGER_FULLNAME = "org.kie.kogito.mongodb.transaction.MongoDBTransactionManager";

    /**
     * Generic PersistenceGenerator constants
     */
    public static final String GENERATOR_NAME = "persistence";
    protected static final String CLASS_TEMPLATES_PERSISTENCE = "/class-templates/persistence/";

    private final ProtoGenerator protoGenerator;

    public PersistenceGenerator(KogitoBuildContext context, ProtoGenerator protoGenerator) {
        super(context, GENERATOR_NAME);
        this.protoGenerator = protoGenerator;
    }

    @Override
    public Optional<ApplicationSection> section() {
        return Optional.empty();
    }

    @Override
    protected Collection<GeneratedFile> internalGenerate() {
        Collection<GeneratedFile> generatedFiles = new ArrayList<>();

        switch (persistenceType()) {
            case INFINISPAN_PERSISTENCE_TYPE:
                generatedFiles.addAll(infinispanBasedPersistence());
                break;
            case FILESYSTEM_PERSISTENCE_TYPE:
                generatedFiles.addAll(fileSystemBasedPersistence());
                break;
            case MONGODB_PERSISTENCE_TYPE:
                generatedFiles.addAll(mongodbBasedPersistence());
                break;
            case KAFKA_PERSISTENCE_TYPE:
                generatedFiles.addAll(kafkaBasedPersistence());
                break;
            case POSTGRESQL_PERSISTENCE_TYPE:
                generatedFiles.addAll(postgresqlBasedPersistence());
                break;
            case JDBC_PERSISTENCE_TYPE:
                generatedFiles.addAll(jdbcBasedPersistence());
                break;
            default:
                throw new IllegalArgumentException("Unknown persistenceType " + persistenceType());
        }

        generatedFiles.addAll(generateProtoMarshaller());
        generatedFiles.addAll(generateProtoForDataIndex());

        return generatedFiles;
    }

    @Override
    public boolean isEmpty() {
        // PersistenceGenerator is a different type of generator without specific resources
        return !context().getAddonsConfig().usePersistence();
    }

    public String persistenceType() {
        return context().getApplicationProperty(KOGITO_PERSISTENCE_TYPE).orElse(PersistenceGenerator.DEFAULT_PERSISTENCE_TYPE);
    }

    protected Collection<GeneratedFile> infinispanBasedPersistence() {
        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration().setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        persistenceProviderClazz.addConstructor(Keyword.PUBLIC).setBody(new BlockStmt().addStatement(new ExplicitConstructorInvocationStmt(false, null, NodeList.nodeList(new NullLiteralExpr()))));

        ConstructorDeclaration constructor = createConstructorForClazz(persistenceProviderClazz);

        if (context().hasDI()) {
            context().getDependencyInjectionAnnotator().withApplicationComponent(persistenceProviderClazz);
            context().getDependencyInjectionAnnotator().withInjection(constructor);

            FieldDeclaration templateNameField = new FieldDeclaration().addVariable(new VariableDeclarator()
                    .setType(new ClassOrInterfaceType(null, new SimpleName(Optional.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, String.class.getCanonicalName()))))
                    .setName(TEMPLATE_NAME));
            context().getDependencyInjectionAnnotator().withConfigInjection(templateNameField, "kogito.persistence.infinispan.template");
            // allow to inject template name for the cache
            BlockStmt templateMethodBody = new BlockStmt();
            templateMethodBody.addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(TEMPLATE_NAME), OR_ELSE).addArgument(new StringLiteralExpr(""))));

            MethodDeclaration templateNameMethod = new MethodDeclaration()
                    .addModifier(Keyword.PUBLIC)
                    .setName("template")
                    .setType(String.class)
                    .setBody(templateMethodBody);

            persistenceProviderClazz.addMember(templateNameField);
            persistenceProviderClazz.addMember(templateNameMethod);
        }

        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        CompilationUnit compilationUnit = new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE);
        compilationUnit.getTypes().add(persistenceProviderClazz);
        addOptimisticLockFlag(persistenceProviderClazz);
        generatePersistenceProviderClazz(persistenceProviderClazz, compilationUnit).ifPresent(generatedFiles::add);
        return generatedFiles;
    }

    protected Collection<GeneratedFile> kafkaBasedPersistence() {
        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                .setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        if (context().hasDI()) {
            context().getDependencyInjectionAnnotator().withApplicationComponent(persistenceProviderClazz);
        }

        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        TemplatedGenerator generator = TemplatedGenerator.builder().withTemplateBasePath(CLASS_TEMPLATES_PERSISTENCE)
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .withPackageName(KOGITO_PROCESS_INSTANCE_PACKAGE)
                .build(context(), "KafkaStreamsTopologyProducer");
        CompilationUnit parsedClazzFile = generator.compilationUnitOrThrow();
        ClassOrInterfaceDeclaration producer = parsedClazzFile.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new InvalidTemplateException(
                generator,
                "Failed to find template for KafkaStreamsTopologyProducer"));

        MethodCallExpr asListOfProcesses = new MethodCallExpr(new NameExpr("java.util.Arrays"), "asList");

        protoGenerator.getProcessIds().forEach(p -> asListOfProcesses.addArgument(new StringLiteralExpr(p)));
        producer.getFieldByName("processes")
                .orElseThrow(() -> new InvalidTemplateException(generator, "Failed to find field 'processes' in KafkaStreamsTopologyProducer template"))
                .getVariable(0).setInitializer(asListOfProcesses);

        String clazzName = KOGITO_PROCESS_INSTANCE_PACKAGE + "." + producer.getName().asString();
        generatedFiles.add(new GeneratedFile(GeneratedFileType.SOURCE,
                clazzName.replace('.', '/') + JAVA,
                parsedClazzFile.toString()));

        CompilationUnit compilationUnit = new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE);
        compilationUnit.getTypes().add(persistenceProviderClazz);
        generatePersistenceProviderClazz(persistenceProviderClazz, compilationUnit).ifPresent(generatedFiles::add);
        return generatedFiles;
    }

    protected Collection<GeneratedFile> generateProtoMarshaller() {
        if (!hasProtoMarshaller(context())) {
            // TODO implement a validation check to verify that data classes implement Serializable
            LOGGER.debug("Proto marshaller generation is skipped because " + KOGITO_PERSISTENCE_PROTO_MARSHALLER + "=false");
            return Collections.emptyList();
        }
        Proto proto = protoGenerator.protoOfDataClasses(context().getPackageName(), "import \"kogito-types.proto\";");

        List<String> variableMarshallers = new ArrayList<>();

        MarshallerGenerator marshallerGenerator = new MarshallerGenerator(context());

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
                    IOUtils.toString(context().getClassLoader().getResourceAsStream(typesURI))));
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

            variableMarshallers.add("org.kie.kogito.persistence.StringProtostreamBaseMarshaller");
            variableMarshallers.add("org.kie.kogito.persistence.BooleanProtostreamBaseMarshaller");
            variableMarshallers.add("org.kie.kogito.persistence.DateProtostreamBaseMarshaller");
            variableMarshallers.add("org.kie.kogito.persistence.DoubleProtostreamBaseMarshaller");
            variableMarshallers.add("org.kie.kogito.persistence.FloatProtostreamBaseMarshaller");
            variableMarshallers.add("org.kie.kogito.persistence.IntegerProtostreamBaseMarshaller");
            variableMarshallers.add("org.kie.kogito.persistence.LongProtostreamBaseMarshaller");
            variableMarshallers.add("org.kie.kogito.persistence.InstantProtostreamBaseMarshaller");
            variableMarshallers.add("org.kie.kogito.persistence.SerializableProtostreamBaseMarshaller");

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
            CatchClause catchClause = new CatchClause(new Parameter().setType(IOException.class).setName("e"), new BlockStmt());
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
                        IOUtils.toString(getClass().getResourceAsStream("/META-INF/services/org.kie.kogito.serialization.process.ObjectMarshallerStrategy"), "UTF-8");
            } catch (Exception e) {
                LOGGER.warn("No existing ObjectMarshallerStrategy found the the classpath to be included with the ProtoS generated one for SPI.");
            }
            objectMarshallerStrategyServiceDescriptor += "\n" + fqnProtoStreamMarshaller + "\n";

            generatedFiles.add(new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE,
                    "META-INF/services/org.kie.kogito.serialization.process.ObjectMarshallerStrategy",
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

    protected Collection<GeneratedFile> fileSystemBasedPersistence() {

        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                .setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        Optional<GeneratedFile> generatedClientFile = Optional.empty();
        if (context().hasDI()) {
            context().getDependencyInjectionAnnotator().withApplicationComponent(persistenceProviderClazz);

            FieldDeclaration pathField = new FieldDeclaration().addVariable(new VariableDeclarator()
                    .setType(new ClassOrInterfaceType(null, new SimpleName(Optional.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, String.class.getCanonicalName()))))
                    .setName(PATH_NAME));
            context().getDependencyInjectionAnnotator().withConfigInjection(pathField, KOGITO_PERSISTENCE_FILESYSTEM_PATH);
            // allow to inject path for the file system storage
            BlockStmt pathMethodBody = new BlockStmt();
            pathMethodBody.addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(PATH_NAME), OR_ELSE).addArgument(new StringLiteralExpr("/tmp"))));

            MethodDeclaration pathMethod = new MethodDeclaration()
                    .addModifier(Keyword.PUBLIC)
                    .setName(PATH_NAME)
                    .setType(String.class)
                    .setBody(pathMethodBody);

            persistenceProviderClazz.addMember(pathField);
            persistenceProviderClazz.addMember(pathMethod);
            generatedClientFile = generatePersistenceProviderClazz(persistenceProviderClazz,
                    new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE).addType(persistenceProviderClazz));
        }
        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        generatedClientFile.ifPresent(generatedFiles::add);
        return generatedFiles;
    }

    private Collection<GeneratedFile> mongodbBasedPersistence() {

        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                .setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL).setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        persistenceProviderClazz.addConstructor(Keyword.PUBLIC).setBody(new BlockStmt().addStatement(new ExplicitConstructorInvocationStmt(false, null, NodeList.nodeList(new NullLiteralExpr()))));

        ConstructorDeclaration constructor = createConstructorForClazz(persistenceProviderClazz);
        Optional<GeneratedFile> generatedClientFile = Optional.empty();
        Optional<GeneratedFile> generatedTMFile = Optional.empty();
        if (context().hasDI()) {
            context().getDependencyInjectionAnnotator().withApplicationComponent(persistenceProviderClazz);
            context().getDependencyInjectionAnnotator().withInjection(constructor);

            FieldDeclaration dbNameField = new FieldDeclaration().addVariable(new VariableDeclarator()
                    .setType(new ClassOrInterfaceType(null, new SimpleName(Optional.class.getCanonicalName()), NodeList.nodeList(
                            new ClassOrInterfaceType(null,
                                    String.class.getCanonicalName()))))
                    .setName(MONGODB_DB_NAME));
            //injecting dbName from quarkus/springboot properties else default kogito
            if (context() instanceof QuarkusKogitoBuildContext) {
                context().getDependencyInjectionAnnotator().withConfigInjection(dbNameField, QUARKUS_MONGODB_DATABASE);
            } else if (context() instanceof SpringBootKogitoBuildContext) {
                context().getDependencyInjectionAnnotator().withConfigInjection(dbNameField, SPRING_DATA_MONGODB_DATABASE);
            }

            BlockStmt dbNameMethodBody = new BlockStmt();
            dbNameMethodBody.addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(MONGODB_DB_NAME), OR_ELSE).addArgument(new StringLiteralExpr(KOGITO))));
            MethodDeclaration dbNameMethod = new MethodDeclaration()
                    .addModifier(Keyword.PUBLIC)
                    .setName(MONGODB_DB_NAME)
                    .setType(String.class)
                    .setBody(dbNameMethodBody);

            persistenceProviderClazz.addMember(dbNameField);
            persistenceProviderClazz.addMember(dbNameMethod);
            generatedTMFile = mongodbBasedTransaction(persistenceProviderClazz);
            addOptimisticLockFlag(persistenceProviderClazz);
            generatedClientFile = generatePersistenceProviderClazz(persistenceProviderClazz,
                    new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE).addType(persistenceProviderClazz));
        }

        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        generatedClientFile.ifPresent(generatedFiles::add);
        generatedTMFile.ifPresent(generatedFiles::add);
        return generatedFiles;
    }

    private Collection<GeneratedFile> postgresqlBasedPersistence() {
        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                .setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        if (context().hasDI()) {
            context().getDependencyInjectionAnnotator().withApplicationComponent(persistenceProviderClazz);

            //injecting constructor with parameter
            final String pgPoolClass = "io.vertx.pgclient.PgPool";
            ConstructorDeclaration constructor = persistenceProviderClazz
                    .addConstructor(Keyword.PUBLIC)
                    .addParameter(pgPoolClass, CLIENT)
                    .addParameter(StaticJavaParser.parseClassOrInterfaceType(Boolean.class.getName()), AUTO_DDL)
                    .addParameter(StaticJavaParser.parseClassOrInterfaceType(Long.class.getName()), QUERY_TIMEOUT)
                    .setBody(new BlockStmt().addStatement(new ExplicitConstructorInvocationStmt()
                            .setThis(false)
                            .addArgument(new NameExpr(CLIENT))
                            .addArgument(AUTO_DDL)
                            .addArgument(QUERY_TIMEOUT)));
            context().getDependencyInjectionAnnotator().withConfigInjection(
                    constructor.getParameterByName(AUTO_DDL).get(), KOGITO_PERSISTENCE_AUTO_DDL, Boolean.TRUE.toString());
            context().getDependencyInjectionAnnotator().withConfigInjection(
                    constructor.getParameterByName(QUERY_TIMEOUT).get(), KOGITO_PERSISTENCE_QUERY_TIMEOUT,
                    String.valueOf(10000));
            context().getDependencyInjectionAnnotator().withInjection(constructor);

            //empty constructor for DI
            persistenceProviderClazz.addConstructor(Keyword.PROTECTED);

            if (context() instanceof SpringBootKogitoBuildContext) {
                context().getDependencyInjectionAnnotator().withNamed(
                        constructor.getParameterByName(CLIENT).get(), KOGITO);

                //creating PgClient default producer class
                ClassOrInterfaceDeclaration pgClientProducerClazz = new ClassOrInterfaceDeclaration()
                        .setName("PgClientProducer")
                        .setModifiers(Modifier.Keyword.PUBLIC);

                //creating PgClient producer
                Parameter uriConfigParam = new Parameter()
                        .setType(StaticJavaParser.parseClassOrInterfaceType(Optional.class.getName())
                                .setTypeArguments(StaticJavaParser.parseClassOrInterfaceType(String.class.getName())))
                        .setName("uri");

                MethodDeclaration clientProviderMethod = pgClientProducerClazz.addMethod(CLIENT, Keyword.PUBLIC)
                        .setType(pgPoolClass)//PgPool
                        .addParameter(uriConfigParam)
                        .setBody(new BlockStmt()// return uri.isPresent() ?  PgPool.pool(uri.get()) : PgPool.pool()
                                .addStatement(new ReturnStmt(
                                        new ConditionalExpr(
                                                new MethodCallExpr(new NameExpr(uriConfigParam.getName()), "isPresent"),
                                                new MethodCallExpr(new NameExpr(pgPoolClass), "pool")
                                                        .addArgument(new MethodCallExpr(new NameExpr("uri"), "get")),
                                                new MethodCallExpr(new NameExpr(pgPoolClass), "pool")))));

                //inserting DI annotations
                context().getDependencyInjectionAnnotator().withConfigInjection(uriConfigParam, KOGITO_PERSISTENCE_POSTGRESQL_CONNECTION_URI);
                context().getDependencyInjectionAnnotator().withProduces(clientProviderMethod, true);
                context().getDependencyInjectionAnnotator().withNamed(clientProviderMethod, KOGITO);
                context().getDependencyInjectionAnnotator().withApplicationComponent(pgClientProducerClazz);

                Optional<GeneratedFile> generatedPgClientFile = generatePersistenceProviderClazz(pgClientProducerClazz,
                        new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE).addType(pgClientProducerClazz));
                generatedPgClientFile.ifPresent(generatedFiles::add);
            }
        }
        addOptimisticLockFlag(persistenceProviderClazz);
        Optional<GeneratedFile> generatedPgClientFile = generatePersistenceProviderClazz(persistenceProviderClazz,
                new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE).addType(persistenceProviderClazz));
        generatedPgClientFile.ifPresent(generatedFiles::add);
        return generatedFiles;
    }

    private Collection<GeneratedFile> jdbcBasedPersistence() {
        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                .setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        if (context().hasDI()) {
            context().getDependencyInjectionAnnotator().withApplicationComponent(persistenceProviderClazz);

            final String datasourceClass = "javax.sql.DataSource";
            ConstructorDeclaration constructor = persistenceProviderClazz
                    .addConstructor(Keyword.PUBLIC)
                    .addParameter(datasourceClass, "dataSource")
                    .addParameter(StaticJavaParser.parseClassOrInterfaceType(Boolean.class.getName()), AUTO_DDL)
                    .setBody(new BlockStmt().addStatement(new ExplicitConstructorInvocationStmt()
                            .setThis(false)
                            .addArgument(new NameExpr("dataSource"))
                            .addArgument(AUTO_DDL)));
            Optional<Parameter> autoDDL = constructor.getParameterByName(AUTO_DDL);
            autoDDL.ifPresent(parameter -> context().getDependencyInjectionAnnotator().withConfigInjection(
                    parameter, KOGITO_PERSISTENCE_AUTO_DDL, Boolean.TRUE.toString()));
            context().getDependencyInjectionAnnotator().withInjection(constructor);
            //empty constructor for DI
            persistenceProviderClazz.addConstructor(Keyword.PROTECTED);
        }

        addOptimisticLockFlag(persistenceProviderClazz);
        Optional<GeneratedFile> generatedPgClientFile = generatePersistenceProviderClazz(persistenceProviderClazz,
                new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE).addType(persistenceProviderClazz));
        generatedPgClientFile.ifPresent(generatedFiles::add);
        return generatedFiles;
    }

    private void addOptimisticLockFlag(ClassOrInterfaceDeclaration persistenceProviderClazz) {
        FieldDeclaration lockField = new FieldDeclaration().addVariable(new VariableDeclarator()
                .setType(new ClassOrInterfaceType(null, new SimpleName(Optional.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, Boolean.class.getCanonicalName()))))
                .setName(OPTIMISTIC_LOCK));
        if (context().hasDI()) {
            context().getDependencyInjectionAnnotator().withConfigInjection(lockField, KOGITO_PERSISTENCE_OPTIMISTIC_LOCK);
        }

        BlockStmt lockMethodBody = new BlockStmt();
        lockMethodBody.addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(OPTIMISTIC_LOCK), OR_ELSE).addArgument(new BooleanLiteralExpr(false))));
        MethodDeclaration enabledMethod = new MethodDeclaration()
                .addModifier(Keyword.PUBLIC)
                .setName(OPTIMISTIC_LOCK)
                .setType("boolean")
                .setBody(lockMethodBody);

        persistenceProviderClazz.addMember(lockField);
        persistenceProviderClazz.addMember(enabledMethod);
    }

    private Optional<GeneratedFile> mongodbBasedTransaction(ClassOrInterfaceDeclaration persistenceProviderClazz) {
        FieldDeclaration transactionManagerField = new FieldDeclaration().addVariable(new VariableDeclarator()
                .setType(new ClassOrInterfaceType(null, MONGODB_TRANSACTION_MANAGER_FULLNAME))
                .setName(TRANSACTION_MANAGER_NAME));

        context().getDependencyInjectionAnnotator().withInjection(transactionManagerField);

        BlockStmt transactionManagerMethodBody = new BlockStmt();
        transactionManagerMethodBody.addStatement(new ReturnStmt(new NameExpr(TRANSACTION_MANAGER_NAME)));
        MethodDeclaration transactionManagerMethod = new MethodDeclaration()
                .addModifier(Keyword.PUBLIC)
                .setName(TRANSACTION_MANAGER_NAME)
                .setType(MONGODB_TRANSACTION_MANAGER_FULLNAME)
                .setBody(transactionManagerMethodBody);

        persistenceProviderClazz.addMember(transactionManagerField);
        persistenceProviderClazz.addMember(transactionManagerMethod);

        ClassOrInterfaceDeclaration transactionProviderClazz = new ClassOrInterfaceDeclaration()
                .setName(MONGODB_TRANSACTION_MANAGER_IMPL).setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(MONGODB_TRANSACTION_MANAGER_FULLNAME);

        transactionProviderClazz.addConstructor(Keyword.PUBLIC).setBody(new BlockStmt().addStatement(new ExplicitConstructorInvocationStmt(false, null, NodeList.nodeList(new NullLiteralExpr()))));

        ConstructorDeclaration transactionProviderConstructor = createConstructorForClazz(transactionProviderClazz);

        context().getDependencyInjectionAnnotator().withApplicationComponent(transactionProviderClazz);
        context().getDependencyInjectionAnnotator().withInjection(transactionProviderConstructor);

        FieldDeclaration enabledField = new FieldDeclaration().addVariable(new VariableDeclarator()
                .setType(new ClassOrInterfaceType(null,
                        new SimpleName(Optional.class.getCanonicalName()),
                        NodeList.nodeList(new ClassOrInterfaceType(null, Boolean.class.getCanonicalName()))))
                .setName(TRANSACTION_ENABLED));
        if (context().hasDI()) {
            context().getDependencyInjectionAnnotator().withConfigInjection(enabledField, KOGITO_PERSISTENCE_TRANSACTION_ENABLED);
        }
        BlockStmt enabledMethodBody = new BlockStmt();
        enabledMethodBody.addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(TRANSACTION_ENABLED), OR_ELSE).addArgument(new BooleanLiteralExpr(false))));
        MethodDeclaration enabledMethod = new MethodDeclaration()
                .addModifier(Keyword.PUBLIC)
                .setName(TRANSACTION_ENABLED)
                .setType("boolean")
                .setBody(enabledMethodBody);

        transactionProviderClazz.addMember(enabledField);
        transactionProviderClazz.addMember(enabledMethod);

        return generatePersistenceProviderClazz(transactionProviderClazz,
                new CompilationUnit(MONGODB_TRANSACTION_MANAGER_PACKAGE).addType(transactionProviderClazz));
    }

    private ConstructorDeclaration createConstructorForClazz(ClassOrInterfaceDeclaration persistenceProviderClazz) {
        ConstructorDeclaration constructor = persistenceProviderClazz.addConstructor(Keyword.PUBLIC);
        List<Expression> paramNames = new ArrayList<>();
        for (String parameter : protoGenerator.getPersistenceClassParams()) {
            String name = "param" + paramNames.size();
            constructor.addParameter(parameter, name);
            paramNames.add(new NameExpr(name));
        }
        BlockStmt body = new BlockStmt();
        ExplicitConstructorInvocationStmt superExp = new ExplicitConstructorInvocationStmt(false, null, NodeList.nodeList(paramNames));
        body.addStatement(superExp);

        constructor.setBody(body);
        return constructor;
    }

    private Optional<GeneratedFile> generatePersistenceProviderClazz(ClassOrInterfaceDeclaration persistenceProviderClazz, CompilationUnit compilationUnit) {
        String pkgName = compilationUnit.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
        Optional<ClassOrInterfaceDeclaration> firstClazz = persistenceProviderClazz.findFirst(ClassOrInterfaceDeclaration.class);
        Optional<String> firstClazzName = firstClazz.map(c -> c.getName().toString());
        persistenceProviderClazz.getMembers().sort(new BodyDeclarationComparator());
        if (firstClazzName.isPresent()) {
            String clazzName = pkgName + "." + firstClazzName.get();
            return Optional.of(new GeneratedFile(GeneratedFileType.SOURCE, clazzName.replace('.', '/') + JAVA, compilationUnit.toString()));
        }
        return Optional.empty();
    }
}
