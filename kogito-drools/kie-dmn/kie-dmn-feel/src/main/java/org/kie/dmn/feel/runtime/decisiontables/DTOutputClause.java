package org.kie.dmn.feel.runtime.decisiontables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DTOutputClause {
    private final String name;
    private final String id;
    private final String defaultValue;
    /**
[...] output values is a list of values for the one output. Each value is a string.
TODO ^ this might be conflict in specs if comparing FEEL scope Vs broader DMN scope.
     */
    private final List<String> outputValues;

    public DTOutputClause(String name, List<String> outputValues) {
        this( name, null, outputValues, null );
    }
    
    public DTOutputClause(String name, String id, List<String> outputValues, String defaultValue) {
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;

        if (outputValues != null) {
            this.outputValues = Collections.unmodifiableList(new ArrayList<String>(outputValues));
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

    public List<String> getOutputValues() {
        return outputValues;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
