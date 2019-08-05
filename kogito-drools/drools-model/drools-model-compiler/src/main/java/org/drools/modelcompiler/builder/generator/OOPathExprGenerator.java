package org.drools.modelcompiler.builder.generator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.mvel.parser.ast.expr.OOPathChunk;
import org.drools.mvel.parser.ast.expr.OOPathExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.mvel.parser.printer.PrintUtil;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.expression.AbstractExpressionBuilder;

import static org.drools.core.util.ClassUtils.extractGenericType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.prepend;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.AND_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.PATTERN_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.REACTIVE_FROM_CALL;

public class OOPathExprGenerator {

    private final RuleContext context;
    private final PackageModel packageModel;
    private final AbstractExpressionBuilder expressionBuilder;

    public OOPathExprGenerator(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
        this.expressionBuilder = AbstractExpressionBuilder.getExpressionBuilder(context);
    }

    public void visit(Class<?> originalClass, String originalBind, DrlxParseSuccess patternParseResult) {

        final OOPathExpr ooPathExpr = (OOPathExpr) patternParseResult.getExpr();

        Class<?> previousClass = originalClass;
        String previousBind = originalBind;

        Map<String, List<DrlxParseResult>> ooPathConditionExpressions = new LinkedHashMap<>();

        for (Iterator<OOPathChunk> iterator = ooPathExpr.getChunks().iterator(); iterator.hasNext(); ) {
            OOPathChunk chunk = iterator.next();

            final String fieldName = chunk.getField().toString();

            final TypedExpression callExpr = DrlxParseUtil.nameExprToMethodCallExpr(fieldName, previousClass, null);
            if (callExpr == null) {
                context.addCompilationError( new InvalidExpressionErrorResult( "Unknown field " + fieldName + " on " + previousClass ) );
                break;
            }
            Class<?> fieldType = (chunk.getInlineCast() != null)
                    ? DrlxParseUtil.getClassFromContext(context.getTypeResolver(), chunk.getInlineCast().toString())
                    : callExpr.getRawClass();

            if (Iterable.class.isAssignableFrom(fieldType)) {
                fieldType = extractGenericType(previousClass, ((MethodCallExpr) callExpr.getExpression()).getName().toString());
            }

            final String chunkKey = originalBind + fieldName;
            final String bindingId;
            if (!iterator.hasNext() && patternParseResult.getExprBinding() != null) {
                bindingId = patternParseResult.getExprBinding();
                context.removeDeclarationById(bindingId);
            } else {
                bindingId = context.getOOPathId(fieldType, chunkKey);
            }
            final Expression accessorLambda = generateLambdaWithoutParameters(Collections.emptySortedSet(),
                                                                              prepend(new NameExpr(THIS_PLACEHOLDER), callExpr.getExpression()));

            final MethodCallExpr reactiveFrom = new MethodCallExpr(null, REACTIVE_FROM_CALL);
            reactiveFrom.addArgument(context.getVarExpr(previousBind));
            reactiveFrom.addArgument(accessorLambda);

            DeclarationSpec newDeclaration = context.addDeclaration(bindingId, fieldType, reactiveFrom);
            context.addOOPathDeclaration(newDeclaration);

            final List<Expression> conditions = chunk.getConditions();
            if (!conditions.isEmpty()) {
                Class<?> finalFieldType = fieldType;
                final List<DrlxParseResult> conditionParseResult = conditions.stream().map((Expression c) ->
                                                                                                   new ConstraintParser(context, packageModel).drlxParse(finalFieldType, bindingId, PrintUtil.printConstraint(c))
                ).collect(Collectors.toList());
                ooPathConditionExpressions.put(bindingId, conditionParseResult);
            } else {
                if (context.isPatternDSL()) {
                    ooPathConditionExpressions.put(bindingId, Collections.emptyList());
                } else {
                    final DrlxParseSuccess drlxParseResult = new SingleDrlxParseSuccess(fieldType, "", bindingId, new BooleanLiteralExpr(true), fieldType);
                    ooPathConditionExpressions.put(bindingId, Collections.singletonList(drlxParseResult));
                }
            }

            previousBind = bindingId;
            previousClass = fieldType;
        }

        ooPathConditionExpressions.forEach( context.isPatternDSL() ? this::toPatternExpr : this::toFlowExpr );
    }

    private void toPatternExpr(String bindingId, List<DrlxParseResult> list) {
        MethodCallExpr patternExpr = new MethodCallExpr( null, PATTERN_CALL );
        patternExpr.addArgument( context.getVar( bindingId ) );

        for (DrlxParseResult drlx : list) {
            if (drlx.isSuccess()) {
                MethodCallExpr expr = ( MethodCallExpr ) expressionBuilder.buildExpressionWithIndexing( (( DrlxParseSuccess ) drlx) );
                expr.setScope( patternExpr );
                patternExpr = expr;
            }
        }

        context.addExpression( patternExpr );
    }

    private void toFlowExpr(String bindingId, List<DrlxParseResult> list) {
        final List<DrlxParseSuccess> value = list.stream()
                .filter(DrlxParseResult::isSuccess)
                .map(DrlxParseSuccess.class::cast)
                .collect(Collectors.toList());

        if (value.size() == 1) {
            context.addExpression( expressionBuilder.buildExpressionWithIndexing(value.get(0)) );
        } else {
            final MethodCallExpr andDSL = new MethodCallExpr(null, AND_CALL);
            value.forEach(e -> {
                final Expression expression = expressionBuilder.buildExpressionWithIndexing(e);
                andDSL.addArgument(expression);
            });
            context.addExpression( andDSL );
        }
    }
}
