package org.drools.model.codegen.execmodel.generator.operatorspec;

import org.drools.base.base.ValueType;
import org.drools.compiler.rule.builder.EvaluatorDefinition;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.model.functions.Operator;
import org.drools.model.codegen.execmodel.generator.RuleContext;

public class CustomOperatorSpec extends NativeOperatorSpec {
    public static final CustomOperatorSpec INSTANCE = new CustomOperatorSpec();

    @Override
    protected Operator addOperatorArgument( RuleContext context, MethodCallExpr methodCallExpr, String opName ) {
        EvaluatorDefinition evalDef = context.getEvaluatorDefinition( opName );
        if (evalDef == null) {
            throw new RuntimeException( "Unknown custom operator: " + opName );
        }

        String arg = "new " + CustomOperatorWrapper.class.getCanonicalName() + "( new " + evalDef.getClass().getCanonicalName() + "().getEvaluator(" +
                ValueType.class.getCanonicalName() + ".OBJECT_TYPE, \"" + opName + "\", false, null), \"" + opName + "\")";

        methodCallExpr.addArgument( arg );
        return null;
    }
}
