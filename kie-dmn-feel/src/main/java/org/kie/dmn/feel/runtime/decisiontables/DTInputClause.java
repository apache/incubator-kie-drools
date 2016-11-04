package org.kie.dmn.feel.runtime.decisiontables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.feel.runtime.UnaryTest;

public class DTInputClause {
    private final String inputExpression;
    private final List<UnaryTest> inputValues;
    
    public DTInputClause(String inputExpression, List<UnaryTest> inputValues) {
        super();
        this.inputExpression = inputExpression;
        if (inputValues != null) {
            this.inputValues = Collections.unmodifiableList(new ArrayList<UnaryTest>(inputValues));
        } else {
            this.inputValues = Collections.emptyList();
        }
    }
   
    public String getInputExpression() {
        return inputExpression;
    }
    
    public List<UnaryTest> getInputValues() {
        return inputValues;
    }
}