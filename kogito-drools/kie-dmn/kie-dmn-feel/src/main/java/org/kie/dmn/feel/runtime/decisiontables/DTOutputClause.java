package org.kie.dmn.feel.runtime.decisiontables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;

public class DTOutputClause implements DecisionTable.OutputClause  {
    private final String          name;
    private final String          id;
    private final String          defaultValue;
    private final List<UnaryTest> outputValues;
    private final Type            type;
    private final boolean collection;

    public DTOutputClause(String name, List<UnaryTest> outputValues) {
        this( name, null, outputValues, null );
    }
    
    public DTOutputClause(String name, String id, List<UnaryTest> outputValues, String defaultValue) {
        this(name, id, outputValues, defaultValue, BuiltInType.UNKNOWN, false);
    }

    /**
     * @param isCollection should consider the output can be a collection of feelType; helpful for expressing a DMN isCollection itemDefinition attribute. 
     */
    public DTOutputClause(String name, String id, List<UnaryTest> outputValues, String defaultValue, Type feelType, boolean isCollection) {
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;

        if (outputValues != null) {
            this.outputValues = Collections.unmodifiableList(new ArrayList<UnaryTest>(outputValues));
        } else {
            this.outputValues = Collections.emptyList();
        }
        this.type = feelType;
        this.collection = isCollection;
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

    public boolean isCollection() {
        return collection;
    }
}
