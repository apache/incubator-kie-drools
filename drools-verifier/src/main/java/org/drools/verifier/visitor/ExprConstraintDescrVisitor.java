package org.drools.verifier.visitor;

import org.drools.base.evaluators.Operator;
import org.drools.compiler.DrlExprParser;
import org.drools.lang.descr.*;
import org.drools.verifier.components.*;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.data.VerifierData;

import java.util.List;

public class ExprConstraintDescrVisitor {

    private final Pattern pattern;
    private final VerifierData data;
    private OrderNumber orderNumber;
    private Field field;

    public ExprConstraintDescrVisitor(Pattern pattern, VerifierData data, OrderNumber orderNumber) {
        this.pattern = pattern;
        this.data = data;
        this.orderNumber = orderNumber;
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
        System.out.println("RelationalExprDescr " + descr);

        int currentOrderNumber = orderNumber.next();
        String fieldName = visit(descr.getLeft());
        String value = visit(descr.getRight());

        createField(fieldName);
        createRestriction(descr, currentOrderNumber, value);
    }

    private void createRestriction(RelationalExprDescr descr, int currentOrderNumber, String value) {
        Restriction restriction = LiteralRestriction.createRestriction(pattern, value);
        restriction.setFieldPath(field.getPath());
        restriction.setPatternIsNot(pattern.isPatternNot());
        restriction.setParentPath(pattern.getPath());
        restriction.setParentType(pattern.getVerifierComponentType());
        restriction.setOrderNumber(currentOrderNumber);
        restriction.setOperator(Operator.determineOperator(descr.getOperator(), descr.isNegated()));
        Operator operator = restriction.getOperator();
        System.out.println("Restriction path: " + restriction.getPath() + " " + operator);
        data.add(restriction);
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
        System.out.println("ConstraintConnectiveDescr " + descr);
        // TODO: Generated code -Rikkola-
    }

    private String visit(AtomicExprDescr descr) {
        System.out.println("AtomicExprDescr " + descr);
        String expression = descr.getExpression();

        if (isEval(expression)) {
            createEval(expression);
        } else if (isSurroundedByQuotes(expression)) {
            expression = expression.substring(1, expression.length() - 1);
        }

        return expression;
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

        data.add(eval);
    }

    private boolean firstAndLastCharacterIs(String expression, char character) {
        return expression.charAt(0) == character && expression.charAt(expression.length() - 1) == character;
    }
}
