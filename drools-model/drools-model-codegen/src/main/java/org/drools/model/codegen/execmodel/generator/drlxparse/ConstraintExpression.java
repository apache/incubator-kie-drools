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
package org.drools.model.codegen.execmodel.generator.drlxparse;

import java.lang.reflect.Field;
import java.util.Optional;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.kie.api.definition.type.Position;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import static org.drools.util.StringUtils.isIdentifier;

public class ConstraintExpression {

    private final String expression;

    private String unificationField;
    private boolean nameClashingUnification;

    public ConstraintExpression( String expression) {
        this.expression = expression;
    }

    public static ConstraintExpression createConstraintExpression( RuleContext context, Class<?> patternType, BaseDescr constraint, boolean isPositional ) {
        String expression = parseConstraintExpression(context, patternType, constraint, isPositional);
        if (expression == null) {
            return null;
        }

        int unifPos = expression.indexOf( ":=" );
        if (unifPos > 0) {
            String unificationField = expression.substring( 0, unifPos ).trim();
            String unifiedProp = expression.substring( unifPos+2 ).trim();
            expression = unifiedProp + " == " + unificationField;

            ConstraintExpression constraintExpression = new ConstraintExpression( expression );
            constraintExpression.unificationField = unificationField;
            constraintExpression.nameClashingUnification = unificationField.equals( unifiedProp );
            return constraintExpression;
        }

        return new ConstraintExpression( expression );
    }

    private static String parseConstraintExpression( RuleContext context, Class<?> patternType, BaseDescr constraint, boolean isPositional ) {
        if (isPositional) {
            String expr = constraint.toString();
            boolean isConstraint = !isIdentifier(expr) || context.getTypedDeclarationById(expr ).isPresent();
            int position = ((ExprConstraintDescr ) constraint).getPosition();
            Optional<String> field = getFieldAtPosition(context, patternType, position);

            return field
                    .map( f -> isConstraint ? f + " == " + expr : expr + (context.isQuery() && !expr.equals( f ) ? " := " : " : ") + f)
                    .orElse( null );
        }
        return constraint.toString();
    }

    private static Optional<String> getFieldAtPosition( RuleContext context, Class<?> patternType, int position ) {
        for (Field field : patternType.getDeclaredFields()) {
            Position p = field.getAnnotation(Position.class);
            if (p != null && p.value() == position) {
                return of(field.getName());
            }
        }
        if (patternType.getSuperclass() != null && patternType.getSuperclass() != Object.class) {
            return getFieldAtPosition(context, patternType.getSuperclass(), position);
        }

        context.addCompilationError(new InvalidExpressionErrorResult("Unable to find @Positional field " + position + " for class " + patternType.getCanonicalName(), of(context.getRuleDescr())));
        return empty();
    }

    public String getExpression() {
        return expression;
    }

    public String getUnificationField() {
        return unificationField;
    }

    public boolean isUnification() {
        return unificationField != null;
    }

    public boolean isNameClashingUnification() {
        return nameClashingUnification;
    }
}
