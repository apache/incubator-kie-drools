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
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratedFileType;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.context.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.process.persistence.proto.Proto;
import org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator;


public class PersistenceGenerator extends AbstractGenerator {

    public static final String FILESYSTEM_PERSISTENCE_TYPE = "filesystem";
    public static final String INFINISPAN_PERSISTENCE_TYPE = "infinispan";
    public static final String DEFAULT_PERSISTENCE_TYPE = INFINISPAN_PERSISTENCE_TYPE;
    public static final String MONGODB_PERSISTENCE_TYPE = "mongodb";

    protected static final String TEMPLATE_NAME = "templateName";
    protected static final String PATH_NAME = "path";

    private static final String KOGITO_PERSISTENCE_FS_PATH_PROP = "kogito.persistence.filesystem.path";
    
    private static final String KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE= "org.kie.kogito.persistence.KogitoProcessInstancesFactory";
    private static final String KOGITO_PROCESS_INSTANCE_FACTORY_IMPL= "KogitoProcessInstancesFactoryImpl";
    private static final String KOGITO_PROCESS_INSTANCE_PACKAGE = "org.kie.kogito.persistence";
    private static final String MONGODB_DB_NAME = "dbName";
    private static final String QUARKUS_PERSISTENCE_MONGODB_NAME_PROP = "quarkus.mongodb.database";
    private static final String SPRINGBOOT_PERSISTENCE_MONGODB_NAME_PROP = "spring.data.mongodb.database";
    private static final String OR_ELSE = "orElse";

    private final ProtoGenerator protoGenerator;

    public PersistenceGenerator(KogitoBuildContext context, ProtoGenerator protoGenerator) {
        super(context, "persistence");
        this.protoGenerator = protoGenerator;
    }

    @Override
    public Optional<ApplicationSection> section() {
        return Optional.empty();
    }

    @Override
    public Collection<GeneratedFile> generate() {
        if(!context().getAddonsConfig().usePersistence()) {
            return Collections.emptyList();
        }

        switch (persistenceType()) {
            case INFINISPAN_PERSISTENCE_TYPE:
                return infinispanBasedPersistence();
            case FILESYSTEM_PERSISTENCE_TYPE:
                return fileSystemBasedPersistence();
            case MONGODB_PERSISTENCE_TYPE:
                return mongodbBasedPersistence();
            default:
                throw new IllegalArgumentException("Unknown persistenceType " + persistenceType());
        }
    }

    public String persistenceType() {
        return context().getApplicationProperty("kogito.persistence.type").orElse(PersistenceGenerator.DEFAULT_PERSISTENCE_TYPE);
    }

    protected Collection<GeneratedFile> infinispanBasedPersistence() {
        Collection<GeneratedFile> generatedFiles = new ArrayList<>(protoGenerator.generateProtoFiles());

        Proto proto = protoGenerator.protoOfDataClasses(context().getPackageName(), "import \"kogito-types.proto\";");

        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration().setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        CompilationUnit compilationUnit = new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE);
        compilationUnit.getTypes().add(persistenceProviderClazz);

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
        List<String> variableMarshallers = new ArrayList<>();

        MarshallerGenerator marshallerGenerator = new MarshallerGenerator(context());

        String protoContent = proto.toString();

        List<CompilationUnit> marshallers;
        try {
            marshallers = marshallerGenerator.generate(protoContent);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible to obtain marshaller CompilationUnits", e);
        }
        if (!marshallers.isEmpty()) {

            for (CompilationUnit marshallerClazz : marshallers) {
                String packageName = marshallerClazz.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
                String clazzName = packageName + "." + marshallerClazz.findFirst(ClassOrInterfaceDeclaration.class).map(c -> c.getName().toString()).get();

                variableMarshallers.add(clazzName);

                generatedFiles.add(new GeneratedFile(GeneratedFileType.SOURCE,
                        clazzName.replace('.', '/') + ".java",
                        marshallerClazz.toString()));
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

        generatePersistenceProviderClazz(persistenceProviderClazz, compilationUnit)
                .ifPresent(generatedFiles::add);

        return generatedFiles;
    }

    protected Collection<GeneratedFile> fileSystemBasedPersistence() {

        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                .setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        CompilationUnit compilationUnit = new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE);
        compilationUnit.getTypes().add(persistenceProviderClazz);

        if (context().hasDI()) {
            context().getDependencyInjectionAnnotator().withApplicationComponent(persistenceProviderClazz);

            FieldDeclaration pathField = new FieldDeclaration().addVariable(new VariableDeclarator()
                    .setType(new ClassOrInterfaceType(null, new SimpleName(Optional.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, String.class.getCanonicalName()))))
                    .setName(PATH_NAME));
            context().getDependencyInjectionAnnotator().withConfigInjection(pathField, KOGITO_PERSISTENCE_FS_PATH_PROP);
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

        generatePersistenceProviderClazz(persistenceProviderClazz, compilationUnit)
                .ifPresent(generatedFiles::add);

        return generatedFiles;
    }

    private Collection<GeneratedFile> mongodbBasedPersistence() {
        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                                                                                                .setName(KOGITO_PROCESS_INSTANCE_FACTORY_IMPL).setModifiers(Modifier.Keyword.PUBLIC)
                                                                                                .addExtendedType(KOGITO_PROCESS_INSTANCE_FACTORY_PACKAGE);

        CompilationUnit compilationUnit = new CompilationUnit(KOGITO_PROCESS_INSTANCE_PACKAGE);
        compilationUnit.getTypes().add(persistenceProviderClazz);

        persistenceProviderClazz.addConstructor(Keyword.PUBLIC).setBody(new BlockStmt().addStatement(new ExplicitConstructorInvocationStmt(false, null, NodeList.nodeList(new NullLiteralExpr()))));

        ConstructorDeclaration constructor = createConstructorForClazz(persistenceProviderClazz);

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
                context().getDependencyInjectionAnnotator().withConfigInjection(dbNameField, QUARKUS_PERSISTENCE_MONGODB_NAME_PROP);
            } else if (context() instanceof SpringBootKogitoBuildContext) {
                context().getDependencyInjectionAnnotator().withConfigInjection(dbNameField, SPRINGBOOT_PERSISTENCE_MONGODB_NAME_PROP);
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
        generatePersistenceProviderClazz(persistenceProviderClazz, compilationUnit)
                .ifPresent(generatedFiles::add);

        return generatedFiles;
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
            return Optional.of(new GeneratedFile(GeneratedFileType.SOURCE, clazzName.replace('.', '/') + ".java", compilationUnit.toString()));
        }
        return Optional.empty();
    }
}
