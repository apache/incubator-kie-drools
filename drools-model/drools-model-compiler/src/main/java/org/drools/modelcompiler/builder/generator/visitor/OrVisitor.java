package org.drools.modelcompiler.builder.generator.visitor;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.modelcompiler.builder.generator.RuleContext;

public class OrVisitor {

    final ModelGeneratorVisitor modelGeneratorVisitor;
    final RuleContext context;

    public OrVisitor(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context) {
        this.modelGeneratorVisitor = modelGeneratorVisitor;
        this.context = context;
    }

    public void visit(ConditionalElementDescr descr, String methodName) {
        final MethodCallExpr ceDSL = new MethodCallExpr(null, methodName);
        context.addExpression(ceDSL);

        for (BaseDescr subDescr : descr.getDescrs()) {
            final MethodCallExpr andDSL = new MethodCallExpr(null, "and");
            context.pushExprPointer(andDSL::addArgument);
            subDescr.accept(modelGeneratorVisitor);
            context.popExprPointer();
            ceDSL.addArgument( andDSL );
        }
    }
}
