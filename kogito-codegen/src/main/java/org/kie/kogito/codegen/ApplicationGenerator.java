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

package org.kie.kogito.codegen;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.rules.RuleUnitsRegisterClass.RULE_UNIT_REGISTER_FQN;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.kogito.Config;
import org.kie.kogito.codegen.metadata.ImageMetaData;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ApplicationGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationGenerator.class);

    private static final String RESOURCE = "/class-templates/ApplicationTemplate.java";
    private final static String LABEL_PREFIX = "org.kie/";
    
    private ObjectMapper mapper = new ObjectMapper();   
    
    private final String packageName;
    private final String sourceFilePath;
    private final String completePath;
    private final String targetCanonicalName;
    private final File targetDirectory;

    private String targetTypeName;
    private boolean hasCdi;
    private boolean hasRuleUnits;
    private final List<BodyDeclaration<?>> factoryMethods;
    private ConfigGenerator configGenerator = new ConfigGenerator();
    private List<Generator> generators = new ArrayList<>();

    public ApplicationGenerator(String packageName, File targetDirectory) {
        if (packageName == null) {
            throw new IllegalArgumentException("Package name cannot be undefined (null), please specify a package name!");
        }
        this.packageName = packageName;
        this.targetDirectory = targetDirectory;
        this.targetTypeName = "Application";
        this.targetCanonicalName = this.packageName + "." + targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.completePath = "src/main/java/" + sourceFilePath;
        this.factoryMethods = new ArrayList<>();
    }

    public String targetCanonicalName() {
        return targetCanonicalName;
    }

    public String generatedFilePath() {
        return sourceFilePath;
    }

    public void addFactoryMethods(Collection<MethodDeclaration> decls) {
        factoryMethods.addAll(decls);
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit =
                parse(this.getClass().getResourceAsStream(RESOURCE))
                        .setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration cls = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).get();
        
        FieldDeclaration unitOfWorkManagerField = cls.findFirst(FieldDeclaration.class).filter(fd -> fd.getVariable(0).getNameAsString().equals("unitOfWorkManager")).get();
        // unit of work manager setup
        unitOfWorkManagerField.getVariable(0).setInitializer(new ObjectCreationExpr(null, 
                                                                                    new ClassOrInterfaceType(null, DefaultUnitOfWorkManager.class.getCanonicalName()), 
                                                                                    NodeList.nodeList(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, CollectingUnitOfWorkFactory.class.getCanonicalName()), NodeList.nodeList()))));
        
        
        if (hasCdi) {
            cls.addAnnotation("javax.inject.Singleton");                       
        }

        if (hasRuleUnits) {
            // static {
            //    try {
            //        Class.forName( RULE_UNIT_REGISTER_FQN );
            //    } catch (ClassNotFoundException e) { }
            // }

            BlockStmt blockStmt = cls.addStaticInitializer();
            TryStmt tryStmt = new TryStmt();
            blockStmt.addStatement( tryStmt );

            tryStmt.getTryBlock().addStatement( new MethodCallExpr( new NameExpr("Class"), "forName" )
                    .addArgument( new StringLiteralExpr( RULE_UNIT_REGISTER_FQN ) ) );

            tryStmt.getCatchClauses().add( new CatchClause()
                    .setParameter( new Parameter( new ClassOrInterfaceType("ClassNotFoundException"), new SimpleName( "e" ) ) ) );
        }

        cls.addMember(new FieldDeclaration()
                              .addModifier(Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC)
                              .addVariable(new VariableDeclarator()
                                                   .setType(Config.class.getCanonicalName())
                                                   .setName("config")
                                                   .setInitializer(configGenerator.newInstance())));

        factoryMethods.forEach(cls::addMember);

        for (Generator generator : generators) {
            ApplicationSection section = generator.section();
            cls.addMember(section.fieldDeclaration());
            cls.addMember(section.factoryMethod());
            cls.addMember(section.classDeclaration());
        }

        return compilationUnit;
    }

    public ApplicationGenerator withDependencyInjection(boolean hasCdi) {
        this.hasCdi = hasCdi;
        return this;
    }

   public ApplicationGenerator withRuleUnits(boolean hasRuleUnits) {
        this.hasRuleUnits = hasRuleUnits;
        return this;
    }

    public Collection<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles =
                generators.stream()
                        .flatMap(gen -> gen.generate().stream())
                        .collect(Collectors.toList());
        generators.forEach(gen -> gen.updateConfig(configGenerator));
        generators.forEach(gen -> writeLabelsImageMetadata(gen.getLabels()));
        generatedFiles.add(new GeneratedFile(GeneratedFile.Type.APPLICATION,
                                             generatedFilePath(),
                                             log( compilationUnit().toString() ).getBytes(StandardCharsets.UTF_8)));
        return generatedFiles;
    }

    public <G extends Generator> G withGenerator(G generator) {
        this.generators.add(generator);
        generator.setPackageName(packageName);
        generator.setDependencyInjection(hasCdi);
        return generator;
    }
    
    protected void writeLabelsImageMetadata(Map<String, String> labels) {
        try {
            Path imageMetaDataFile = Paths.get(targetDirectory.getAbsolutePath(), "image_metadata.json");
            ImageMetaData imageMetadata;
            if (Files.exists(imageMetaDataFile)) {
                // read the file to merge the content
                imageMetadata =  mapper.readValue(imageMetaDataFile.toFile(), ImageMetaData.class);
            } else {
                imageMetadata = new ImageMetaData();            
            }
            imageMetadata.add(labels);

            Files.createDirectories(imageMetaDataFile.getParent());
            Files.write(imageMetaDataFile,
                        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(imageMetadata).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String log(String source) {
        if ( logger.isDebugEnabled() ) {
            logger.debug( "=====" );
            logger.debug( source );
            logger.debug( "=====" );
        }
        return source;
    }
}
