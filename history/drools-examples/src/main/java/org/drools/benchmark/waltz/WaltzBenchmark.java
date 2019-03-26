/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.util.IoUtils;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.RuleRuntime;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public abstract class WaltzBenchmark {

    public static void main(final String[] args) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource("waltz.drl",
                WaltzBenchmark.class),
                              ResourceType.DRL );
        Collection<KiePackage> pkgs = kbuilder.getKnowledgePackages();
        //add the package to a kbase
        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( pkgs );

        long totalTime = 0;
        for ( int i = 0; i < 5; i++ ) {
            KieSession session = kbase.newKieSession();
    
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

    private static void loadLines(RuleRuntime wm,
                                  String filename) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader( WaltzBenchmark.class.getResourceAsStream( "data/" + filename ),
                                           IoUtils.UTF8_CHARSET ) );
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
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read file with filename (" + filename + ").", e);
        }
    }

}
