package org.drools.modelcompiler.builder.generator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.javaparser.ast.drlx.OOPathChunk;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.BooleanLiteralExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;

import static org.drools.core.util.ClassUtils.extractGenericType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.prepend;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.buildExpressionWithIndexing;

public class OOPathExprGenerator {

    private final RuleContext context;
    private final PackageModel packageModel;

    public OOPathExprGenerator(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
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
            Class<?> fieldType = (chunk.getInlineCast() != null)
                    ? DrlxParseUtil.getClassFromContext(context.getTypeResolver(), chunk.getInlineCast().toString())
                    : callExpr.getType();

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
                                                                              prepend(new NameExpr("_this"), callExpr.getExpression()));

            final MethodCallExpr reactiveFrom = new MethodCallExpr(null, "reactiveFrom");
            reactiveFrom.addArgument(new NameExpr(toVar(previousBind)));
            reactiveFrom.addArgument(accessorLambda);

            DeclarationSpec newDeclaration = new DeclarationSpec(bindingId, fieldType, reactiveFrom);
            context.addDeclaration(newDeclaration);
            context.addOOPathDeclaration(newDeclaration);

            final List<Expression> conditions = chunk.getConditions();
            if (!conditions.isEmpty()) {
                Class<?> finalFieldType = fieldType;
                final List<DrlxParseResult> conditionParseResult = conditions.stream().map((Expression c) ->
                                                                                                   new ConstraintParser(context, packageModel).drlxParse(finalFieldType, bindingId, c.toString())
                ).collect(Collectors.toList());
                ooPathConditionExpressions.put(chunkKey, conditionParseResult);
            } else {
                final DrlxParseSuccess drlxParseResult = new DrlxParseSuccess(fieldType, "", bindingId, new BooleanLiteralExpr(true), fieldType);
                ooPathConditionExpressions.put(chunkKey, Collections.singletonList(drlxParseResult));
            }

            previousBind = bindingId;
            previousClass = fieldType;
        }

        final List<Expression> collect = buildExpressions(ooPathConditionExpressions);

        collect.forEach(context::addExpression);
    }

    private List<Expression> buildExpressions(Map<String, List<DrlxParseResult>> ooPathConditionExpressions) {
        // Condition with same key were defined as , and need to be put in an AND expression
        return ooPathConditionExpressions.entrySet().stream()
                .map((Map.Entry<String, List<DrlxParseResult>> kv) -> {
                    final List<DrlxParseSuccess> value = kv.getValue()
                            .stream()
                            .filter(r -> r instanceof DrlxParseSuccess)
                            .map(x -> (DrlxParseSuccess)x)
                            .collect(Collectors.toList());
                    if (value.size() == 1) {
                        return buildExpressionWithIndexing(context, value.get(0));
                    } else {
                        final MethodCallExpr andDSL = new MethodCallExpr(null, "and");
                        value.forEach(e -> {
                            final Expression expression = buildExpressionWithIndexing(context, e);
                            andDSL.addArgument(expression);
                        });
                        return andDSL;
                    }
                }).collect(Collectors.toList());
    }
}
