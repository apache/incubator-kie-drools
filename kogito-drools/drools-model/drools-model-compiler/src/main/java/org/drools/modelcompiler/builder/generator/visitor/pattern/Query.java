package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.List;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.QueryGenerator;
import org.drools.modelcompiler.builder.generator.QueryParameter;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.ModelGenerator.QUERY_INVOCATION_CALL;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.VALUE_OF_CALL;
import static org.drools.modelcompiler.builder.generator.QueryGenerator.toQueryDef;

class Query implements DSLNode {

    private final RuleContext context;
    private final PackageModel packageModel;
    private PatternDescr pattern;
    private List<? extends BaseDescr> constraintDescrs;
    private String queryName;

    public Query(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, String queryName) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.constraintDescrs = constraintDescrs;
        this.queryName = queryName;
    }

    @Override
    public void buildPattern() {
        NameExpr queryCall = new NameExpr(toQueryDef(pattern.getObjectType()));
        MethodCallExpr callCall = new MethodCallExpr(queryCall, QUERY_INVOCATION_CALL);
        callCall.addArgument("" + !pattern.isQuery());

        for (int i = 0; i < constraintDescrs.size(); i++) {
            String itemText = constraintDescrs.get(i).getText();
            if (QueryGenerator.isLiteral(itemText)) {
                MethodCallExpr valueOfMethod = new MethodCallExpr(null, VALUE_OF_CALL);
                valueOfMethod.addArgument(new NameExpr(itemText));
                callCall.addArgument(valueOfMethod);
            } else {
                QueryParameter qp = packageModel.queryVariables(queryName).get(i);
                context.addDeclaration(new DeclarationSpec(itemText, qp.getType()));
                callCall.addArgument(QueryGenerator.substituteBindingWithQueryParameter(context, itemText));
            }
        }

        context.addExpression(callCall);
    }
}
