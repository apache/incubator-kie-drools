package org.drools.benchmark.waltzdb;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;


public class WaltzDbBenchmark {
    public static void main(final String[] args) throws Exception {        	
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( WaltzDbBenchmark.class.getResourceAsStream( "waltzdb.drl" ) ) );
            Package pkg = builder.getPackage();
            
            RuleBaseConfiguration conf = new RuleBaseConfiguration();
    		conf.setRemoveIdentities( true );
    		final RuleBase ruleBase = RuleBaseFactory.newRuleBase(RuleBase.RETEOO, conf);
           
    		ruleBase.addPackage( pkg );
           
            StatefulSession session = ruleBase.newStatefulSession();
            
            java.util.List lines = WaltzDbBenchmark.loadLines("waltzdb16.dat"); //12,8,4
            java.util.List labels = WaltzDbBenchmark.loadLabels("waltzdb16.dat"); //12,8,4
            long now = System.currentTimeMillis();
        	Iterator iter =lines.iterator();
        	while(iter.hasNext()){
        		Line line = (Line)iter.next();
        		session.insert(line);
        		System.out.println(line.getP1() + " " +  line.getP2());
        	}
        	
        	iter =labels.iterator();
        	while(iter.hasNext()){
        		Label label = (Label)iter.next();
        		session.insert(label);
        		System.out.println(label.getId() + " " +  label.getType());
        	}
            
            Stage stage = new Stage(Stage.DUPLICATE);
            session.insert( stage );
            session.fireAllRules();        
        	System.out.println("Time: " + (System.currentTimeMillis() - now));
        	session.dispose();
        
    }

    private static java.util.List loadLines(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader( WaltzDbBenchmark.class.getResourceAsStream( filename ) ));
        Pattern pat = Pattern.compile( ".*make line \\^p1 ([0-9]*) \\^p2 ([0-9]*).*" );
        String line = reader.readLine();
        java.util.List result = new java.util.ArrayList();
        while(line != null) {
            Matcher m = pat.matcher( line );
            if(m.matches()) {
                Line l = new Line(Integer.parseInt( m.group( 1 ) ),
                                  Integer.parseInt( m.group( 2 ) ) );
                result.add(l);
            }
            line = reader.readLine();
        }
        reader.close();
        return result;
    }
    private static java.util.List loadLabels(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader( WaltzDbBenchmark.class.getResourceAsStream( filename ) ));
        Pattern pat = Pattern.compile( ".*make label \\^type ([0-9a-z]*) \\^name ([0-9a-zA-Z]*) \\^id ([0-9]*) \\^n1 ([B+-]*) \\^n2 ([B+-]*)( \\^n3 ([B+-]*))?.*" );
        String line = reader.readLine();
        java.util.List result = new java.util.ArrayList();
        while(line != null) {
            Matcher m = pat.matcher( line );
            if(m.matches()) {
                Label l = new Label(m.group( 1 ),m.group( 2 ), m.group(3) , m.group(4), m.group(5), m.group(6) );
                result.add(l);
            }
            line = reader.readLine();
        }
        reader.close();
        return result;
    }
}
