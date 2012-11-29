package org.kie.builder.impl;

import org.drools.commons.jci.problems.CompilationProblem;
import org.kie.builder.KnowledgeBuilderResult;
import org.kie.builder.Message;

public class MessageImpl implements Message {

    private final long id;
    private final Level level;
    private final String path;
    private final int line;
    private final int column;
    private final String text;

    public MessageImpl(long id, CompilationProblem problem) {
        this.id = id;
        level = problem.isError() ? Level.ERROR : Level.WARNING;
        path = problem.getFileName();
        line = problem.getStartLine();
        column = problem.getStartColumn();
        text = problem.getMessage();
    }

    public MessageImpl(long id, KnowledgeBuilderResult result) {
        this.id = id;
        switch (result.getSeverity()) {
            case ERROR:
                level = Level.ERROR;
                break;
            case WARNING:
                level = Level.WARNING;
                break;
            default:
                level = Level.INFO;
        }
        path = ""; // TODO ?
        line = result.getLines()[0];
        column = 0;
        text = result.getMessage();
    }

    public long getId() {
        return id;
    }

    public Level getLevel() {
        return level;
    }

    public String getPath() {
        return path;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getText() {
        return text;
    }
}
