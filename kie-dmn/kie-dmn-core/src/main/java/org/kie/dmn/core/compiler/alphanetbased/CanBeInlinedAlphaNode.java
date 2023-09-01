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
package org.kie.dmn.core.compiler.alphanetbased;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.ancompiler.CanInlineInANC;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.base.rule.constraint.Constraint;
import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.PredicateInformation;
import org.drools.modelcompiler.constraints.ConstraintEvaluator;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.kie.dmn.core.compiler.alphanetbased.evaluator.TestEvaluator;

import static com.github.javaparser.StaticJavaParser.parseExpression;

public class CanBeInlinedAlphaNode extends AlphaNode implements CanInlineInANC<LambdaConstraint> {

    private MethodCallExpr methodCallExpr;

    public CanBeInlinedAlphaNode() {
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static class Builder {

        LambdaConstraint constraint;
        MethodCallExpr methodCallExpr;
        String id;

        /**
         * IMPORTANT: remember to use the FEEL expression as an Identifier for the same constraint
         * <p>
         * Prefix: column name + value
         */
        public <T> Builder withConstraint(
                String id,
                Predicate1<T> predicate, // TODO DT-ANC this is bound to be removed
                Index index,
                Variable<T> variable,
                Declaration declaration) {
            constraint = createConstraint(id, predicate, index, variable, declaration);
            this.id = id;
            return this;
        }

        public static <T extends Object> LambdaConstraint createConstraint(
                String id,
                Predicate1<T> predicate,
                Index index,
                Variable<T> variable,
                Declaration declaration
        ) {
            // TODO DT-ANC need these twos to keep the two code paths otherwise only the second
            SingleConstraint1<T> constraint;
            if (predicate != null) {
                constraint = new SingleConstraint1<>(id, variable, predicate);
            } else {
                // TODO DT-ANC predicate information
                PredicateInformation predicateInformation = PredicateInformation.EMPTY_PREDICATE_INFORMATION;
                constraint = new SingleConstraint1<>(id, predicateInformation);
            }
            constraint.setIndex(index);
            LambdaConstraint lambda = new LambdaConstraint(new ConstraintEvaluator(new Declaration[]{declaration}, constraint));
            lambda.setType(Constraint.ConstraintType.ALPHA);
            return lambda;
        }

        public Builder withFeelConstraint(String feelConstraintTest, int index, String traceString) {
            // i.e. InlineableAlphaNode.Builder
            // .createConstraint("Age_62_6118", p -> evaluateAllTests(p, UnaryTestR1C1.getInstance(), 0, "trace"), null, ctx.variable, ctx.declaration)

            methodCallExpr = new MethodCallExpr(new NameExpr(Builder.class.getCanonicalName()), "createConstraint");

            // p
            methodCallExpr.addArgument(new StringLiteralExpr(id));

            // lambda
            Parameter parameter = new Parameter(StaticJavaParser.parseType(PropertyEvaluator.class.getCanonicalName()), "p");
            MethodCallExpr evaluateAllTests = new MethodCallExpr(new NameExpr(TestEvaluator.class.getCanonicalName()), "evaluateAllTests");
            evaluateAllTests.addArgument(new NameExpr("p"));
            evaluateAllTests.addArgument(new MethodCallExpr(new NameExpr(feelConstraintTest), "getInstance"));
            evaluateAllTests.addArgument(new IntegerLiteralExpr(index));
            evaluateAllTests.addArgument(new StringLiteralExpr(traceString));
            methodCallExpr.addArgument(new LambdaExpr(NodeList.nodeList(parameter), evaluateAllTests));

            // index lambda
            methodCallExpr.addArgument(new NullLiteralExpr());

            // variable
            methodCallExpr.addArgument(parseExpression("ctx.getVariable()"));

            // declaration
            methodCallExpr.addArgument(parseExpression("ctx.getDeclaration()"));


            return this;
        }

        public CanBeInlinedAlphaNode createAlphaNode(int id, ObjectSource objectSource, BuildContext context) {
            return new CanBeInlinedAlphaNode(id, constraint, objectSource, context, methodCallExpr);
        }
    }

    private CanBeInlinedAlphaNode(int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context, MethodCallExpr methodCallExpr) {
        super(id, constraint, objectSource, context);
        this.methodCallExpr = methodCallExpr;
    }

    @Override
    public MethodCallExpr toANCInlinedForm() {
        return methodCallExpr;
    }

    @Override
    public Class<LambdaConstraint> inlinedType() {
        return LambdaConstraint.class;
    }
}
