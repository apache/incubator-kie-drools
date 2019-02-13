package org.drools.modelcompiler.builder.generator.visitor;

import org.drools.compiler.lang.descr.EvalDescr;
import com.github.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder;

public class EvalVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;

    public EvalVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public void visit(EvalDescr descr) {
        String expression = descr.getContent().toString();
        DrlxParseResult drlxParseResult = new ConstraintParser(context, packageModel).drlxParse(null, null, expression);

        drlxParseResult.accept(drlxParseSuccess -> {
            SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) drlxParseResult;
            Expression rewriteExprAsLambdaWithoutThisParam = DrlxParseUtil.generateLambdaWithoutParameters(singleResult.getUsedDeclarations(), singleResult.getExpr(), true);
            singleResult.setExpr(rewriteExprAsLambdaWithoutThisParam); // rewrites the DrlxParserResult expr as directly the lambda to use
            singleResult.setStatic(true);
            new FlowExpressionBuilder(context).processExpression(drlxParseSuccess);
        });

    }


}
