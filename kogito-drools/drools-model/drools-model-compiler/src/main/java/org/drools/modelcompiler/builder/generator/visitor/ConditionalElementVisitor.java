package org.drools.modelcompiler.builder.generator.visitor;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.modelcompiler.builder.generator.RuleContext;

public class ConditionalElementVisitor {

    private final RuleContext context;
    private final ModelGeneratorVisitor visitor;

    public ConditionalElementVisitor(ModelGeneratorVisitor visitor, RuleContext context) {
        this.visitor = visitor;
        this.context = context;
    }

    public void visit(ConditionalElementDescr descr, String methodName) {
        final MethodCallExpr ceDSL = new MethodCallExpr(null, methodName);
        this.context.addExpression(ceDSL);
        this.context.pushExprPointer(ceDSL::addArgument );
        for (BaseDescr subDescr : descr.getDescrs()) {
            subDescr.accept(visitor);
        }
        this.context.popExprPointer();
    }

}
