package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;

import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceSimpleNameWith;

public class UnaryTestClass {
    private final String input;
    private final DMNFEELHelper feel;
    private final CompilerContext compilerContext;
    private final Type type;

    public UnaryTestClass(String input,
                          DMNFEELHelper feel,
                          CompilerContext compilerContext,
                          Type type) {
        this.input = input;
        this.feel = feel;
        this.compilerContext = compilerContext;
        this.type = type;
    }

    public void compileUnaryTestAndAddTo(Map<String, String> allGeneratedSources,
                                         String className,
                                         String classNameWithPackage,
                                         String packageName) {
        ClassOrInterfaceDeclaration sourceCode = feel.generateUnaryTestsSource(
                compilerContext,
                input,
                type,
                false);

        replaceSimpleNameWith(sourceCode, "TemplateCompiledFEELUnaryTests", className);

        sourceCode.setName(className);

        CompilationUnit cu = new CompilationUnit(packageName);
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = cu.addClass(className);
        classOrInterfaceDeclaration.replace(sourceCode);

        allGeneratedSources.put(classNameWithPackage, cu.toString());
    }
}
