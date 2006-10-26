package org.benchmarks.waltz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.benchmarks.Benchmark;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

public class DroolsWaltz
    implements
    Benchmark {
        
    WorkingMemory workingMemory;
    public void init() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( DroolsWaltz.class.getResourceAsStream( "waltz.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );        
        workingMemory = ruleBase.newWorkingMemory();    
    }

    public void assertObjects() throws Exception {
        loadLines( this.workingMemory,
                   "waltz50.dat" );  
        Stage stage = new Stage(Stage.DUPLICATE);
        workingMemory.assertObject( stage );
        System.out.println( "help" );
    }

    public void fireAllRules() throws Exception {
        workingMemory.fireAllRules();
    }

    private void loadLines(final WorkingMemory wm,
                           final String filename) throws IOException {
      final BufferedReader br = new BufferedReader( new InputStreamReader( DroolsWaltz.class.getResourceAsStream( filename ) ) );

        String textLine;
        while ( (textLine = br.readLine()) != null ) {
            if ( textLine.trim().length() == 0 || textLine.trim().startsWith( ";" ) ) {
                continue;
            }
            final StringTokenizer st = new StringTokenizer( textLine,
                                                            "() " );
            final String type = st.nextToken();

            if ( "line".equals( type ) ) {
                Line line = new Line();
                String  field = st.nextToken();
                if ( field.equals( "p1" ) ){
                    String value = st.nextToken(); 
                    line.setP1( Integer.parseInt( value ) );
                } 
                
                field = st.nextToken();
                if ( field.equals( "p2" ) ) {
                    String value = st.nextToken();
                    line.setP2( Integer.parseInt( value ) );
                } 

                wm.assertObject( line );
            }
        }
        br.close();

    }
}