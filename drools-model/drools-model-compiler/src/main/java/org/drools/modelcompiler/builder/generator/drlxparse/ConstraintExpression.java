/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.drlxparse;

import java.lang.reflect.Field;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.kie.api.definition.type.Position;

public class ConstraintExpression {

    private final String expression;

    private String unificationField;
    private boolean nameClashingUnification;

    public ConstraintExpression( String expression) {
        this.expression = expression;
    }

    public static ConstraintExpression createConstraintExpression( Class<?> patternType, BaseDescr constraint, boolean isPositional ) {
        String expression = parseConstraintExpression(patternType, constraint, isPositional);

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

        // I really hope we won't be comparing Strings with newline here.
        String expressionWithoutNewLines = expression.replace("\n", "");

        return new ConstraintExpression( expressionWithoutNewLines );
    }

    public static String parseConstraintExpression( Class<?> patternType, BaseDescr constraint, boolean isPositional) {
        if (isPositional) {
            int position = ((ExprConstraintDescr ) constraint).getPosition();
            return getFieldAtPosition(patternType, position) + " == " + constraint.toString();
        }
        return constraint.toString();
    }

    private static String getFieldAtPosition(Class<?> patternType, int position) {
        for (Field field : patternType.getDeclaredFields()) {
            Position p = field.getAnnotation(Position.class);
            if (p != null && p.value() == position) {
                return field.getName();
            }
        }
        if (patternType.getSuperclass() != null && patternType.getSuperclass() != Object.class) {
            return getFieldAtPosition(patternType.getSuperclass(), position);
        }
        throw new RuntimeException("Cannot find field in position " + position + " for " + patternType);
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
