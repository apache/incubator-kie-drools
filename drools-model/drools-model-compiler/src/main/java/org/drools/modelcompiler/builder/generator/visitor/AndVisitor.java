package org.drools.modelcompiler.builder.generator.visitor;

import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.modelcompiler.builder.generator.RuleContext;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.AND_CALL;

public class AndVisitor {

    private final ModelGeneratorVisitor visitor;
    private final RuleContext context;

    public AndVisitor(ModelGeneratorVisitor visitor, RuleContext context) {
        this.visitor = visitor;
        this.context = context;
    }

    public void visit(AndDescr descr) {
        int exprStackSize = this.context.getExprPointerLevel();

        // if it's the first (implied) `and` wrapping the first level of patterns, skip adding it to the DSL.
        if (exprStackSize != 1) {
            final MethodCallExpr andDSL = new MethodCallExpr(null, AND_CALL);
            this.context.addExpression(andDSL);
            this.context.pushExprPointer(andDSL::addArgument);
            exprStackSize++;
        }

        for (BaseDescr subDescr : descr.getDescrs()) {
            this.context.parentDesc = descr;
            subDescr.accept(visitor);
        }

        if (exprStackSize != this.context.getExprPointerLevel()) {
            throw new RuntimeException( "Non paired number of push and pop expression on context stack in " + descr );
        }

        if (exprStackSize != 1) {
            this.context.popExprPointer();
        }
    }
}
