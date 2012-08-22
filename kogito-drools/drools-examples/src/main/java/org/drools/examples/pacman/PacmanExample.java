/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.examples.pacman;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.ConsequenceException;

public class PacmanExample {
    volatile StatefulKnowledgeSession ksession = null;
    PacMan                   pacMan;

    public static void main(String[] args) {
        new PacmanExample().init(true);
    }

    public PacmanExample() {
    }

    public void init(boolean exitOnClose) {
        initKsession();
        buildGrid();
        initGui(exitOnClose);
        runKSession();
    }

    public void initKsession() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "base.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "key-handlers.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "pacman.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "monster.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        this.ksession = kbase.newStatefulKnowledgeSession();

        this.pacMan = new PacMan();
        this.pacMan.setSpeed( 3 );
        this.ksession.insert( this.pacMan );

        Monster monster = new Monster();
        monster.setSpeed( 5 );
        this.ksession.insert( monster );

        this.ksession.insert( new Score() );

//        KnowledgeRuntimeLoggerFactory.newThreadedFileLogger( this.ksession,
//                                                             "log/pacman.log",
//                                                             3000 );

        Location pacLocation = new Location( this.pacMan,
                                             1,
                                             5 );

        Location monLocation = new Location( monster,
                                             10,
                                             5 );

        this.ksession.insert( pacLocation );
        this.ksession.insert( monLocation );

        Tick tick = new Tick( 0 );
        this.ksession.insert( tick );
    }

    public void buildGrid() {

        BufferedReader reader;
        List<String> lines = new ArrayList<String>();
        try {
            reader = new BufferedReader( ResourceFactory.newClassPathResource("grid1.dat",
                    PacmanExample.class).getReader() );

            String line;
            while ( (line = reader.readLine()) != null ) {
                lines.add( line );
            }
        } catch (IOException e) {
            throw new IllegalStateException("Reading dat file failed.", e);
        }

        for ( int row = lines.size() - 1; row >= 0; row-- ) {
            String line = lines.get( row );
            int whiteCellCount = 0;
            for ( int col = 0; col < line.length(); col++ ) {
                char c = line.charAt( col );
                
                Cell cell = new Cell( lines.size() - row - 1,
                                      col - whiteCellCount ); // use white spaces for layout, so need to correct
                CellContents contents = null;
                switch ( c ) {
                    case '*' : {
                        contents = new CellContents( cell,
                                                     CellType.WALL );
                        break;
                    }
                    case '.' : {
                        contents = new CellContents( cell,
                                                     CellType.FOOD );
                        break;
                    }
                    case '#' : {
                        contents = new CellContents( cell,
                                                     CellType.POWER_PILL );
                        break;
                    }
                    case '_' : {
                        contents = new CellContents( cell,
                                                     CellType.EMPTY );
                        break;
                    }
                    case ' ' : {
                        // ignore, just for spacing
                        whiteCellCount++;
                        break;
                    }
                    default : {
                        throw new IllegalArgumentException( "'" + c + "' is an invalid cell type" );
                    }
                }
                if ( contents != null ) {
                    System.out.println( cell + " : " + contents );
                    ksession.insert( cell );
                    ksession.insert( contents );
                }
            }
        }
    }

    public void initGui(boolean exitOnClose) {
        PacmanGui.createAndShowGUI( this.ksession, exitOnClose );
    }

    public void runKSession() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    // run forever
                    ksession.fireUntilHalt();
                } catch ( ConsequenceException e ) {
                    throw e;
                }
            }
        };
        Thread thread = new Thread(runnable); // In java 6 use Executors instead
        thread.start();
    }

}
