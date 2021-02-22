package org.kie.dmn.feel.codegen.feel11;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.visitor.ASTHeuristicCheckerVisitor;
import org.kie.dmn.feel.lang.ast.visitor.ASTTemporalConstantVisitor;
import org.kie.dmn.feel.lang.impl.CompiledExecutableExpression;
import org.kie.dmn.feel.lang.impl.CompiledExpressionImpl;
import org.kie.dmn.feel.lang.impl.InterpretedExecutableExpression;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;

import static org.kie.dmn.feel.codegen.feel11.ProcessedFEELUnit.DefaultMode.Compiled;
import static org.kie.dmn.feel.util.ClassLoaderUtil.CAN_PLATFORM_CLASSLOAD;;

public class ProcessedExpression extends ProcessedFEELUnit {

    private static final String TEMPLATE_RESOURCE = "/TemplateCompiledFEELExpression.java";
    private static final String TEMPLATE_CLASS = "TemplateCompiledFEELExpression";

    private final BaseNode ast;
    private final DefaultMode defaultBackend;
    private DirectCompilerResult compiledExpression;

    private final CompilerBytecodeLoader compiler = new CompilerBytecodeLoader();
    private CompiledFEELExpression defaultResult;

    public ProcessedExpression(
            String expression,
            CompilerContext ctx,
            ProcessedFEELUnit.DefaultMode defaultBackend,
            List<FEELProfile> profiles) {

        super(expression, ctx, profiles);
        this.defaultBackend = defaultBackend;
        ParseTree tree = getFEELParser(expression, ctx, profiles).compilation_unit();
        ASTBuilderVisitor astVisitor = new ASTBuilderVisitor(ctx.getInputVariableTypes(), ctx.getFEELFeelTypeRegistry());
        ast = tree.accept(astVisitor);
        if (ast == null) {
            return; // if parsetree/ast is invalid, no need of further processing and early return.
        }
        List<FEELEvent> heuristicChecks = ast.accept(new ASTHeuristicCheckerVisitor());
        if (!heuristicChecks.isEmpty()) {
            for (FEELEventListener listener : ctx.getListeners()) {
                heuristicChecks.forEach(listener::onEvent);
            }
        }
        if (astVisitor.isVisitedTemporalCandidate()) {
            ast.accept(new ASTTemporalConstantVisitor(ctx));
        }
    }

    public CompiledFEELExpression getResult() {
        if (defaultBackend == Compiled) {
            if (CAN_PLATFORM_CLASSLOAD) {
                defaultResult = getCompiled();
            } else {
                throw new UnsupportedOperationException("Cannot jit classload on this platform.");
            }
        } else { // "legacy" interpreted AST compilation:
            defaultResult = getInterpreted();
        }

        return this;
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
                compilerResult.getFieldDeclarations()
        );
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

    @Override
    public Object apply(EvaluationContext evaluationContext) {
        return defaultResult.apply(evaluationContext);
    }
}
