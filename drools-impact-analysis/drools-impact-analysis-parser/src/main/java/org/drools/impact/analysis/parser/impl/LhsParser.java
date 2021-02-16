/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.parser.impl;

import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.impact.analysis.model.Rule;
import org.drools.impact.analysis.model.left.Constraint;
import org.drools.impact.analysis.model.left.LeftHandSide;
import org.drools.impact.analysis.model.left.Pattern;
import org.drools.model.Index;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;

import static org.drools.impact.analysis.parser.impl.ParserUtil.literalToValue;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.isThisExpression;

public class LhsParser {

    private final PackageModel packageModel;
    private final PackageRegistry pkgRegistry;

    public LhsParser( PackageModel packageModel, PackageRegistry pkgRegistry ) {
        this.packageModel = packageModel;
        this.pkgRegistry = pkgRegistry;
    }

    public void parse( RuleDescr ruleDescr, RuleContext context, Rule rule ) {
        for (BaseDescr baseDescr : ruleDescr.getLhs().getDescrs()) {
            parseDescr( context, rule.getLhs(), baseDescr, true );
        }
    }

    private void parseDescr( RuleContext context, LeftHandSide lhs, BaseDescr baseDescr, boolean positive ) {
        if (baseDescr instanceof PatternDescr) {
            lhs.addPattern( parsePattern( context, (PatternDescr) baseDescr, positive) );
        } else if (baseDescr instanceof ConditionalElementDescr ) {
            if (baseDescr instanceof NotDescr) {
                positive = !positive;
            }
            for (BaseDescr innerDescr : (( ConditionalElementDescr ) baseDescr).getDescrs()) {
                parseDescr( context, lhs, innerDescr, positive );
            }
        }
    }

    private Pattern parsePattern( RuleContext context, PatternDescr patternDescr, boolean positive ) {
        String type = patternDescr.getObjectType();
        Class<?> patternClass;
        try {
            patternClass = pkgRegistry.getTypeResolver().resolveType( type );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }

        Pattern pattern = new Pattern( patternClass, positive );
        if ( patternDescr.getIdentifier() != null ) {
            context.addDeclaration( patternDescr.getIdentifier(), patternClass );
        }

        ConstraintParser constraintParser = new ConstraintParser(context, packageModel);
        for (BaseDescr constraintDescr : patternDescr.getConstraint().getDescrs()) {
            parseConstraint( patternDescr, pattern, constraintParser, constraintDescr );
        }
        return pattern;
    }

    private void parseConstraint( PatternDescr patternDescr, Pattern pattern, ConstraintParser constraintParser, BaseDescr constraintDescr ) {
        ConstraintExpression constraintExpression = ConstraintExpression.createConstraintExpression( pattern.getPatternClass(), constraintDescr, false);
        DrlxParseResult drlxParseResult = constraintParser.drlxParse( pattern.getPatternClass(), patternDescr.getIdentifier(), constraintExpression, false);
        if (drlxParseResult.isSuccess()) {
            SingleDrlxParseSuccess result = ( SingleDrlxParseSuccess ) drlxParseResult;
            if (result.getReactOnProperties().size() > 0) {
                result.getReactOnProperties().forEach( pattern::addReactOn );
            } else {
                pattern.setClassReactive( true );
            }
            if (result.getRight() != null) {
                Constraint constraint = new Constraint();
                parseExpressionInConstraint( constraint, result.getLeft() );
                boolean valueOnLeft = constraint.getValue() != null;
                parseExpressionInConstraint( constraint, result.getRight() );
                if ( constraint.getValue() != null) {
                    // the constraint is relevant for impact analysis only if it checks a fixed value
                    constraint.setType( decode(result.getDecodeConstraintType(), valueOnLeft ) );
                    pattern.addConstraint( constraint );
                }
            }
        }
    }

    private void parseExpressionInConstraint( Constraint constraint, TypedExpression expr ) {
        if (expr.getExpression() instanceof MethodCallExpr && isThisExpression( (( MethodCallExpr ) expr.getExpression()).getScope().orElse( null ) )) {
            constraint.setProperty( expr.getFieldName() );
        } else if (expr.getExpression().isLiteralExpr()) {
            constraint.setValue( literalToValue( expr.getExpression().asLiteralExpr() ) );
        }
    }

    private static Constraint.Type decode( Index.ConstraintType constraintType, boolean isInverted) {
        if (constraintType == null) {
            return null;
        }
        if (isInverted) {
            constraintType = constraintType.inverse();
        }
        switch (constraintType) {
            case FORALL_SELF_JOIN:
            case EQUAL:
                return Constraint.Type.EQUAL;
            case NOT_EQUAL:
                return Constraint.Type.NOT_EQUAL;
            case GREATER_THAN:
                return Constraint.Type.GREATER_THAN;
            case GREATER_OR_EQUAL:
                return Constraint.Type.GREATER_OR_EQUAL;
            case LESS_OR_EQUAL:
                return Constraint.Type.LESS_OR_EQUAL;
            case LESS_THAN:
                return Constraint.Type.LESS_THAN;
        }
        return Constraint.Type.UNKNOWN;

    }
}
