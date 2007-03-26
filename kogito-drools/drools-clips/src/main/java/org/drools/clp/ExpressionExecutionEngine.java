package org.drools.clp;

public class ExpressionExecutionEngine { //implements Consequence, ReturnValueExpression, PredicateExpression {
    //private Map variableMap = new HashMap();

    private Function function;
    private int      localVariableSize;

    public ExpressionExecutionEngine() {

    }

    public ExpressionExecutionEngine(Function function) {
        this.function = function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

}
