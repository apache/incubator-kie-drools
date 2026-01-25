/*
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
package org.drools.verifier.visitor;

import java.util.List;

import org.drools.drl.parser.DrlExprParserFactory;
import org.drools.drl.parser.impl.Operator;
import org.drools.drl.parser.DrlExprParser;
import org.drools.drl.ast.descr.AtomicExprDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.BindingDescr;
import org.drools.drl.ast.descr.ConstraintConnectiveDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.RelationalExprDescr;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.FieldVariable;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternEval;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.solver.Solvers;
import org.kie.internal.builder.conf.LanguageLevelOption;

public class ExprConstraintDescrVisitor {

    private final Pattern pattern;
    private final VerifierData data;
    private final Solvers solvers;
    private final OrderNumber orderNumber;
    private Field field;

    public ExprConstraintDescrVisitor(Pattern pattern, VerifierData data, OrderNumber orderNumber, Solvers solvers) {
        this.pattern = pattern;
        this.data = data;
        this.orderNumber = orderNumber;
        this.solvers = solvers;
    }

    public void visit(ExprConstraintDescr descr) {

        DrlExprParser drlExprParser = DrlExprParserFactory.getDrlExprParser(LanguageLevelOption.DRL5);
        ConstraintConnectiveDescr constraintConnectiveDescr = drlExprParser.parse(descr.getExpression());

        visit(constraintConnectiveDescr.getDescrs());
    }

    private void visit(List<BaseDescr> descrs) {
        for (BaseDescr descr : descrs) {
            visit(descr);
        }
    }

    private void visit(RelationalExprDescr descr) {
        int currentOrderNumber = orderNumber.next();
        String fieldName = visit(descr.getLeft());
        Operator operator = Operator.determineOperator(descr.getOperator(), descr.isNegated());
        String value = visit(descr.getRight());

        setField(descr,fieldName);

        if (isAVariableRestriction(value)) {
            createVariableRestriction(currentOrderNumber, value, operator);
        } else {
            createRestriction(currentOrderNumber, value, operator);
        }
    }

    private void createRestriction(int currentOrderNumber, String value, Operator operator) {
        LiteralRestriction restriction = LiteralRestriction.createRestriction(pattern, value);
        restriction.setFieldPath(field.getPath());
        restriction.setFieldName(field.getName());
        restriction.setPatternIsNot(pattern.isPatternNot());
        restriction.setParentPath(pattern.getPath());
        restriction.setParentType(pattern.getVerifierComponentType());
        restriction.setOrderNumber(currentOrderNumber);
        restriction.setOperator(operator);
        field.setFieldType(restriction.getValueType());
        data.add(restriction);
        solvers.addPatternComponent(restriction);
    }

    private void setField(RelationalExprDescr descr, String fieldName) {
        field = data.getFieldByObjectTypeAndFieldName(pattern.getName(), fieldName);
        if (field == null) {
            createField(descr, fieldName);
        }
    }

    private void createField(RelationalExprDescr descr, String fieldName) {
        field = new Field(pattern.getDescr());
        field.setName(fieldName);
        field.setObjectTypePath(pattern.getObjectTypePath());
        field.setObjectTypeName(pattern.getName());
        data.add(field);
    }

    private String visit(BaseDescr descr) {
        if (descr instanceof AtomicExprDescr) {
            return visit((AtomicExprDescr) descr);
        } else if (descr instanceof ConstraintConnectiveDescr) {
            visit((ConstraintConnectiveDescr) descr);
        } else if (descr instanceof RelationalExprDescr) {
            visit((RelationalExprDescr) descr);
        } else if (descr instanceof BindingDescr) {
            visit((BindingDescr) descr);
        }

        return "";
    }

    private void visit(BindingDescr descr) {
        Field field = new Field(descr);
        field.setName(descr.getExpression());
        field.setObjectTypeName(pattern.getName());
        field.setObjectTypePath(pattern.getObjectTypePath());
        data.add(field);

        FieldVariable fieldVariable = new FieldVariable(pattern);
        fieldVariable.setParentPath(field.getPath());
        fieldVariable.setName(descr.getVariable());
        fieldVariable.setOrderNumber(orderNumber.next());

        data.add(fieldVariable);
    }

    private void visit(ConstraintConnectiveDescr descr) {
        switch (descr.getConnective()) {
            case AND:
                solvers.startOperator(OperatorDescrType.AND);
                for (BaseDescr baseDescr : descr.getDescrs()) {
                    visit(baseDescr);
                }
                solvers.endOperator();
                break;
            case OR:
                solvers.startOperator(OperatorDescrType.OR);
                for (BaseDescr baseDescr : descr.getDescrs()) {
                    visit(baseDescr);
                }
                solvers.endOperator();
                break;
            case XOR:
            case INC_OR:
            case INC_AND:
                // TODO: Generated code -Rikkola-
                break;
        }
    }

    private String visit(AtomicExprDescr descr) {
        String expression = descr.getExpression();

        if (isEval(expression)) {
            createEval(expression);
        } else if (isSurroundedByQuotes(expression)) {
            expression = expression.substring(1, expression.length() - 1);
        }

        return expression;
    }

    private boolean isAVariableRestriction(String value) {
        return data.getVariableByRuleAndVariableName(pattern.getRuleName(), value) != null;
    }

    private boolean isSurroundedByQuotes(String expression) {
        return firstAndLastCharacterIs(expression, '"') || firstAndLastCharacterIs(expression, '\'');
    }

    private boolean isEval(String expression) {
        return expression.trim().startsWith("eval");
    }

    private void createEval(String expression) {
        PatternEval eval = new PatternEval(pattern);
        eval.setContent(expression);
        eval.setOrderNumber(orderNumber.next());
        eval.setParentPath(pattern.getPath());
        eval.setParentType(pattern.getVerifierComponentType());

        solvers.addPatternComponent(eval);
        data.add(eval);
    }

    private boolean firstAndLastCharacterIs(String expression, char character) {
        return expression.charAt(0) == character && expression.charAt(expression.length() - 1) == character;
    }

    private void createVariableRestriction(int orderNumber, String value, Operator operator) {
        Variable variable = data.getVariableByRuleAndVariableName(pattern.getRuleName(), value);
        VariableRestriction restriction = new VariableRestriction(pattern);

        restriction.setPatternIsNot(pattern.isPatternNot());
        restriction.setFieldPath(field.getPath());
        restriction.setOperator(operator);
        restriction.setVariable(variable);
        restriction.setOrderNumber(orderNumber);
        restriction.setParentPath(pattern.getPath());
        restriction.setParentType(pattern.getVerifierComponentType());

        // Set field value, if it is unset.
        field.setFieldType(Field.VARIABLE);

        data.add(restriction);
        solvers.addPatternComponent(restriction);
    }
}
