package org.drools.model.codegen.execmodel.generator.visitor.pattern;

import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.QueryGenerator;
import org.drools.model.codegen.execmodel.generator.QueryParameter;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.visitor.DSLNode;

import static org.drools.model.codegen.execmodel.generator.DslMethodNames.QUERY_INVOCATION_CALL;

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

        String queryName = context.getQueryName().orElseThrow(RuntimeException::new);
        List<QueryParameter> parameters = packageModel.getQueryDefWithType().get(queryDef).getContext().getQueryParameters();
        for (int i = 0; i < parameters.size(); i++) {
            String variableName = getVariableName(i);
            Expression parameter = context.getQueryParameterByName(variableName)
                    .map(qp -> (Expression) new MethodCallExpr(new NameExpr(queryName), QueryGenerator.toQueryArg(qp.getIndex())))
                    .orElseGet(() -> context.getUnificationId(variableName)
                            .map(name -> context.getVarExpr(name))
                            .orElseGet(() -> context.getVarExpr(variableName)));
            callMethod.addArgument(parameter);
        }

        context.addExpression(callMethod);
    }

    private String getVariableName(int i) {
        ExprConstraintDescr variableExpr = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(i);
        String variableName = variableExpr.toString();
        int unifPos = variableName.indexOf( ":=" );
        if (unifPos > 0) {
            variableName = variableName.substring( 0, unifPos ).trim();
        }
        return variableName;
    }
}
