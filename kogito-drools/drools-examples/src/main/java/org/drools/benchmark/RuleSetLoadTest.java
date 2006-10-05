package org.drools.benchmark;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.RuleIntegrationException;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Package;

/**
 * @author Peter Lin
 *
 */
public class RuleSetLoadTest {
    
    public static final void main(final String[] args) {
        RuleSetLoadTest test = new RuleSetLoadTest();
        test.testOneThousandLoad();
        
        test.testOneThousandLoad();
        
        test.testOneThousandLoad();
        
        // generate the drl first to run this test
        //test.testFourThousandLoad();
    }

    private static RuleBase readRule(String file) throws Exception {
        //read in the source
        Reader reader = new InputStreamReader( RuleSetLoadTest.class.getResourceAsStream( file ) );
        DrlParser parser = new DrlParser();
        PackageDescr packageDescr = parser.parse( reader );

        //pre build the package
        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        Package pkg = builder.getPackage();

        //add the package to a rulebase
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        return ruleBase;
    }

    public void testOneThousandLoad() {
        try {
            String file = "500_rules.drl";
            long loadStart = System.currentTimeMillis();
            RuleBase ruleBase = readRule( file );
            long loadEnd = System.currentTimeMillis();
            System.out.println( "time to load " + file + " " + (loadEnd - loadStart) + "ms" );
            WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void testFourThousandLoad() {
        try {
            String file = "4000_rules.drl";
            long loadStart = System.currentTimeMillis();
            RuleBase ruleBase = readRule( file );
            long loadEnd = System.currentTimeMillis();
            System.out.println( "time to load " + file + " " + (loadEnd - loadStart) + "ms" );
            WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
