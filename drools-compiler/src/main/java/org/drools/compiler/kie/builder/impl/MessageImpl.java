/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.api.builder.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MessageImpl implements Message {

    protected static final transient Logger logger = LoggerFactory.getLogger( MessageImpl.class );

    private final long id;
    private final Level level;
    private final String path;
    private final int line;
    private final int column;
    private final String text;

    private String kieBaseName;

    public MessageImpl( long id,
                        Level level,
                        String path,
                        String text ) {
        this.id = id;
        this.level = level;
        this.path = path;
        this.text = text;
        this.line = 0;
        this.column = 0;
    }

    public MessageImpl( long id,
                        CompilationProblem problem ) {
        this.id = id;
        level = problem.isError() ? Level.ERROR : Level.WARNING;
        path = problem.getFileName();
        line = problem.getStartLine();
        column = problem.getStartColumn();
        text = problem.getMessage();
    }

    public MessageImpl( long id,
                        KnowledgeBuilderResult result ) {
        this.id = id;
        switch ( result.getSeverity() ) {
            case ERROR:
                level = Level.ERROR;
                break;
            case WARNING:
                level = Level.WARNING;
                break;
            default:
                level = Level.INFO;
        }
        //See JIRA DROOLS-193 (KnowledgeBuilderResult does not always contain a Resource)
        Resource resource = result.getResource();
        if ( resource == null ) {
            logger.debug( "resource is null: " + result.toString() );
            path = null;
        } else {
            path = resource.getSourcePath();
        }

        if ( result.getLines().length > 0 ) {
            line = result.getLines()[ 0 ];
        } else {
            line = -1;
        }
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

    public String getKieBaseName() {
        return kieBaseName;
    }

    public void setKieBaseName( String kieBaseName ) {
        this.kieBaseName = kieBaseName;
    }

    public static List<Message> filterMessages( List<Message> messages,
                                                Level... levels ) {
        List<Message> filteredMsgs = new ArrayList<Message>();
        if ( levels != null && levels.length > 0 ) {
            for ( Level level : levels ) {
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
        return "Message [id=" + id + (kieBaseName != null ? ", kieBase=" + kieBaseName : "") + ", level=" + level +
               ", path=" + path + ", line=" + line + ", column=" + column + "\n   text=" + text + "]";
    }

}
