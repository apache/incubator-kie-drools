/**
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

package org.drools.benchmark.waltz;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemory;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public abstract class WaltzBenchmark {

    public static void main(final String[] args) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "waltz.drl",
                                                                    WaltzBenchmark.class ),
                              ResourceType.DRL );
        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
        //add the package to a kbase
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );

        long totalTime = 0;
        for ( int i = 0; i < 5; i++ ) {
            StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
    
            String filename;
            if ( args.length != 0 ) {
                String arg = args[0];
                filename = arg;
            } else {
                filename = "waltz50.dat";
            }
    
            loadLines( session,
                       filename );
    
            Stage stage = new Stage( Stage.DUPLICATE );
            session.insert( stage );
    
            long start = System.currentTimeMillis();
            session.setGlobal( "time",
                               start );
            session.fireAllRules();
            long time = System.currentTimeMillis() - start;
            System.err.println( time );
            totalTime += time;
            session.dispose();
        }
        System.out.println( "average : " + totalTime / 5 );
    }

    private static void loadLines(WorkingMemory wm,
                                  String filename) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( WaltzBenchmark.class.getResourceAsStream( filename ) ) );
        Pattern pat = Pattern.compile( ".*make line \\^p1 ([0-9]*) \\^p2 ([0-9]*).*" );
        String line = reader.readLine();
        while ( line != null ) {
            Matcher m = pat.matcher( line );
            if ( m.matches() ) {
                Line l = new Line( Integer.parseInt( m.group( 1 ) ),
                                   Integer.parseInt( m.group( 2 ) ) );
                wm.insert( l );
            }
            line = reader.readLine();
        }
        reader.close();
    }

}