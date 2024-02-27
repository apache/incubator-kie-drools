/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.drlxparse.ConstraintParser;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseResult;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.expression.AbstractExpressionBuilder;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.ast.expr.OOPathChunk;
import org.drools.mvel.parser.ast.expr.OOPathExpr;
import org.drools.mvel.parser.printer.PrintUtil;

import static org.drools.util.ClassUtils.extractGenericType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.prepend;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.FROM_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PATTERN_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.REACTIVE_FROM_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;
import static org.kie.internal.ruleunit.RuleUnitUtil.isDataSource;

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

        NodeList<OOPathChunk> chunks = ooPathExpr.getChunks();
        boolean passive = false;
        for (int i = 0; i < chunks.size(); i++) {
            OOPathChunk chunk = chunks.get(i);
            if (chunk.isPassive()) {
                if (passive) {
                    context.addCompilationError(new InvalidExpressionErrorResult("Invalid oopath expression '" + PrintUtil.printNode(ooPathExpr) + "': It is not possible to have 2 non-reactive parts in the same oopath"));
                    break;
                }
                passive = true;
            }

            final String fieldName = chunk.getField().toString();

            final TypedExpression callExpr = DrlxParseUtil.nameExprToMethodCallExpr(fieldName, previousClass, null, context);
            if (callExpr == null) {
                context.addCompilationError( new InvalidExpressionErrorResult( "Unknown field " + fieldName + " on " + previousClass ) );
                break;
            }
            Class<?> fieldType = (chunk.getInlineCast() != null)
                    ? DrlxParseUtil.getClassFromContext(context.getTypeResolver(), chunk.getInlineCast().toString())
                    : callExpr.getRawClass();

            Type exprType = callExpr.getType();
            Expression ooPathChunkExpr = prepend(new NameExpr(THIS_PLACEHOLDER), callExpr.getExpression());
            if ( Iterable.class.isAssignableFrom(fieldType) || isDataSource(fieldType) ) {
                if (chunk.isSingleValue()) {
                    ooPathChunkExpr = new MethodCallExpr(null, "java.util.Collections.singletonList", NodeList.nodeList(ooPathChunkExpr));
                    exprType = creteListParameterizedType(exprType);
                } else {
                    fieldType = extractGenericType(previousClass, ((MethodCallExpr) callExpr.getExpression()).getName().toString());
                }
            }

            final Expression accessorLambda = createLambdaAccessor(previousClass, exprType, ooPathChunkExpr);
            final MethodCallExpr reactiveFrom = createFromExpr(previousBind, accessorLambda, passive);
            previousBind = bindOOPathChunk(originalBind, patternParseResult, i, i == chunks.size()-1, chunk, fieldName, fieldType, accessorLambda, reactiveFrom);
            previousClass = fieldType;
        }
    }

    private Expression createLambdaAccessor(Class<?> previousClass, Type exprType, Expression ooPathChunkExpr) {
        final Expression accessorLambda = generateLambdaWithoutParameters(Collections.emptySortedSet(), ooPathChunkExpr, false, Optional.ofNullable(previousClass), context);
        if (accessorLambda instanceof LambdaExpr) {
            context.getPackageModel().registerLambdaReturnType((LambdaExpr)accessorLambda, exprType);
        }
        return accessorLambda;
    }

    private MethodCallExpr createFromExpr(String previousBind, Expression accessorLambda, boolean passive) {
        final MethodCallExpr reactiveFrom = createDslTopLevelMethod(passive ? FROM_CALL : REACTIVE_FROM_CALL);
        reactiveFrom.addArgument(context.getVarExpr(previousBind));
        reactiveFrom.addArgument(accessorLambda);
        return reactiveFrom;
    }

    private String bindOOPathChunk(String originalBind, DrlxParseSuccess patternParseResult, int pos, boolean isLast, OOPathChunk chunk, String fieldName, Class<?> fieldType, Expression accessorLambda, MethodCallExpr reactiveFrom) {
        String previousBind;
        final String bindingId;
        if (isLast && patternParseResult.getExprBinding() != null) {
            bindingId = patternParseResult.getExprBinding();
            context.removeDeclarationById(bindingId);
        } else {
            bindingId = context.getOOPathId(fieldType, originalBind + fieldName + pos);
        }

        TypedDeclarationSpec newDeclaration = context.addDeclaration(bindingId, fieldType, reactiveFrom);
        context.addOOPathDeclaration(newDeclaration);

        final List<DrlxExpression> conditions = chunk.getConditions();
        if (conditions.isEmpty()) {
            toPatternExpr(bindingId, Collections.emptyList(), patternParseResult, fieldType);
        } else if (conditions.size() == 1 && conditions.get(0).getExpr().isIntegerLiteralExpr()) {
            // indexed access
            reactiveFrom.setArgument( 1, new MethodCallExpr(accessorLambda, "get", new NodeList<>(conditions.get(0).getExpr())) );
            toPatternExpr(bindingId, Collections.emptyList(), patternParseResult, fieldType);
        } else {
            Class<?> finalFieldType = fieldType;
            final List<DrlxParseResult> conditionParseResult = conditions.stream().map((DrlxExpression c) ->
                    ConstraintParser.defaultConstraintParser(context, packageModel).drlxParse(finalFieldType, bindingId, PrintUtil.printNode(c))
            ).collect(Collectors.toList());
            toPatternExpr(bindingId, conditionParseResult, patternParseResult, fieldType);
        }

        previousBind = bindingId;
        return previousBind;
    }

    private ParameterizedType creteListParameterizedType(Type exprType) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{ exprType };
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    private void toPatternExpr(String bindingId, List<DrlxParseResult> list, DrlxParseSuccess patternParseResult, Class<?> fieldType) {
        MethodCallExpr patternExpr = createDslTopLevelMethod(PATTERN_CALL);
        patternExpr.addArgument( context.getVar( bindingId ) );

        SingleDrlxParseSuccess oopathConstraint = null;

        for (DrlxParseResult drlx : list) {
            if (drlx.isSuccess()) {
                SingleDrlxParseSuccess singleDrlx = ( SingleDrlxParseSuccess ) drlx;
                if (singleDrlx.isOOPath()) {
                    if (oopathConstraint != null) {
                        throw new UnsupportedOperationException("An oopath chunk can only have a single oopath constraint");
                    }
                    oopathConstraint = singleDrlx;
                    continue;
                }
                if (singleDrlx.getExprBinding() != null) {
                    MethodCallExpr expr = expressionBuilder.buildBinding( singleDrlx );
                    expr.setScope( patternExpr );
                    patternExpr = expr;
                }
                if (singleDrlx.getExpr() != null && singleDrlx.isPredicate()) {
                    MethodCallExpr expr = expressionBuilder.buildExpressionWithIndexing( singleDrlx );
                    expr.setScope( patternExpr );
                    patternExpr = expr;
                }
            }
        }

        context.addExpression( patternExpr );
        if ( bindingId.equals( patternParseResult.getExprBinding() ) ) {
            context.registerOOPathPatternExpr(bindingId, patternExpr);
        }

        if (oopathConstraint != null) {
            new OOPathExprGenerator(context, packageModel).visit(fieldType, bindingId, oopathConstraint);
        }
    }
}
