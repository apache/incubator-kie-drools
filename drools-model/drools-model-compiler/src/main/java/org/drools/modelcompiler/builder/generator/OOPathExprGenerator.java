package org.drools.modelcompiler.builder.generator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.javaparser.ast.drlx.OOPathChunk;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.BooleanLiteralExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;

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

    public void visit(Class<?> originalClass, String originalBind, OOPathExpr ooPathExpr) {

        Class<?> previousClass = originalClass;
        String previousBind = originalBind;

        Map<String, List<DrlxParseResult>> ooPathConditionExpressions = new LinkedHashMap<>();

        for (OOPathChunk chunk : ooPathExpr.getChunks()) {

            final String fieldName = chunk.getField().toString();

            final TypedExpression callExpr = DrlxParseUtil.nameExprToMethodCallExpr(fieldName, previousClass, null);
            Class<?> fieldType = (chunk.getInlineCast() != null)
                    ? DrlxParseUtil.getClassFromContext(context.getPkg().getTypeResolver(), chunk.getInlineCast().toString())
                    : callExpr.getType();

            if (Iterable.class.isAssignableFrom(fieldType)) {
                fieldType = extractGenericType(previousClass, ((MethodCallExpr) callExpr.getExpression()).getName().toString());
            }

            final String chunkKey = originalBind + fieldName;
            final String bindingId = context.getOOPathId(fieldType, chunkKey);
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
                final List<DrlxParseResult> conditionParseResult = conditions.stream().map((Expression c) -> {
                    return ModelGenerator
                            .drlxParse(context, packageModel, finalFieldType, bindingId, c.toString());
                }).collect(Collectors.toList());
                ooPathConditionExpressions.put(chunkKey, conditionParseResult);
            } else {
                final DrlxParseResult drlxParseResult = new DrlxParseResult(fieldType, "", bindingId, new BooleanLiteralExpr(true), fieldType);
                ooPathConditionExpressions.put(chunkKey, Collections.singletonList(drlxParseResult));
            }

            previousBind = bindingId;
            previousClass = fieldType;
        }

        inferTypeForInnerBinding(ooPathConditionExpressions);
        final List<Expression> collect = buildExpressions(ooPathConditionExpressions);

        collect.forEach(context::addExpression);
    }

    private List<Expression> buildExpressions(Map<String, List<DrlxParseResult>> ooPathConditionExpressions) {
        // Condition with same key were defined as , and need to be put in an AND expression
        return ooPathConditionExpressions.entrySet().stream()
                .map((Map.Entry<String, List<DrlxParseResult>> kv) -> {
                    final List<DrlxParseResult> value = kv.getValue();
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

    private void inferTypeForInnerBinding(Map<String, List<DrlxParseResult>> ooPathConditionExpressions) {
        // If the OOPath has an inner binding, it will be in the context's declarations without its type (as it's inferred from the last OOPath chunk).
        // We remove the original expression without type and use its name in the last expression
        List<DeclarationSpec> declarations = context.getDeclarations();
        final Optional<DeclarationSpec> missingClassDeclarationFound = declarations.stream().filter(d -> d.getDeclarationClass() == null).findFirst();

        missingClassDeclarationFound.ifPresent(missingClassDeclaration -> {
            declarations.remove(declarations.indexOf(missingClassDeclaration));

            final String innerBindingId = missingClassDeclaration.getBindingId();
            final int lastIndex = declarations.size() - 1;
            final DeclarationSpec last = declarations.get(lastIndex);
            declarations.add(new DeclarationSpec(innerBindingId, last.getDeclarationClass(), last.getOptPattern(), last.getDeclarationSource()));

            // In the meanwhile some condition could have used that binding, we need to rename that also
            final DrlxParseResult[] flattenedCollection =
                    ooPathConditionExpressions.values().stream().flatMap(Collection::stream).toArray(DrlxParseResult[]::new);
            for (DrlxParseResult r : flattenedCollection) {
                if (r.getExprId().equals(last.getBindingId())) {
                    r.setExprId(innerBindingId);
                } else if (r.getPatternBinding().equals(last.getBindingId())) {
                    r.setPatternBinding(innerBindingId);
                }
            }
        });
    }
}
