package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.QueryGenerator;
import org.drools.modelcompiler.builder.generator.QueryParameter;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.QUERY_INVOCATION_CALL;

class QueryCall implements DSLNode {

    private final RuleContext context;
    private final PackageModel packageModel;
    private PatternDescr pattern;
    private final String queryDef;

    QueryCall(RuleContext context, PackageModel packageModel, PatternDescr pattern, String queryDef) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.queryDef = queryDef;
    }

    @Override
    public void buildPattern() {
        MethodCallExpr callMethod = new MethodCallExpr(new NameExpr(queryDef), QUERY_INVOCATION_CALL);
        callMethod.addArgument("" + !pattern.isQuery());

        List<QueryParameter> parameters = packageModel.getQueryDefWithType().get(queryDef).getContext().getQueryParameters();
        for (int i = 0; i < parameters.size(); i++) {
            String queryName = context.getQueryName().orElseThrow(RuntimeException::new);
            ExprConstraintDescr variableExpr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(i);
            String variableName = variableExpr.toString();
            int unifPos = variableName.indexOf( ":=" );
            if (unifPos > 0) {
                variableName = variableName.substring( 0, unifPos ).trim();
            }
            Optional<String> unificationId = context.getUnificationId(variableName);
            int queryIndex = i + 1;
            Expression parameterCall = unificationId.map(name -> (Expression) new NameExpr(toVar(name)))
                    .orElseGet(() -> new MethodCallExpr(new NameExpr(queryName), QueryGenerator.toQueryArg(queryIndex)));
            callMethod.addArgument(parameterCall);
        }

        context.addExpression(callMethod);
    }
}
