package org.drools.model.codegen.execmodel.generator.visitor;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.model.codegen.execmodel.generator.RuleContext;

import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;

public class ConditionalElementVisitor {

    private final RuleContext context;
    private final ModelGeneratorVisitor visitor;

    public ConditionalElementVisitor(ModelGeneratorVisitor visitor, RuleContext context) {
        this.visitor = visitor;
        this.context = context;
    }

    public void visit(ConditionalElementDescr descr, String methodName) {
        final MethodCallExpr ceDSL = createDslTopLevelMethod(methodName);
        this.context.addExpression(ceDSL);
        this.context.pushScope(descr);
        this.context.pushExprPointer(ceDSL::addArgument );
        for (BaseDescr subDescr : descr.getDescrs()) {
            subDescr.accept(visitor);
        }
        this.context.popExprPointer();
        this.context.popScope();
    }

}
