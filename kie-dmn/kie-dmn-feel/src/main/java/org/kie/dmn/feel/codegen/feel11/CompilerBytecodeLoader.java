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
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.dmn.feel.util.ClassLoaderUtil;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.resources.MemoryResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.drools.compiler.compiler.JavaDialectConfiguration.createNativeCompiler;

public class CompilerBytecodeLoader {

    private static final Logger LOG = LoggerFactory.getLogger(CompilerBytecodeLoader.class);

    public interface GenerateClassListener {
        void generatedClass(CompilationUnit cu);
    }

    public static GenerateClassListener generateClassListener;

    public static class TemplateLoader extends ClassLoader {

        public TemplateLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> load(String name, byte[] b) {
            if (!ClassLoaderUtil.CAN_PLATFORM_CLASSLOAD) {
                throw new UnsupportedOperationException("Cannot jit classload on this platform.");
            }
            return defineClass(name, b, 0, b.length);
        }

        public Class<?> load(MemoryFileSystem pStore, String string) {
            if (!ClassLoaderUtil.CAN_PLATFORM_CLASSLOAD) {
                throw new UnsupportedOperationException("Cannot jit classload on this platform.");
            }
            Class<?> loadedClass = null;
            for (Entry<String, byte[]> kv : pStore.getMap().entrySet() ) {
                final String className = kv.getKey().substring(0, kv.getKey().lastIndexOf(".class")).replaceAll("/", ".");
                final Class<?> definedClass = defineClass(className, kv.getValue(), 0, kv.getValue().length);
                if (string.equals(className)) {
                    loadedClass = definedClass;
                }
            }
            return loadedClass;
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

    public <T> T internal_makefromJP(Class<T> clazz, String templateResourcePath, String cuPackage, String cuClass, String feelExpression, Expression theExpression, Set<FieldDeclaration> fieldDeclarations) {
        CompilationUnit cu = getCompilationUnit(clazz, templateResourcePath, cuPackage, cuClass, feelExpression, theExpression, fieldDeclarations);
        return compileUnit(cuPackage, cuClass, cu);
    }

    public  <T> T compileUnit(String cuPackage, String cuClass, CompilationUnit cu) {
        try {
            MemoryResourceReader pReader = new MemoryResourceReader();
            pReader.add(cuPackage.replaceAll("\\.", "/") + "/" + cuClass + ".java", cu.toString().getBytes());
            JavaCompiler compiler = createNativeCompiler();
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
        CompilationUnit cu = getCompilationUnit(CompiledFEELUnaryTests.class, "/TemplateCompiledFEELUnaryTests.java", packageName, className, feelExpression, theExpression, fieldDeclarations);
        ClassOrInterfaceDeclaration classSource = cu.getClassByName( className ).orElseThrow(() -> new IllegalArgumentException("Cannot find class by name " + className));
        classSource.setStatic( true );
        return classSource.toString();
    }

    public <T> CompilationUnit getCompilationUnit(Class<T> clazz, String templateResourcePath, String cuPackage, String cuClass, String feelExpression, Expression theExpression, Set<FieldDeclaration> fieldDeclarations) {
        CompilationUnit cu = parse(CompilerBytecodeLoader.class.getResourceAsStream(templateResourcePath));
        cu.setPackageDeclaration(cuPackage);
        final String className = templateResourcePath.substring( 1, templateResourcePath.length() - 5);
        ClassOrInterfaceDeclaration classSource = cu.getClassByName(className).orElseThrow(() -> new IllegalArgumentException("Cannot find class by name " + className));
        classSource.setName( cuClass );

        MethodDeclaration lookupMethod = cu
                .findFirst(MethodDeclaration.class)
                .orElseThrow(() -> new RuntimeException("Something unexpected changed in the template."));

        lookupMethod.setComment(new JavadocComment("   FEEL: " + feelExpression + "   "));

        ReturnStmt returnStmt =
                lookupMethod.findFirst(ReturnStmt.class)
                .orElseThrow(() -> new RuntimeException("Something unexpected changed in the template."));

        Expression expr;
        if (clazz.equals(CompiledFEELUnaryTests.class)) {
            expr = new CastExpr(StaticJavaParser.parseType("java.util.List"), new EnclosedExpr(theExpression));
        } else {
            expr = theExpression;
        }
        returnStmt.setExpression(expr);

        List<ClassOrInterfaceDeclaration> classDecls = cu.findAll(ClassOrInterfaceDeclaration.class);
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

        if (generateClassListener != null) {
            generateClassListener.generatedClass(cu);
        }

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
