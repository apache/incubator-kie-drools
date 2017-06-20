package org.kie.dmn.feel.runtime.decisiontables;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DTOutputClause {
    private final String          name;
    private final String          id;
    private final String          defaultValue;
    private final List<UnaryTest> outputValues;
    private final Type            type;

    public DTOutputClause(String name, List<UnaryTest> outputValues) {
        this( name, null, outputValues, null );
    }
    
    public DTOutputClause(String name, String id, List<UnaryTest> outputValues, String defaultValue) {
        this( name, id, outputValues, defaultValue, BuiltInType.UNKNOWN );
    }

    public DTOutputClause(String name, String id, List<UnaryTest> outputValues, String defaultValue, Type feelType) {
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;

        if (outputValues != null) {
            this.outputValues = Collections.unmodifiableList(new ArrayList<UnaryTest>(outputValues));
        } else {
            this.outputValues = Collections.emptyList();
        }
        this.type = feelType;
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

    public Type getType() {
        return type;
    }
}
