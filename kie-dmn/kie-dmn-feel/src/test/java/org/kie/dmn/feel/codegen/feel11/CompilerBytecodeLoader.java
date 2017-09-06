package org.kie.dmn.feel.codegen.feel11;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.comments.LineComment;
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
        
        List<ReturnStmt> lookupReturnList = cu.getChildNodesByType(ReturnStmt.class);
        if (lookupReturnList.size() != 1) {
            throw new RuntimeException("Something unexpected changed in the template.");
        }
        ReturnStmt returnStmt = lookupReturnList.get(0);
        returnStmt.setExpression(theExpression);
        returnStmt.setComment(new LineComment(" FEEL: " + feelExpression));
        
        List<ClassOrInterfaceDeclaration> classDecls = cu.getChildNodesByType(ClassOrInterfaceDeclaration.class);
        if (classDecls.size() != 1) {
            throw new RuntimeException("Something unexpected changed in the template.");
        }
        ClassOrInterfaceDeclaration classDecl = classDecls.get(0);
        fieldDeclarations.forEach(classDecl::addMember);
        
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
}
