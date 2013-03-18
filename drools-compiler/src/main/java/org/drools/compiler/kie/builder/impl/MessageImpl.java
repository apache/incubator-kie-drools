package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.builder.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageImpl implements Message {

    private final long id;
    private final Level level;
    private final String path;
    private final int line;
    private final int column;
    private final String text;

    public MessageImpl(long id, Level level, String path, String text) {
        this.id = id;
        this.level = level;
        this.path = path;
        this.text = text;
        this.line = 0;
        this.column = 0;
    }
    
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
        path = result.getResource().getSourcePath();
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
    
    public static List<Message> filterMessages(List<Message> messages, Level... levels) {
        List<Message> filteredMsgs = new ArrayList<Message>();
        if ( levels != null && levels.length > 0 ) {
            for ( Level level : levels )  {
                for ( Message msg : messages ) {
                    if ( msg.getLevel() == level ) {
                        filteredMsgs.add( msg );
                    }
                }
            }
        }
        return filteredMsgs;
    }

    @Override
    public String toString() {
        return "Message [id=" + id + ", level=" + level + ", path=" + path + ", line=" + line + ", column=" + column + "\n   text=" + text + "]";
    }
    

}
