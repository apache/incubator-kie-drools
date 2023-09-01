package org.kie.dmn.core.pmml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PMMLModelInfo {

    protected final String name;
    protected final String className;
    protected final Collection<String> inputFieldNames;
    protected final Collection<String> outputFieldNames;
    protected final Collection<String> targetFieldNames;

    public PMMLModelInfo(String name, String className, Collection<String> inputFieldNames, Collection<String> targetFieldNames, Collection<String> outputFieldNames) {
        this.name = name;
        this.className = className;
        this.inputFieldNames = Collections.unmodifiableList(new ArrayList<>(inputFieldNames));
        this.targetFieldNames = Collections.unmodifiableList(new ArrayList<>(targetFieldNames));
        this.outputFieldNames = Collections.unmodifiableList(new ArrayList<>(outputFieldNames));
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public Collection<String> getInputFieldNames() {
        return inputFieldNames;
    }

    public Collection<String> getOutputFieldNames() {
        return outputFieldNames;
    }

    public Collection<String> getTargetFieldNames() {
        return targetFieldNames;
    }

}
