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

package org.drools.benchmark.waltzdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * This example is incomplete, it run's, but is no way near correct.
 *
 */
public class WaltzDbBenchmark {
    public static void main(final String[] args) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "waltzdb.drl",
                                                                    WaltzDbBenchmark.class ),
                              ResourceType.DRL );
        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

        KnowledgeBaseConfiguration kbaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbaseConfiguration.setProperty( "drools.removeIdentities",
                                        "true" );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConfiguration );
        //                final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
        //                                                               conf );

        kbase.addKnowledgePackages( pkgs );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Line> lines = WaltzDbBenchmark.loadLines( "waltzdb16.dat" ); //12,8,4
        List<Label> labels = WaltzDbBenchmark.loadLabels( "waltzdb16.dat" ); //12,8,4
        long now = System.currentTimeMillis();
        for ( Line line: lines ) {
            ksession.insert( line );
            System.out.println( line.getP1() + " " + line.getP2() );
        }
        for ( Label label: labels ) {
            ksession.insert( label );
            System.out.println( label.getId() + " " + label.getType() );
        }

        Stage stage = new Stage( Stage.DUPLICATE );
        ksession.insert( stage );
        ksession.fireAllRules();
        System.out.println( "Time: " + (System.currentTimeMillis() - now) );
        ksession.dispose();

    }

    private static List<Line> loadLines(String filename) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( WaltzDbBenchmark.class.getResourceAsStream( filename ) ) );
        Pattern pat = Pattern.compile( ".*make line \\^p1 ([0-9]*) \\^p2 ([0-9]*).*" );
        String line = reader.readLine();
        List<Line> result = new ArrayList<Line>();
        while ( line != null ) {
            Matcher m = pat.matcher( line );
            if ( m.matches() ) {
                Line l = new Line( Integer.parseInt( m.group( 1 ) ),
                                   Integer.parseInt( m.group( 2 ) ) );
                result.add( l );
            }
            line = reader.readLine();
        }
        reader.close();
        return result;
    }

    private static List<Label> loadLabels(String filename) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( WaltzDbBenchmark.class.getResourceAsStream( filename ) ) );
        Pattern pat = Pattern.compile( ".*make label \\^type ([0-9a-z]*) \\^name ([0-9a-zA-Z]*) \\^id ([0-9]*) \\^n1 ([B+-]*) \\^n2 ([B+-]*)( \\^n3 ([B+-]*))?.*" );
        String line = reader.readLine();
        List<Label> result = new ArrayList<Label>();
        while ( line != null ) {
            Matcher m = pat.matcher( line );
            if ( m.matches() ) {
                Label l = new Label( m.group( 1 ),
                                     m.group( 2 ),
                                     m.group( 3 ),
                                     m.group( 4 ),
                                     m.group( 5 ),
                                     m.group( 6 ) );
                result.add( l );
            }
            line = reader.readLine();
        }
        reader.close();
        return result;
    }
}
