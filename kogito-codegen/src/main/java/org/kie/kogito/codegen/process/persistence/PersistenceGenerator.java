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

import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.metadata.MetaDataWriter;
import org.kie.kogito.codegen.metadata.PersistenceLabeler;
import org.kie.kogito.codegen.metadata.PersistenceProtoFilesLabeler;
import org.kie.kogito.codegen.process.persistence.proto.Proto;
import org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator;

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


public class PersistenceGenerator extends AbstractGenerator {
    
	private static final String FILESYSTEM_PERSISTENCE_TYPE = "filesystem";
	private static final String INFINISPAN_PERSISTENCE_TYPE = "infinispan";
	private static final String DEFAULT_PERSISTENCE_TYPE = INFINISPAN_PERSISTENCE_TYPE;
    
	private static final String TEMPLATE_NAME = "templateName";
	private static final String PATH_NAME = "path";
	
    private static final String KOGITO_APPLICATION_PROTO = "kogito-application.proto";
    private static final String KOGITO_PERSISTENCE_FS_PATH_PROP = "kogito.persistence.filesystem.path";

    private final File targetDirectory;
    private final Collection<?> modelClasses;    
    private final boolean persistence;
    private final ProtoGenerator<?> protoGenerator;
    
    private List<String> parameters;
    
    private String packageName;
    private DependencyInjectionAnnotator annotator;
    
    private ClassLoader classLoader;
    
    private PersistenceProtoFilesLabeler persistenceProtoLabeler = new PersistenceProtoFilesLabeler();
    private PersistenceLabeler persistenceLabeler = new PersistenceLabeler();
        
    public PersistenceGenerator(File targetDirectory, Collection<?> modelClasses, boolean persistence, ProtoGenerator<?> protoGenerator, List<String> parameters) {
        this(targetDirectory, modelClasses, persistence, protoGenerator, Thread.currentThread().getContextClassLoader(), parameters);
    }
    
    public PersistenceGenerator(File targetDirectory, Collection<?> modelClasses, boolean persistence, ProtoGenerator<?> protoGenerator, ClassLoader classLoader, List<String> parameters) {
        this.targetDirectory = targetDirectory;
        this.modelClasses = modelClasses;
        this.persistence = persistence;
        this.protoGenerator = protoGenerator;
        this.classLoader = classLoader;
        this.parameters = parameters;
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
    	String persistenceType = context.getApplicationProperty("kogito.persistence.type").orElse(DEFAULT_PERSISTENCE_TYPE);
    	
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        if (persistence) {
        	if (persistenceType.equals(INFINISPAN_PERSISTENCE_TYPE)) {
        		inifinispanBasedPersistence(generatedFiles);
        	} else if (persistenceType.equals(FILESYSTEM_PERSISTENCE_TYPE)) {
        		fileSystemBasedPersistence(generatedFiles);
        	}

        }

        if (targetDirectory.isDirectory()) {
            MetaDataWriter.writeLabelsImageMetadata( targetDirectory, getLabels() );
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
    protected void inifinispanBasedPersistence(List<GeneratedFile> generatedFiles) {
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
                .setName("KogitoProcessInstancesFactoryImpl")
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType("org.kie.kogito.persistence.KogitoProcessInstancesFactory");
        
        CompilationUnit compilationUnit = new CompilationUnit("org.kie.kogito.persistence");            
        compilationUnit.getTypes().add(persistenceProviderClazz);  
        
        persistenceProviderClazz.addConstructor(Keyword.PUBLIC).setBody(new BlockStmt().addStatement(new ExplicitConstructorInvocationStmt(false, null, NodeList.nodeList(new NullLiteralExpr()))));
        
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
        
        if (useInjection()) {
            annotator.withApplicationComponent(persistenceProviderClazz);
            annotator.withInjection(constructor);
            
            FieldDeclaration templateNameField = new FieldDeclaration().addVariable(new VariableDeclarator()
                                                                                     .setType(new ClassOrInterfaceType(null, new SimpleName(Optional.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, String.class.getCanonicalName()))))
                                                                                     .setName(TEMPLATE_NAME));
            annotator.withConfigInjection(templateNameField, "kogito.persistence.infinispan.template");
            // allow to inject template name for the cache
            BlockStmt templateMethodBody = new BlockStmt();                
            templateMethodBody.addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(TEMPLATE_NAME), "orElse").addArgument(new StringLiteralExpr(""))));
            
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
                    
                
                String packageName = compilationUnit.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
                String clazzName = packageName + "." + persistenceProviderClazz.findFirst(ClassOrInterfaceDeclaration.class).map(c -> c.getName().toString()).get();
             
                generatedFiles.add(new GeneratedFile(GeneratedFile.Type.CLASS,
                                                     clazzName.replace('.', '/') + ".java",
                                                     compilationUnit.toString().getBytes(StandardCharsets.UTF_8))); 
            } catch (Exception e) {
                throw new RuntimeException("Error when generating marshallers for defined variables", e);
            }
            persistenceProviderClazz.getMembers().sort(new BodyDeclarationComparator());
        }
    }
    
    protected void fileSystemBasedPersistence(List<GeneratedFile> generatedFiles) {
    	ClassOrInterfaceDeclaration persistenceProviderClazz = new ClassOrInterfaceDeclaration()
                .setName("KogitoProcessInstancesFactoryImpl")
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addExtendedType("org.kie.kogito.persistence.KogitoProcessInstancesFactory");
        
        CompilationUnit compilationUnit = new CompilationUnit("org.kie.kogito.persistence");            
        compilationUnit.getTypes().add(persistenceProviderClazz);                 
        
        if (useInjection()) {
            annotator.withApplicationComponent(persistenceProviderClazz);            
            
            FieldDeclaration pathField = new FieldDeclaration().addVariable(new VariableDeclarator()
                                                                                     .setType(new ClassOrInterfaceType(null, new SimpleName(Optional.class.getCanonicalName()), NodeList.nodeList(new ClassOrInterfaceType(null, String.class.getCanonicalName()))))
                                                                                     .setName(PATH_NAME));
            annotator.withConfigInjection(pathField, KOGITO_PERSISTENCE_FS_PATH_PROP);
            // allow to inject path for the file system storage
            BlockStmt pathMethodBody = new BlockStmt();                
            pathMethodBody.addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(PATH_NAME), "orElse").addArgument(new StringLiteralExpr("/tmp"))));
            
            MethodDeclaration pathMethod = new MethodDeclaration()
                    .addModifier(Keyword.PUBLIC)
                    .setName(PATH_NAME)
                    .setType(String.class)                                
                    .setBody(pathMethodBody);
            
            persistenceProviderClazz.addMember(pathField);
            persistenceProviderClazz.addMember(pathMethod);
        }
        
        String packageName = compilationUnit.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
        String clazzName = packageName + "." + persistenceProviderClazz.findFirst(ClassOrInterfaceDeclaration.class).map(c -> c.getName().toString()).get();
     
        generatedFiles.add(new GeneratedFile(GeneratedFile.Type.CLASS,
                                             clazzName.replace('.', '/') + ".java",
                                             compilationUnit.toString().getBytes(StandardCharsets.UTF_8))); 
        
        persistenceProviderClazz.getMembers().sort(new BodyDeclarationComparator());
    }
}
