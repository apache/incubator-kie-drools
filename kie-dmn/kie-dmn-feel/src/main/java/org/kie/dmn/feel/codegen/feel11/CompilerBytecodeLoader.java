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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration.CompilerType;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.comments.JavadocComment;
import org.drools.javaparser.ast.expr.CastExpr;
import org.drools.javaparser.ast.expr.EnclosedExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.stmt.ReturnStmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompilerBytecodeLoader {

    private static final Logger LOG = LoggerFactory.getLogger(CompilerBytecodeLoader.class);

    public static class TemplateLoader extends ClassLoader {

        public TemplateLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> load(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }

        public Class<?> load(MemoryFileSystem pStore, String string) {
            Map<String, Class<?>> loaded = new HashMap<>();
            for (Entry<String, byte[]> kv : pStore.getMap().entrySet() ) {
                String className = kv.getKey().substring(0, kv.getKey().lastIndexOf(".class")).replaceAll("/", ".");
                loaded.put(className, defineClass(className, kv.getValue(), 0, kv.getValue().length));
            }
            return (Class<?>) loaded.get(string);
        }

    }

    public CompiledFEELExpression makeFromJPExpression(Expression theExpression) {
        return makeFromJPExpression(null, theExpression, Collections.emptySet());
    }

    public CompiledFEELExpression makeFromJPExpression(String feelExpression, Expression theExpression, Set<FieldDeclaration> fieldDeclarations) {
        return internal_makefromJP(CompiledFEELExpression.class, "/TemplateCompiledFEELExpression.java", generateRandomPackage(), "TemplateCompiledFEELExpression", feelExpression, theExpression, fieldDeclarations);
    }

    public CompiledFEELUnaryTests makeFromJPUnaryTestsExpression(String feelExpression, Expression theExpression, Set<FieldDeclaration> fieldDeclarations) {
        return internal_makefromJP(CompiledFEELUnaryTests.class, "/TemplateCompiledFEELUnaryTests.java", generateRandomPackage(), "TemplateCompiledFEELUnaryTests", feelExpression, theExpression, fieldDeclarations);
    }

    public CompiledFEELUnaryTests makeFromJPUnaryTestsExpression(String packageName, String className, String feelExpression, Expression theExpression, Set<FieldDeclaration> fieldDeclarations) {
        return internal_makefromJP(CompiledFEELUnaryTests.class, "/TemplateCompiledFEELUnaryTests.java", packageName, className, feelExpression, theExpression, fieldDeclarations);
    }

    private <T> T internal_makefromJP(Class<T> clazz, String templateResourcePath, String cuPackage, String cuClass, String feelExpression, Expression theExpression, Set<FieldDeclaration> fieldDeclarations) {
        CompilationUnit cu = getCompilationUnitForUnaryTests( clazz, templateResourcePath, cuPackage, cuClass, feelExpression, theExpression, fieldDeclarations );

        try {
            MemoryResourceReader pReader = new MemoryResourceReader();
            pReader.add(cuPackage.replaceAll("\\.", "/") + "/" + cuClass + ".java", cu.toString().getBytes());
            JavaCompiler compiler = new JavaCompilerFactory().loadCompiler(CompilerType.ECLIPSE, "1.8");
            MemoryFileSystem pStore = new MemoryFileSystem();
            CompilationResult compilationResult = compiler.compile(new String[]{cuPackage.replaceAll("\\.", "/") + "/" + cuClass + ".java"},
                                                                   pReader,
                                                                   pStore,
                                                                   this.getClass().getClassLoader());
            LOG.debug("{}", Arrays.asList(compilationResult.getErrors()));
            LOG.debug("{}", Arrays.asList(compilationResult.getWarnings()));

            String fqnClassName = cuPackage + "." + cuClass;
            Class<T> loaded = (Class<T>) new TemplateLoader(this.getClass().getClassLoader()).load(pStore, fqnClassName);

            return loaded.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSourceForUnaryTest(String packageName, String className, String feelExpression, DirectCompilerResult directResult) {
        return getSourceForUnaryTest(packageName, className, feelExpression, directResult.getExpression(), directResult.getFieldDeclarations());
    }

    public String getSourceForUnaryTest(String packageName, String className, String feelExpression, Expression theExpression, Set<FieldDeclaration> fieldDeclarations) {
        CompilationUnit cu = getCompilationUnitForUnaryTests( CompiledFEELUnaryTests.class, "/TemplateCompiledFEELUnaryTests.java", packageName, className, feelExpression, theExpression, fieldDeclarations );
        ClassOrInterfaceDeclaration classSource = cu.getClassByName( className ).get();
        classSource.setStatic( true );
        return classSource.toString();
    }

    private <T> CompilationUnit getCompilationUnitForUnaryTests( Class<T> clazz, String templateResourcePath, String cuPackage, String cuClass, String feelExpression, Expression theExpression, Set<FieldDeclaration> fieldDeclarations ) {
        CompilationUnit cu = JavaParser.parse(CompilerBytecodeLoader.class.getResourceAsStream(templateResourcePath));
        cu.setPackageDeclaration(cuPackage);
        ClassOrInterfaceDeclaration classSource = cu.getClassByName( templateResourcePath.substring( 1, templateResourcePath.length()-5 ) ).get();
        classSource.setName( cuClass );

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
        Expression expr;
        if (clazz.equals(CompiledFEELUnaryTests.class)) {
            expr = new CastExpr(JavaParser.parseType("java.util.List"), new EnclosedExpr(theExpression));
        } else {
            expr = theExpression;
        }
        returnStmt.setExpression(expr);

        List<ClassOrInterfaceDeclaration> classDecls = cu.getChildNodesByType(ClassOrInterfaceDeclaration.class);
        if (classDecls.size() != 1) {
            throw new RuntimeException("Something unexpected changed in the template.");
        }
        ClassOrInterfaceDeclaration classDecl = classDecls.get(0);

        fieldDeclarations.stream()
                .filter(fd -> !isUnaryTest(fd))
                .sorted(new SortFieldDeclarationStrategy()).forEach(classDecl::addMember);
        fieldDeclarations.stream()
                .filter(fd -> fd.getVariable(0).getName().asString().startsWith("UT"))
                .sorted(new SortFieldDeclarationStrategy()).forEach(classDecl::addMember);

        LOG.debug("{}", cu);
        return cu;
    }

    private boolean isUnaryTest(FieldDeclaration fd) {
        return fd.getVariable(0).getName().asString().startsWith("UT");
    }

    private String generateRandomPackage() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return this.getClass().getPackage().getName() + ".gen" + uuid;
    }

    private static class SortFieldDeclarationStrategy implements Comparator<FieldDeclaration> {

        @Override
        public int compare(FieldDeclaration o1, FieldDeclaration o2) {
            String s1 = o1.getVariable(0).getNameAsString();
            String s2 = o2.getVariable(0).getNameAsString();
            // heuristic to sort longest field names at the bottom.
            // Should be substituted with proper dependency tracking
            return s1.length() < 5 && s2.length() < 5 ? s1.compareTo(s2) : s1.length() - s2.length() ;
        }

    }
}
