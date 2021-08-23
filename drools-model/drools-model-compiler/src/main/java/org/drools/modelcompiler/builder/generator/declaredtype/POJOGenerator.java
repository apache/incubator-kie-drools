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
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.rule.builder.ConstraintBuilder;
import org.drools.core.addon.TypeResolver;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.AccessibleFact;
import org.drools.core.factmodel.GeneratedFact;
import org.drools.modelcompiler.builder.GeneratedClassWithPackage;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.DuplicatedDeclarationError;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration;

import static org.drools.core.util.Drools.hasMvel;
import static org.drools.modelcompiler.builder.JavaParserCompiler.compileAll;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ADD_ANNOTATION_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ANNOTATION_VALUE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.TYPE_META_DATA_CALL;

public class POJOGenerator {

    private final static List<Class<?>> MARKER_INTERFACES = Arrays.asList(GeneratedFact.class, AccessibleFact.class);

    private ModelBuilderImpl builder;
    private InternalKnowledgePackage pkg;
    private PackageDescr packageDescr;
    private PackageModel packageModel;

    private static final List<String> exprAnnotations = Arrays.asList("duration", "timestamp");

    public POJOGenerator(ModelBuilderImpl builder, InternalKnowledgePackage pkg, PackageDescr packageDescr, PackageModel packageModel) {
        this.builder = builder;
        this.pkg = pkg;
        this.packageDescr = packageDescr;
        this.packageModel = packageModel;
    }

    public static Map<String, Class<?>> compileType(KnowledgeBuilderImpl kbuilder,
                                                    ClassLoader packageClassLoader,
                                                    List<GeneratedClassWithPackage> classesWithPackage) {
        return compileAll(kbuilder, packageClassLoader, classesWithPackage);
    }

    public void findPOJOorGenerate() {
        TypeResolver typeResolver = pkg.getTypeResolver();
        Set<String> generatedPojos = new HashSet<>();
        for (TypeDeclarationDescr typeDescr : packageDescr.getTypeDeclarations()) {
            if (!generatedPojos.add(typeDescr.getFullTypeName())) {
                builder.addBuilderResult( new DuplicatedDeclarationError(typeDescr.getFullTypeName()) );
                continue;
            }
            try {
                Class<?> type = typeResolver.resolveType(typeDescr.getFullTypeName());
                checkRedeclarationCompatibility(type, typeDescr);
                processTypeMetadata(type, typeDescr.getAnnotations());
            } catch (ClassNotFoundException e) {
                createPOJO(typeDescr);
            }
        }

        for (EnumDeclarationDescr enumDescr : packageDescr.getEnumDeclarations()) {
            try {
                Class<?> type = typeResolver.resolveType(enumDescr.getFullTypeName());
                processTypeMetadata(type, enumDescr.getAnnotations());
            } catch (ClassNotFoundException e) {
                TypeDeclaration generatedEnum = new EnumGenerator()
                        .generate(enumDescr);
                packageModel.addGeneratedPOJO(generatedEnum);
                addTypeMetadata(enumDescr.getTypeName());
            }
        }
    }

    static class SafeTypeResolver implements org.drools.modelcompiler.builder.generator.declaredtype.api.TypeResolver {

        private final TypeResolver typeResolver;

        public SafeTypeResolver(TypeResolver typeResolver) {
            this.typeResolver = typeResolver;
        }

        @Override
        public Optional<Class<?>> resolveType(String className) {
            try {
                return Optional.ofNullable(this.typeResolver.resolveType(className));
            } catch(ClassNotFoundException e) {
                return Optional.empty();
            }
        }
    }

    private void createPOJO(TypeDeclarationDescr typeDescr) {
        SafeTypeResolver typeResolver = new SafeTypeResolver(pkg.getTypeResolver());
        DescrTypeDefinition descrDeclaredTypeDefinition = new DescrTypeDefinition(packageDescr, typeDescr, typeResolver);
        descrDeclaredTypeDefinition.getErrors().forEach(builder::addBuilderResult);

        // Implemented types should be probably in
        ClassOrInterfaceDeclaration generatedClass = new GeneratedClassDeclaration(descrDeclaredTypeDefinition,
                                                                                   MARKER_INTERFACES)
                .toClassDeclaration();
        packageModel.addGeneratedPOJO(generatedClass);
        addTypeMetadata(typeDescr.getTypeName());
    }

    private void addTypeMetadata(String typeName) {
        packageModel.addTypeMetaDataExpressions(registerTypeMetaData(pkg.getName() + "." + typeName));
    }

    private void checkRedeclarationCompatibility(Class<?> type, TypeDeclarationDescr typeDescr) {
        if (!typeDescr.getFields().isEmpty() && type.getDeclaredFields().length != typeDescr.getFields().size()) {
            builder.addBuilderResult( new InvalidExpressionErrorResult("Wrong redeclaration of type " + typeDescr.getFullTypeName()) );
        }
    }

    private void processTypeMetadata(Class<?> type, Collection<AnnotationDescr> annotations) {
        MethodCallExpr typeMetaDataCall = registerTypeMetaData(type.getCanonicalName());

        for (AnnotationDescr ann : annotations) {
            typeMetaDataCall = new MethodCallExpr(typeMetaDataCall, ADD_ANNOTATION_CALL);
            typeMetaDataCall.addArgument(new StringLiteralExpr(ann.getName()));
            for (Map.Entry<String, Object> entry : ann.getValueMap().entrySet()) {
                MethodCallExpr annotationValueCall = new MethodCallExpr(null, ANNOTATION_VALUE_CALL);
                annotationValueCall.addArgument(new StringLiteralExpr(entry.getKey()));
                String expr = entry.getValue().toString();
                if (hasMvel() && exprAnnotations.contains(ann.getName()) && ConstraintBuilder.get().analyzeExpression(type, expr) == null) {
                    builder.addBuilderResult(new InvalidExpressionErrorResult("Unable to analyze expression '" + expr + "' for " + ann.getName() + " attribute"));
                }
                annotationValueCall.addArgument(quote(expr));
                typeMetaDataCall.addArgument(annotationValueCall);
            }
        }

        packageModel.addTypeMetaDataExpressions(typeMetaDataCall);
    }

    private static MethodCallExpr registerTypeMetaData(String className) {
        MethodCallExpr typeMetaDataCall = new MethodCallExpr(null, TYPE_META_DATA_CALL);
        typeMetaDataCall.addArgument(className + ".class");
        return typeMetaDataCall;
    }

    public static String quote(String str) {
        return "\"" + str.replace("\"", "\\\"") + "\"";
    }
}
