package org.kie.dmn.core.compiler.alphanetbased;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.ancompiler.CanInlineInANC;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.common.PropagationContext;

public class DMNResultCollectorAlphaSink extends LeftInputAdapterNode implements CanInlineInANC<DMNResultCollector> {

    private final int row;
    private final String columnName;
    private final String outputClass;

    public DMNResultCollectorAlphaSink(int id,
                                       ObjectSource source,
                                       BuildContext context,
                                       int row,
                                       String columnName,
                                       String outputClass) {
        super(id, source, context);
        this.row = row;
        this.columnName = columnName;
        this.outputClass = outputClass;
    }

    @Override
    public void assertObject(InternalFactHandle factHandle, PropagationContext propagationContext, ReteEvaluator reteEvaluator) {
        throwDoNotCallException();
    }

    @Override
    public void modifyObject(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, ReteEvaluator reteEvaluator) {
        throwDoNotCallException();
    }

    @Override
    public void byPassModifyToBetaNode(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, ReteEvaluator reteEvaluator) {
        throwDoNotCallException();
    }

    private void throwDoNotCallException() {
        throw new UnsupportedOperationException("This sink will never be called, it'll be inlined as a DMNResultCollector");
    }

    @Override
    public Expression toANCInlinedForm() {
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();

        objectCreationExpr.setType(StaticJavaParser.parseClassOrInterfaceType(DMNResultCollector.class.getCanonicalName()));
        objectCreationExpr.addArgument(new IntegerLiteralExpr(row));
        objectCreationExpr.addArgument(new StringLiteralExpr(columnName));
        objectCreationExpr.addArgument(StaticJavaParser.parseExpression("ctx.getResultCollector()"));

        Expression lambdaExpr = StaticJavaParser.parseExpression(String.format("(org.kie.dmn.feel.lang.EvaluationContext x) -> %s.getInstance().apply(x)", outputClass));
        objectCreationExpr.addArgument(lambdaExpr);

        return objectCreationExpr;
    }

    @Override
    public Class<DMNResultCollector> inlinedType() {
        return DMNResultCollector.class;
    }
}
