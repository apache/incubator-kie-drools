package org.drools.mvelcompiler;

import com.github.javaparser.ast.expr.Expression;
import org.drools.mvel.parser.MvelParser;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

/* A special case of compiler in that compiles constraints, that is
    every variable can be implicitly a field of the root object
    no LHS
    converted FieldToAccessor prepend a this expr
 */
public class ConstraintCompiler {

    private final MvelCompilerContext mvelCompilerContext;

    public ConstraintCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public CompiledExpressionResult compileExpression(String mvelExpressionString) {
        Expression parsedExpression = MvelParser.parseExpression(mvelExpressionString);
        return compileExpression(parsedExpression);
    }

    public CompiledExpressionResult compileExpression(Expression parsedExpression) {
        // Avoid processing the LHS as it's not present while compiling an expression
        TypedExpression compiled = new RHSPhase(mvelCompilerContext).invoke(parsedExpression);

        Expression expression = (Expression) compiled.toJavaExpression();

        return new CompiledExpressionResult(expression, compiled.getType())
                .setUsedBindings(mvelCompilerContext.getUsedBindings());
    }
}
