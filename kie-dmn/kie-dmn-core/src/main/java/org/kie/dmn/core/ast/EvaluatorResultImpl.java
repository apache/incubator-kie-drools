package org.kie.dmn.core.ast;

import org.kie.dmn.core.api.EvaluatorResult;

public class EvaluatorResultImpl implements EvaluatorResult {
    private final Object     result;
    private final ResultType code;

    public EvaluatorResultImpl(Object result, ResultType code) {
        this.result = result;
        this.code = code;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public ResultType getResultType() {
        return code;
    }

    @Override
    public String toString() {
        return "EvaluatorResultImpl{" +
                "result=" + result +
                ", code=" + code +
                '}';
    }
}
