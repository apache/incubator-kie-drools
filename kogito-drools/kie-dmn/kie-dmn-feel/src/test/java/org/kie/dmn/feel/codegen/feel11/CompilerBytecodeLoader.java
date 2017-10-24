/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.codegen.feel11;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration.CompilerType;

public class CompilerBytecodeLoader {

    public static class TemplateLoader extends ClassLoader {

        public TemplateLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> load(String name, byte[] b) {
            return defineClass(name,
                               b,
                               0,
                               b.length);
        }

    }

    public CompiledFEELExpression makeFromJPExpression(Expression theExpression) {
        return makeFromJPExpression(null, theExpression, Collections.emptySet());
    }

    public CompiledFEELExpression makeFromJPExpression(String feelExpression, Expression theExpression, Set<FieldDeclaration> fieldDeclarations) {
        CompilationUnit cu = JavaParser.parse(CompilerBytecodeLoader.class.getResourceAsStream("/TemplateCompiledFEELExpression.java"));

        String uuid = UUID.randomUUID().toString().replaceAll("-",
                                                              "");
        String cuPackage = this.getClass().getPackage().getName() + ".gen" + uuid;

        cu.setPackageDeclaration(cuPackage);
        
        List<MethodDeclaration> lookupMethodList = cu.getChildNodesByType(MethodDeclaration.class);
        if (lookupMethodList.size() != 1) {
            throw new RuntimeException("Something unexpected changed in the template.");
        }
        MethodDeclaration lookupMethod = lookupMethodList.get(0);
        lookupMethod.setComment(new JavadocComment("   FEEL: " + feelExpression + "   "));

        List<ReturnStmt> lookupReturnList = cu.getChildNodesByType(ReturnStmt.class);
        if (lookupReturnList.size() != 1) {
            throw new RuntimeException("Something unexpected changed in the template.");
        }
        ReturnStmt returnStmt = lookupReturnList.get(0);
        returnStmt.setExpression(theExpression);
        
        List<ClassOrInterfaceDeclaration> classDecls = cu.getChildNodesByType(ClassOrInterfaceDeclaration.class);
        if (classDecls.size() != 1) {
            throw new RuntimeException("Something unexpected changed in the template.");
        }
        ClassOrInterfaceDeclaration classDecl = classDecls.get(0);
        
        fieldDeclarations.stream().sorted(new SortFieldDeclarationStrategy()).forEach(classDecl::addMember);

        System.out.println(cu);

        try {
            MemoryResourceReader pReader = new MemoryResourceReader();
            pReader.add(cuPackage.replaceAll("\\.", "/") + "/TemplateCompiledFEELExpression.java",
                        cu.toString().getBytes());
            JavaCompiler compiler = new JavaCompilerFactory().loadCompiler(CompilerType.ECLIPSE,
                                                                           "1.8"); // TODO Reminder: using NATIVE causes ClassNotFound over drools-compiler classes?
            MemoryFileSystem pStore = new MemoryFileSystem();
            CompilationResult compilationResult = compiler.compile(new String[]{cuPackage.replaceAll("\\.",
                                                                                                     "/") + "/TemplateCompiledFEELExpression.java"},
                                                                   pReader,
                                                                   pStore);
            System.out.println(Arrays.asList(compilationResult.getErrors()));
            System.out.println(Arrays.asList(compilationResult.getWarnings()));

            byte[] b = pStore.getBytes(cuPackage.replaceAll("\\.",
                                                            "/") + "/TemplateCompiledFEELExpression.class");
            Class<CompiledFEELExpression> loaded = (Class<CompiledFEELExpression>) new TemplateLoader(this.getClass().getClassLoader()).load(cuPackage + ".TemplateCompiledFEELExpression",
                                                                                                                                             b);

            return loaded.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CompiledFEELUnaryTests makeFromJPUnaryTestsExpression(String feelExpression, Expression theExpression, Set<FieldDeclaration> fieldDeclarations) {
        CompilationUnit cu = JavaParser.parse(CompilerBytecodeLoader.class.getResourceAsStream("/TemplateCompiledFEELUnaryTests.java"));

        String uuid = UUID.randomUUID().toString().replaceAll("-",
                                                              "");
        String cuPackage = this.getClass().getPackage().getName() + ".gen" + uuid;

        cu.setPackageDeclaration(cuPackage);

        List<MethodDeclaration> lookupMethodList = cu.getChildNodesByType(MethodDeclaration.class);
        if (lookupMethodList.size() != 1) {
            throw new RuntimeException("Something unexpected changed in the template.");
        }
        MethodDeclaration lookupMethod = lookupMethodList.get(0);
        lookupMethod.setComment(new JavadocComment("   FEEL unary tests: " + feelExpression + "   "));

        List<ReturnStmt> lookupReturnList = cu.getChildNodesByType(ReturnStmt.class);
        if (lookupReturnList.size() != 1) {
            throw new RuntimeException("Something unexpected changed in the template.");
        }
        ReturnStmt returnStmt = lookupReturnList.get(0);
        returnStmt.setExpression(theExpression);

        List<ClassOrInterfaceDeclaration> classDecls = cu.getChildNodesByType(ClassOrInterfaceDeclaration.class);
        if (classDecls.size() != 1) {
            throw new RuntimeException("Something unexpected changed in the template.");
        }
        ClassOrInterfaceDeclaration classDecl = classDecls.get(0);

        fieldDeclarations.stream().sorted(new SortFieldDeclarationStrategy()).forEach(classDecl::addMember);

        System.out.println(cu);

        try {
            MemoryResourceReader pReader = new MemoryResourceReader();
            pReader.add(cuPackage.replaceAll("\\.", "/") + "/TemplateCompiledFEELUnaryTests.java",
                        cu.toString().getBytes());
            JavaCompiler compiler = new JavaCompilerFactory().loadCompiler(CompilerType.ECLIPSE,
                                                                           "1.8"); // TODO Reminder: using NATIVE causes ClassNotFound over drools-compiler classes?
            MemoryFileSystem pStore = new MemoryFileSystem();
            CompilationResult compilationResult = compiler.compile(new String[]{cuPackage.replaceAll("\\.",
                                                                                                     "/") + "/TemplateCompiledFEELUnaryTests.java"},
                                                                   pReader,
                                                                   pStore);
            System.out.println(Arrays.asList(compilationResult.getErrors()));
            System.out.println(Arrays.asList(compilationResult.getWarnings()));

            byte[] b = pStore.getBytes(cuPackage.replaceAll("\\.",
                                                            "/") + "/TemplateCompiledFEELUnaryTests.class");
            Class<CompiledFEELUnaryTests> loaded = (Class<CompiledFEELUnaryTests>) new TemplateLoader(this.getClass().getClassLoader()).load(cuPackage + ".TemplateCompiledFEELUnaryTests",
                                                                                                                                             b);

            return loaded.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class SortFieldDeclarationStrategy implements Comparator<FieldDeclaration> {

        @Override
        public int compare(FieldDeclaration o1, FieldDeclaration o2) {
            String s1 = o1.getVariable(0).getNameAsString();
            String s2 = o2.getVariable(0).getNameAsString();
            return s1.compareTo(s2);
        }

    }
}
