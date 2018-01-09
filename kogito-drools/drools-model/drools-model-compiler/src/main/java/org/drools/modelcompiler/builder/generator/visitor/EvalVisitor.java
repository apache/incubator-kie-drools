package org.drools.modelcompiler.builder.generator.visitor;

import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;

public class EvalVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;

    public EvalVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public void visit(EvalDescr descr) {
        String expression = descr.getContent().toString();
        DrlxParseResult drlxParseResult = ModelGenerator.drlxParse(context, packageModel, null, null, expression);
        Expression rewriteExprAsLambdaWithoutThisParam = DrlxParseUtil.generateLambdaWithoutParameters(drlxParseResult.getUsedDeclarations(), drlxParseResult.getExpr(), true);
        drlxParseResult.setExpr(rewriteExprAsLambdaWithoutThisParam); // rewrites the DrlxParserResult expr as directly the lambda to use
        drlxParseResult.setStatic(true);
        ModelGenerator.processExpression(context, drlxParseResult);
    }


}
