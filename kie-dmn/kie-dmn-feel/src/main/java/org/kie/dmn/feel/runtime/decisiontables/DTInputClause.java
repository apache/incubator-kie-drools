package org.kie.dmn.feel.runtime.decisiontables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.runtime.UnaryTest;

public class DTInputClause {
    private final String inputExpression;
    private final CompiledExpression compiledInput;
    private final String inputValuesText;
    private final List<UnaryTest> inputValues;

    public DTInputClause(String inputExpression, String inputValuesText, List<UnaryTest> inputValues, CompiledExpression compiledInput) {
        super();
        this.inputExpression = inputExpression;
        this.inputValuesText = inputValuesText;
        if (inputValues != null) {
            this.inputValues = Collections.unmodifiableList(new ArrayList<UnaryTest>(inputValues));
        } else {
            this.inputValues = Collections.emptyList();
        }
        this.compiledInput = compiledInput;
    }
   
    public String getInputExpression() {
        return inputExpression;
    }
    
    public List<UnaryTest> getInputValues() {
        return inputValues;
    }

    public String getInputValuesText() {
        return inputValuesText;
    }

    public CompiledExpression getCompiledInput() {
        return compiledInput;
    }
}