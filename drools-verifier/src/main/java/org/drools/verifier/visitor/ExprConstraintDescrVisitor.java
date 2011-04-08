package org.drools.verifier.visitor;

import org.drools.base.evaluators.Operator;
import org.drools.compiler.DrlExprParser;
import org.drools.lang.descr.*;
import org.drools.verifier.components.*;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.solver.Solvers;

import java.util.List;

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

        DrlExprParser drlExprParser = new DrlExprParser();
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

        setField(fieldName);

        if (isAVariableRestriction(value)) {
            createVariableRestriction(currentOrderNumber, value, operator);
        } else {
            createRestriction(currentOrderNumber, value, operator);
        }
    }

    private void createRestriction(int currentOrderNumber, String value, Operator operator) {
        Restriction restriction = LiteralRestriction.createRestriction(pattern, value);
        restriction.setFieldPath(field.getPath());
        restriction.setPatternIsNot(pattern.isPatternNot());
        restriction.setParentPath(pattern.getPath());
        restriction.setParentType(pattern.getVerifierComponentType());
        restriction.setOrderNumber(currentOrderNumber);
        restriction.setOperator(operator);
        data.add(restriction);
        solvers.addPatternComponent(restriction);
    }

    private void setField(String fieldName) {
        field = data.getFieldByObjectTypeAndFieldName(pattern.getName(), fieldName);
        if (field == null) {
            createField(fieldName);
        }
    }

    private void createField(String fieldName) {
        field = new Field();
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
        }

        return "";
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
                // TODO: Generated code -Rikkola-
                break;
            case INC_OR:
                // TODO: Generated code -Rikkola-
                break;
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
