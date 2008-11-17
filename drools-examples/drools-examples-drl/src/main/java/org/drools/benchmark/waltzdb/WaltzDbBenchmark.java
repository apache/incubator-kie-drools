package org.drools.benchmark.waltzdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.definition.KnowledgePackage;
import org.drools.runtime.StatefulKnowledgeSession;

public class WaltzDbBenchmark {
    public static void main(final String[] args) throws Exception {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.addResource( new InputStreamReader( WaltzDbBenchmark.class.getResourceAsStream( "waltzdb.drl" ) ),
                             KnowledgeType.DRL );
        Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();

        KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setProperty( "drools.removeIdentities", "true" );

        final KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( conf );
//                final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
//                                                               conf );

        knowledgeBase.addKnowledgePackages( pkgs );

        StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();

        java.util.List lines = WaltzDbBenchmark.loadLines( "waltzdb16.dat" ); //12,8,4
        java.util.List labels = WaltzDbBenchmark.loadLabels( "waltzdb16.dat" ); //12,8,4
        long now = System.currentTimeMillis();
        Iterator iter = lines.iterator();
        while ( iter.hasNext() ) {
            Line line = (Line) iter.next();
            session.insert( line );
            System.out.println( line.getP1() + " " + line.getP2() );
        }

        iter = labels.iterator();
        while ( iter.hasNext() ) {
            Label label = (Label) iter.next();
            session.insert( label );
            System.out.println( label.getId() + " " + label.getType() );
        }

        Stage stage = new Stage( Stage.DUPLICATE );
        session.insert( stage );
        session.fireAllRules();
        System.out.println( "Time: " + (System.currentTimeMillis() - now) );
        session.dispose();

    }

    private static java.util.List loadLines(String filename) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( WaltzDbBenchmark.class.getResourceAsStream( filename ) ) );
        Pattern pat = Pattern.compile( ".*make line \\^p1 ([0-9]*) \\^p2 ([0-9]*).*" );
        String line = reader.readLine();
        java.util.List result = new java.util.ArrayList();
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

    private static java.util.List loadLabels(String filename) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( WaltzDbBenchmark.class.getResourceAsStream( filename ) ) );
        Pattern pat = Pattern.compile( ".*make label \\^type ([0-9a-z]*) \\^name ([0-9a-zA-Z]*) \\^id ([0-9]*) \\^n1 ([B+-]*) \\^n2 ([B+-]*)( \\^n3 ([B+-]*))?.*" );
        String line = reader.readLine();
        java.util.List result = new java.util.ArrayList();
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
