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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.SpringDependencyInjectionAnnotator;
import org.kie.kogito.codegen.metadata.MetaDataWriter;
import org.kie.kogito.codegen.metadata.PersistenceLabeler;
import org.kie.kogito.codegen.metadata.PersistenceProtoFilesLabeler;
import org.kie.kogito.codegen.process.persistence.proto.Proto;
import org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator;


public class PersistenceGenerator extends AbstractGenerator {

    public static final String FILESYSTEM_PERSISTENCE_TYPE = "filesystem";
    public static final String INFINISPAN_PERSISTENCE_TYPE = "infinispan";
    public static final String DEFAULT_PERSISTENCE_TYPE = INFINISPAN_PERSISTENCE_TYPE;
    public static final String MONGODB_PERSISTENCE_TYPE = "mongodb";
    
    private static final String TEMPLATE_NAME = "templateName";
    private static final String PATH_NAME = "path";
    
    private static final String KOGITO_APPLICATION_PROTO = "kogito-application.proto";
    private static final String KOGITO_PERSISTENCE_FS_PATH_PROP = "kogito.persistence.filesystem.path";
    
    private static final String KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE= "org.kie.kogito.persistence.KogitoProcessInstancesFactory";
    private static final String KOGITO_PROCESS_INSTANCE_FACTORY_IMPL= "KogitoProcessInstancesFactoryImpl";
    private static final String KOGITO_PROCESS_INSTANCE_PACKAGE = "org.kie.kogito.persistence";
    private static final String MONGODB_DB_NAME = "dbName";
    private static final String QUARKUS_PERSISTENCE_MONGODB_NAME_PROP = "quarkus.mongodb.database";
    private static final String SPRINGBOOT_PERSISTENCE_MONGODB_NAME_PROP = "spring.data.mongodb.database";
    private static final String OR_ELSE = "orElse";

    private final File targetDirectory;
    private final Collection<?> modelClasses;
    private final boolean persistence;
    private final ProtoGenerator<?> protoGenerator;

    private List<String> parameters;

    private String packageName;
    private DependencyInjectionAnnotator annotator;

    private ClassLoader classLoader;
    private String persistenceType;

    private PersistenceProtoFilesLabeler persistenceProtoLabeler = new PersistenceProtoFilesLabeler();
    private PersistenceLabeler persistenceLabeler = new PersistenceLabeler();

    public PersistenceGenerator(File targetDirectory, Collection<?> modelClasses, boolean persistence, ProtoGenerator<?> protoGenerator, List<String> parameters, String persistenceType) {
        this(targetDirectory, modelClasses, persistence, protoGenerator, Thread.currentThread().getContextClassLoader(), parameters, persistenceType);
    }

    public PersistenceGenerator(File targetDirectory, Collection<?> modelClasses, boolean persistence, ProtoGenerator<?> protoGenerator, ClassLoader classLoader, List<String> parameters, String persistenceType) {
        this.targetDirectory = targetDirectory;
        this.modelClasses = modelClasses;
        this.persistence = persistence;
        this.protoGenerator = protoGenerator;
        this.classLoader = classLoader;
        this.parameters = parameters;
        this.persistenceType = persistenceType;
        if (this.persistence) {
            this.addLabeler(persistenceProtoLabeler);
            this.addLabeler(persistenceLabeler);
        }
    }

    @Override
    public ApplicationSection section() {
        return null;
    }


    @Override
    public Collection<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        if (persistence) {
            if (persistenceType.equals(INFINISPAN_PERSISTENCE_TYPE)) {
                infinispanBasedPersistence(generatedFiles);
            } else if (persistenceType.equals(FILESYSTEM_PERSISTENCE_TYPE)) {
                fileSystemBasedPersistence(generatedFiles);
            } else if (persistenceType.equals(MONGODB_PERSISTENCE_TYPE)) {
                mongodbBasedPersistence(generatedFiles);
            }

        }

