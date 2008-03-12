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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
 
/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public abstract class WaltzBenchmark {

    public static void main(final String[] args) throws Exception {
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( WaltzBenchmark.class.getResourceAsStream( "waltz.drl" ) ) );
            Package pkg = builder.getPackage();
            //add the package to a rulebase
            RuleBaseConfiguration conf = new RuleBaseConfiguration();
            //conf.setAlphaMemory( true );
            conf.setShadowProxy( false );
            final RuleBase ruleBase = RuleBaseFactory.newRuleBase( conf );
            ruleBase.addPackage( pkg );
            
            StatefulSession session = ruleBase.newStatefulSession();
            
            String filename;
            if (  args.length != 0 ) {
                String arg = args[0];                
                filename  = arg;                
            } else {
                filename  = "waltz12.dat";
            }
            
            loadLines( session, filename );
            
            Stage stage = new Stage(Stage.DUPLICATE);
            session.insert( stage );
                        
            long start = System.currentTimeMillis();
            session.setGlobal( "time", start );
            session.fireAllRules();
            System.out.println( (System.currentTimeMillis() - start) / 1000 );
            session.dispose();
    }

    
    private static void loadLines(WorkingMemory wm, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader( WaltzBenchmark.class.getResourceAsStream( filename ) ));
        Pattern pat = Pattern.compile( ".*make line \\^p1 ([0-9]*) \\^p2 ([0-9]*).*" );
        String line = reader.readLine();
        while(line != null) {
            Matcher m = pat.matcher( line );
            if(m.matches()) {
                Line l = new Line(Integer.parseInt( m.group( 1 ) ),
                                  Integer.parseInt( m.group( 2 ) ) );
                wm.insert( l );
            }
            line = reader.readLine();
        }
        reader.close();
    }
    
}