package org.drools.model.codegen.execmodel.generator.visitor;

import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import org.drools.drl.ast.descr.EvalDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.DrlxParseUtil;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.drlxparse.ConstraintParser;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseResult;
import org.drools.model.codegen.execmodel.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.expression.EvalExpressionBuilder;

public class EvalVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;

    public EvalVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public void visit(EvalDescr descr) {
        String expression = descr.getContent().toString();
        DrlxParseResult drlxParseResult = ConstraintParser.withoutVariableValidationConstraintParser(context, packageModel)
                .drlxParse(null, null, expression);

        drlxParseResult.accept(drlxParseSuccess -> {
            SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) drlxParseResult;
            Expression rewriteExprAsLambdaWithoutThisParam = DrlxParseUtil.generateLambdaWithoutParameters(singleResult.getUsedDeclarations(), singleResult.getExpr(), true, Optional.empty(), context);
            singleResult.setExpr(rewriteExprAsLambdaWithoutThisParam); // rewrites the DrlxParserResult expr as directly the lambda to use
            singleResult.setStatic(true);
            new EvalExpressionBuilder(context).processExpression(singleResult);
        });

    }
}