        if (targetDirectory.isDirectory()) {
            MetaDataWriter.writeLabelsImageMetadata(targetDirectory, getLabels());
        }
        return generatedFiles;
    }

    @Override
    public void updateConfig(ConfigGenerator cfg) {
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void setDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
    }

    protected boolean useInjection() {
        return this.annotator != null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void infinispanBasedPersistence(List<GeneratedFile> generatedFiles) {
        Collection dataModelClasses = protoGenerator.extractDataClasses((Collection) modelClasses, targetDirectory.toString());
        Path protoFilePath = Paths.get(targetDirectory.getParent(), "src/main/resources", "/persistence", KOGITO_APPLICATION_PROTO);
        File persistencePath = Paths.get(targetDirectory.getAbsolutePath(), "/classes/persistence").toFile();

        if (persistencePath != null && persistencePath.isDirectory()) {
            // only process proto files generated by the inner generator
            for (final File protoFile : Objects.requireNonNull(persistencePath.listFiles((dir, name) ->
                    !KOGITO_APPLICATION_PROTO.equalsIgnoreCase(name) && name.toLowerCase().endsWith(PersistenceProtoFilesLabeler.PROTO_FILE_EXT))))
                this.persistenceProtoLabeler.processProto(protoFile);
        }

        if (!protoFilePath.toFile().exists()) {
            try {
                // generate proto file based on known data model
                Proto proto = protoGenerator.generate(packageName, dataModelClasses, "import \"kogito-types.proto\";");
                protoFilePath = Paths.get(targetDirectory.toString(), "classes", "/persistence", KOGITO_APPLICATION_PROTO);

                Files.createDirectories(protoFilePath.getParent());
                Files.write(protoFilePath, proto.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException("Error during proto file generation/store", e);
            }

        }


        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                .setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        CompilationUnit compilationUnit = new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE);
        compilationUnit.getTypes().add(persistenceProviderClazz);

        persistenceProviderClazz.addConstructor(Keyword.PUBLIC).setBody(new BlockStmt().addStatement(new ExplicitConstructorInvocationStmt(false, null, NodeList.nodeList(new NullLiteralExpr()))));

        ConstructorDeclaration constructor = createConstructorForClazz(persistenceProviderClazz);

        if (useInjection()) {
            annotator.withApplicationComponent(persistenceProviderClazz);
            annotator.withInjection(constructor);

            FieldDeclaration templateNameField = new FieldDeclaration().addVariable(new VariableDeclarator()
                    .setType(new ClassOrInterfaceType(null, new SimpleName(Optional.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, String.class.getCanonicalName()))))
                    .setName(TEMPLATE_NAME));
            annotator.withConfigInjection(templateNameField, "kogito.persistence.infinispan.template");
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
        List<String> variableMarshallers = new ArrayList<>();
        // handler process variable marshallers
        if (protoFilePath.toFile().exists()) {
            MarshallerGenerator marshallerGenerator = new MarshallerGenerator(this.classLoader);
            try {
                String protoContent = new String(Files.readAllBytes(protoFilePath));

                List<CompilationUnit> marshallers = marshallerGenerator.generate(protoContent);

                if (!marshallers.isEmpty()) {

                    for (CompilationUnit marshallerClazz : marshallers) {
                        String packageName = marshallerClazz.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
                        String clazzName = packageName + "." + marshallerClazz.findFirst(ClassOrInterfaceDeclaration.class).map(c -> c.getName().toString()).get();

                        variableMarshallers.add(clazzName);

                        generatedFiles.add(new GeneratedFile(GeneratedFile.Type.CLASS,
                                clazzName.replace('.', '/') + ".java",
                                marshallerClazz.toString().getBytes(StandardCharsets.UTF_8)));
                    }
                }

                // handler process variable marshallers                                     
                if (!variableMarshallers.isEmpty()) {

                    MethodDeclaration protoMethod = new MethodDeclaration()
                            .addModifier(Keyword.PUBLIC)
                            .setName("proto")
                            .setType(String.class)
                            .setBody(new BlockStmt()
                                    .addStatement(new ReturnStmt(new StringLiteralExpr().setString(protoContent))));

                    persistenceProviderClazz.addMember(protoMethod);

                    ClassOrInterfaceType listType = new ClassOrInterfaceType(null, List.class.getCanonicalName());
                    BlockStmt marshallersMethodBody = new BlockStmt();
                    VariableDeclarationExpr marshallerList = new VariableDeclarationExpr(new VariableDeclarator(listType, "list", new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ArrayList.class.getCanonicalName()), NodeList.nodeList())));
                    marshallersMethodBody.addStatement(marshallerList);

                    for (String marshallerClazz : variableMarshallers) {

                        MethodCallExpr addMarshallerMethod = new MethodCallExpr(new NameExpr("list"), "add").addArgument(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, marshallerClazz), NodeList.nodeList()));
                        marshallersMethodBody.addStatement(addMarshallerMethod);

                    }

                    marshallersMethodBody.addStatement(new ReturnStmt(new NameExpr("list")));

                    MethodDeclaration marshallersMethod = new MethodDeclaration()
                            .addModifier(Keyword.PUBLIC)
                            .setName("marshallers")
                            .setType(listType)
                            .setBody(marshallersMethodBody);

                    persistenceProviderClazz.addMember(marshallersMethod);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error when generating marshallers for defined variables", e);
            }
            generatePersistenceProviderClazz(generatedFiles, persistenceProviderClazz, compilationUnit);
        }
    }

    protected void fileSystemBasedPersistence(List<GeneratedFile> generatedFiles) {
        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                .setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        CompilationUnit compilationUnit = new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE);
        compilationUnit.getTypes().add(persistenceProviderClazz);

        if (useInjection()) {
            annotator.withApplicationComponent(persistenceProviderClazz);

            FieldDeclaration pathField = new FieldDeclaration().addVariable(new VariableDeclarator()
                    .setType(new ClassOrInterfaceType(null, new SimpleName(Optional.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, String.class.getCanonicalName()))))
                    .setName(PATH_NAME));
            annotator.withConfigInjection(pathField, KOGITO_PERSISTENCE_FS_PATH_PROP);
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
        }

        generatePersistenceProviderClazz(generatedFiles, persistenceProviderClazz, compilationUnit);
    }

    private void mongodbBasedPersistence(List<GeneratedFile> generatedFiles) {
        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                                                                                                .setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL).setModifiers(Modifier.Keyword.PUBLIC)
                                                                                                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        CompilationUnit compilationUnit = new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE);
        compilationUnit.getTypes().add(persistenceProviderClazz);

        persistenceProviderClazz.addConstructor(Keyword.PUBLIC).setBody(new BlockStmt().addStatement(new ExplicitConstructorInvocationStmt(false, null, NodeList.nodeList(new NullLiteralExpr()))));

        ConstructorDeclaration constructor = createConstructorForClazz(persistenceProviderClazz);
        if (useInjection()) {
            annotator.withApplicationComponent(persistenceProviderClazz);
            annotator.withInjection(constructor);

            FieldDeclaration dbNameField = new FieldDeclaration().addVariable(new VariableDeclarator()
                                                                                                      .setType(new ClassOrInterfaceType(null, new SimpleName(Optional.class.getCanonicalName()), NodeList.nodeList(
                                                                                                                                                                                                                   new ClassOrInterfaceType(null,
                                                                                                                                                                                                                                            String.class.getCanonicalName()))))
                                                                                                      .setName(MONGODB_DB_NAME));
            //injecting dbName from quarkus/springboot properties else default kogito
            if (annotator instanceof CDIDependencyInjectionAnnotator) {
                annotator.withConfigInjection(dbNameField, QUARKUS_PERSISTENCE_MONGODB_NAME_PROP);
            } else if (annotator instanceof SpringDependencyInjectionAnnotator) {
                annotator.withConfigInjection(dbNameField, SPRINGBOOT_PERSISTENCE_MONGODB_NAME_PROP);
            }
           
            BlockStmt dbNameMethodBody = new BlockStmt();
            dbNameMethodBody.addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(MONGODB_DB_NAME), OR_ELSE).addArgument(new StringLiteralExpr("kogito"))));
            MethodDeclaration dbNameMethod = new MethodDeclaration()
                                                                  .addModifier(Keyword.PUBLIC)
                                                                  .setName(MONGODB_DB_NAME)
                                                                  .setType(String.class)
                                                                  .setBody(dbNameMethodBody);

            persistenceProviderClazz.addMember(dbNameField);
            persistenceProviderClazz.addMember(dbNameMethod);

        }
        generatePersistenceProviderClazz(generatedFiles, persistenceProviderClazz, compilationUnit);
    }

    private ConstructorDeclaration createConstructorForClazz(ClassOrInterfaceDeclaration persistenceProviderClazz) {
        ConstructorDeclaration constructor = persistenceProviderClazz.addConstructor(Keyword.PUBLIC);
        List<Expression> paramNames = new ArrayList<>();
        for (String parameter : parameters) {
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

    private void generatePersistenceProviderClazz(List<GeneratedFile> generatedFiles, ClassOrInterfaceDeclaration persistenceProviderClazz, CompilationUnit compilationUnit) {
        String pkgName = compilationUnit.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
        Optional<ClassOrInterfaceDeclaration> firstClazz = persistenceProviderClazz.findFirst(ClassOrInterfaceDeclaration.class);
        Optional<String> firstClazzName = firstClazz.map(c -> c.getName().toString());
        if (firstClazzName.isPresent()) {
            String clazzName = pkgName + "." + firstClazzName.get();

            generatedFiles.add(new GeneratedFile(GeneratedFile.Type.CLASS, clazzName.replace('.', '/') + ".java", compilationUnit.toString().getBytes(StandardCharsets.UTF_8)));
        }
        persistenceProviderClazz.getMembers().sort(new BodyDeclarationComparator());
    }
}
