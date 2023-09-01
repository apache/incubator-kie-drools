package org.drools.compiler.kie.builder.impl;

public class BuildContext {
    private final ResultsImpl messages;

    public BuildContext() {
        this(new ResultsImpl());
    }

    public BuildContext(ResultsImpl messages) {
        this.messages = messages;
    }

    public ResultsImpl getMessages() {
        return messages;
    }

    public boolean registerResourceToBuild(String kBaseName, String resource) {
        return true;
    }
}
