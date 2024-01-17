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
package org.drools.model.codegen.execmodel.generator.visitor.pattern;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.Index;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.DeclarationSpec;
import org.drools.model.codegen.execmodel.generator.DrlxParseUtil;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.mvel.parser.ast.expr.DrlxExpression;

import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PROTO_EXPR_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PROTO_PATTERN_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createProtoDslTopLevelMethod;
import static org.drools.model.impl.VariableImpl.GENERATED_VARIABLE_PREFIX;

public class PrototypePatternDSL extends PatternDSL {

    private final String patternName;

    PrototypePatternDSL(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, String patternName) {
        super(context, packageModel, pattern, constraintDescrs);
        this.patternName = patternName;
    }

    @Override
    protected List<PatternConstraintParseResult> findAllConstraint(PatternDescr pattern, List<? extends BaseDescr> constraintDescrs) {
        List<PatternConstraintParseResult> patternConstraintParseResults = new ArrayList<>();

        for (BaseDescr constraint : constraintDescrs) {
            String patternIdentifier = pattern.getIdentifier();
            String expression = constraint.toString();

            DrlxExpression drlx = DrlxParseUtil.parseExpression( expression );
            var drlxParse = new SingleDrlxParseSuccess(null, patternIdentifier, drlxToExpression(drlx), null);
            if (drlx.getBind() != null) {
                drlxParse.setExprBinding(drlx.getBind().asString());
            }
            patternConstraintParseResults.add(new PatternConstraintParseResult(expression, patternIdentifier, drlxParse));
        }

        return patternConstraintParseResults;
    }

    private Expression drlxToExpression(DrlxExpression drlx) {
        Expression expression = drlx.getExpr();

        if (expression instanceof BinaryExpr bExpr) {
            MethodCallExpr dslExpr = new MethodCallExpr(PROTO_EXPR_CALL);
            dslExpr.addArgument( new StringLiteralExpr(bExpr.getLeft().asNameExpr().getName().asString()) );
            dslExpr.addArgument( Index.ConstraintType.class.getCanonicalName() + "." + DrlxParseUtil.toConstraintType(bExpr.getOperator() ) );
            dslExpr.addArgument( bExpr.getRight() );
            return dslExpr;
        }

        throw new UnsupportedOperationException("Unknown expression type: " + expression.getClass().getSimpleName());
    }

    @Override
    public DeclarationSpec initPattern() {
        generatePatternIdentifierIfMissing();
        context.addPatternBinding(pattern.getIdentifier());
        return context.addPrototypeDeclaration(pattern.getIdentifier(), pattern.getObjectType());
    }

    private void generatePatternIdentifierIfMissing() {
        // the PatternDescr can be shared by multiple rules in case of rules inheritance, so its identifier has to
        // be set atomically when rule generation is performed in parallel
        synchronized (pattern) {
            if (pattern.getIdentifier() == null) {
                pattern.setIdentifier(GENERATED_VARIABLE_PREFIX + patternName);
            }
        }
    }

    @Override
    protected void buildPattern(DeclarationSpec declarationSpec, List<PatternConstraintParseResult> patternConstraintParseResults) {
        MethodCallExpr patternExpression = createPatternExpression(pattern);
        for (PatternConstraintParseResult parseResult : patternConstraintParseResults) {
            patternExpression = ((DrlxParseSuccess) parseResult.drlxParseResult()).getExpr().asMethodCallExpr().setScope(patternExpression);
        }
        context.addExpression( patternExpression );
    }

    @Override
    protected String getPatternTypeName() {
        return patternName;
    }

    @Override
    protected Class<?> getPatternType() {
        return null;
    }

    @Override
    protected MethodCallExpr input(DeclarationSpec declarationSpec) {
        return createPatternExpression(pattern);
    }

    private MethodCallExpr createPatternExpression(PatternDescr pattern) {
        MethodCallExpr dslExpr = createProtoDslTopLevelMethod(PROTO_PATTERN_CALL);
        dslExpr.addArgument( context.getVarExpr( pattern.getIdentifier()) );
        return dslExpr;
    }
}
