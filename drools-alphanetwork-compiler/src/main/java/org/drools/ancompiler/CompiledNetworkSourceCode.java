package org.drools.ancompiler;

import org.drools.core.rule.IndexableConstraint;

public class CompiledNetworkSourceCode {

    private final String source;
    private final IndexableConstraint indexableConstraint;
    private final String sourceName;
    private final String binaryName;
    private final String name;

    public CompiledNetworkSourceCode(String source, IndexableConstraint indexableConstraint, String sourceName, String binaryName, String name) {
        this.source = source;
        this.indexableConstraint = indexableConstraint;
        this.sourceName = sourceName;
        this.binaryName = binaryName;
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public IndexableConstraint getIndexableConstraint() {
        return indexableConstraint;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getBinaryName() {
        return binaryName;
    }

    public String getName() {
        return name;
    }
}
