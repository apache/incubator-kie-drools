package org.kie.dmn.feel.codegen.feel11;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.drools.javaparser.ast.CompilationUnit;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.impl.CompiledExecutableExpression;
import org.kie.dmn.feel.lang.impl.CompiledExpressionImpl;
import org.kie.dmn.feel.lang.impl.InterpretedExecutableExpression;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;

import static org.kie.dmn.feel.codegen.feel11.ProcessedFEELUnit.DefaultMode.Compiled;

public class ProcessedExpression extends ProcessedFEELUnit {

    private static final String TEMPLATE_RESOURCE = "/TemplateCompiledFEELExpression.java";
    private static final String TEMPLATE_CLASS = "TemplateCompiledFEELExpression";

    private final BaseNode ast;
    private DirectCompilerResult compiledExpression;

    private final CompilerBytecodeLoader compiler = new CompilerBytecodeLoader();
    private CompiledFEELExpression defaultResult;

    public ProcessedExpression(
            String expression,
            CompilerContext ctx,
            ProcessedFEELUnit.DefaultMode defaultBackend,
            List<FEELProfile> profiles) {

        super(expression, ctx, profiles);

        ParseTree tree = parser.compilation_unit();
        ast = tree.accept(new ASTBuilderVisitor(ctx.getInputVariableTypes()));

        if (defaultBackend == Compiled) {
            defaultResult = getCompiled();
        } else { // "legacy" interpreted AST compilation:
            defaultResult = getInterpreted();
        }
    }

    private DirectCompilerResult getCompilerResult() {
        if (compiledExpression == null) {
            if (errorListener.isError()) {
                compiledExpression =
                        DirectCompilerResult.of(
                                CompiledFEELSupport.compiledErrorExpression(
                                        errorListener.event().getMessage()),
                                BuiltInType.UNKNOWN);
            } else {
                try {
                    compiledExpression = ast.accept(new ASTCompilerVisitor());
                } catch (FEELCompilationError e) {
                    compiledExpression = DirectCompilerResult.of(
                            CompiledFEELSupport.compiledErrorExpression(e.getMessage()),
                            BuiltInType.UNKNOWN);
                }
            }
        }
        return compiledExpression;
    }

    public CompilationUnit getSourceCode() {
        DirectCompilerResult compilerResult = getCompilerResult();
        return compiler.getCompilationUnit(
                CompiledFEELExpression.class,
                TEMPLATE_RESOURCE,
                packageName,
                TEMPLATE_CLASS,
                expression,
                compilerResult.getExpression(),
                compilerResult.getFieldDeclarations());
    }

    public InterpretedExecutableExpression getInterpreted() {
        return new InterpretedExecutableExpression(new CompiledExpressionImpl(ast));
    }

    public CompiledExecutableExpression getCompiled() {
        CompiledFEELExpression compiledFEELExpression =
                compiler.compileUnit(
                        packageName,
                        TEMPLATE_CLASS,
                        getSourceCode());
        return new CompiledExecutableExpression(compiledFEELExpression);
    }

    public CompiledFEELExpression getCompiledFEELExpression() {
        return defaultResult;
    }

    @Override
    public Object apply(EvaluationContext evaluationContext) {
        return getCompiledFEELExpression().apply(evaluationContext);
    }
}
