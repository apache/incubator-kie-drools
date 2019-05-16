package org.kie.dmn.core.pmml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PMMLModelInfo {

    protected final String name;
    private final Collection<String> inputFieldNames;
    private final Collection<String> outputFieldNames;

    public PMMLModelInfo(String name, Collection<String> inputFieldNames, Collection<String> outputFieldNames) {
        this.name = name;
        this.inputFieldNames = Collections.unmodifiableList(new ArrayList<>(inputFieldNames));
        this.outputFieldNames = Collections.unmodifiableList(new ArrayList<>(outputFieldNames));
    }

    public String getName() {
        return name;
    }

    public Collection<String> getInputFieldNames() {
        return inputFieldNames;
    }

    public Collection<String> getOutputFieldNames() {
        return outputFieldNames;
    }

}
