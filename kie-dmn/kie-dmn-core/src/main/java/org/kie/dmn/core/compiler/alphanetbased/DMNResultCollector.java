package org.kie.dmn.core.compiler.alphanetbased;

import org.drools.ancompiler.ResultCollectorSink;
import org.drools.model.functions.Function1;
import org.kie.dmn.feel.lang.EvaluationContext;

public class DMNResultCollector implements ResultCollectorSink {

    private final int row;
    private final String columnName;
    private final Function1<EvaluationContext, Object> outputEvaluationFunction;
    private final Results results;

    public DMNResultCollector(int row,
                              String columnName,
                              Results results,
                              Function1<EvaluationContext, Object> outputEvaluationFunction) {
        this.row = row;
        this.columnName = columnName;
        this.results = results;
        this.outputEvaluationFunction = outputEvaluationFunction;
    }

    @Override
    public void collectObject() {
        results.addResult(row, columnName, outputEvaluationFunction);
    }
}
