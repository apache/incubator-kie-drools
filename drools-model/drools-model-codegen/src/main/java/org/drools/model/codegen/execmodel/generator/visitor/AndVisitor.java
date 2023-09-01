package org.drools.model.codegen.execmodel.generator.visitor;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.model.codegen.execmodel.generator.RuleContext;

import static org.drools.model.codegen.execmodel.generator.DslMethodNames.AND_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;

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
            final MethodCallExpr andDSL = createDslTopLevelMethod(AND_CALL);
            this.context.addExpression(andDSL);
            this.context.pushExprPointer(andDSL::addArgument);
            exprStackSize++;
        }

        this.context.setParentDescr( descr );
        for (BaseDescr subDescr : descr.getDescrs()) {
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
