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

package org.kie.submarine.codegen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.submarine.Config;
import org.kie.submarine.codegen.metadata.ImageMetaData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

public class ApplicationGenerator {

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
    private final List<MethodDeclaration> factoryMethods;
    private ConfigGenerator configGenerator = new ConfigGenerator();
    private List<Generator> generators = new ArrayList<>();

    public ApplicationGenerator(String packageName, File targetDirectory) {
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
                JavaParser.parse(this.getClass().getResourceAsStream(RESOURCE))
                        .setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration cls = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).get();
        if (hasCdi) {
            cls.addAnnotation("javax.inject.Singleton");
        }

        cls.addMember(new FieldDeclaration()
                              .addModifier(Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC)
                              .addVariable(new VariableDeclarator()
                                                   .setType(Config.class.getCanonicalName())
                                                   .setName("config")
                                                   .setInitializer(configGenerator.newInstance())));

        factoryMethods.forEach(cls::addMember);

        return compilationUnit;
    }

    public ApplicationGenerator withDependencyInjection(boolean hasCdi) {
        this.hasCdi = hasCdi;
        return this;
    }

    public Collection<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles =
                generators.stream()
                        .flatMap(gen -> gen.generate().stream())
                        .collect(Collectors.toList());
        generators.forEach(gen -> gen.updateConfig(configGenerator));
        generators.forEach(gen -> factoryMethods.addAll(gen.factoryMethods()));
        generators.forEach(gen -> writeLabelsImageMetadata(gen.getLabels()));
        generatedFiles.add(new GeneratedFile(generatedFilePath(), compilationUnit().toString().getBytes()));
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
            ImageMetaData imageMetadata = null;
            if (Files.exists(imageMetaDataFile)) {
                // read the file to merge the content
                imageMetadata =  mapper.readValue(imageMetaDataFile.toFile(), ImageMetaData.class);
            } else {
                imageMetadata = new ImageMetaData();            
            }
            imageMetadata.add(labels);
            
            Files.write(imageMetaDataFile, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(imageMetadata).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
               
    }
}
