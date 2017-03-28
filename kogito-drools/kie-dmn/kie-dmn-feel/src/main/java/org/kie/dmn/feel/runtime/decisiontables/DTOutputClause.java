package org.kie.dmn.feel.runtime.decisiontables;

import org.kie.dmn.feel.runtime.UnaryTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DTOutputClause {
    private final String          name;
    private final String          id;
    private final String          defaultValue;
    private final List<UnaryTest> outputValues;

    public DTOutputClause(String name, List<UnaryTest> outputValues) {
        this( name, null, outputValues, null );
    }
    
    public DTOutputClause(String name, String id, List<UnaryTest> outputValues, String defaultValue) {
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;

        if (outputValues != null) {
            this.outputValues = Collections.unmodifiableList(new ArrayList<UnaryTest>(outputValues));
        } else {
            this.outputValues = Collections.emptyList();
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<UnaryTest> getOutputValues() {
        return outputValues;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
