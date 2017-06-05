/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.util.IoUtils;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.KiePackage;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This example is incomplete, it run's, but is no way near correct.
 */
public class WaltzDbBenchmark {
    public static void main(final String[] args) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource("waltzdb.drl",
                WaltzDbBenchmark.class),
                              ResourceType.DRL );
        Collection<KiePackage> pkgs = kbuilder.getKnowledgePackages();

        KieBaseConfiguration kbaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbaseConfiguration.setProperty( "drools.removeIdentities",
                                        "true" );

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConfiguration );
        //                final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
        //                                                               conf );

        kbase.addPackages( pkgs );

        KieSession ksession = kbase.newKieSession();

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

    private static List<Line> loadLines(String filename) {
        List<Line> result = new ArrayList<Line>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader( WaltzDbBenchmark.class.getResourceAsStream( "data/" + filename ),
                                           IoUtils.UTF8_CHARSET ) );
            Pattern pat = Pattern.compile( ".*make line \\^p1 ([0-9]*) \\^p2 ([0-9]*).*" );
            String line = reader.readLine();
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
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read file with filename (" + filename + ").", e);
        }
        return result;
    }

    private static List<Label> loadLabels(String filename) {
        List<Label> result = new ArrayList<Label>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader( WaltzDbBenchmark.class.getResourceAsStream( "data/" + filename ),
                                           IoUtils.UTF8_CHARSET ) );
            Pattern pat = Pattern.compile( ".*make label \\^type ([0-9a-z]*) \\^name ([0-9a-zA-Z]*) \\^id ([0-9]*) \\^n1 ([B+-]*) \\^n2 ([B+-]*)( \\^n3 ([B+-]*))?.*" );
            String line = reader.readLine();
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
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read file with filename (" + filename + ").", e);
        }
        return result;
    }
}
