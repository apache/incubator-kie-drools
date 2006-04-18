package org.drools.integrationtests.helloworld;
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

import junit.framework.TestCase;

import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.RuleIntegrationException;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.reteoo.RuleBaseImpl;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Package;


 
/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class HelloWorldTest extends TestCase {

    public void testSomething() throws Exception {
            
            //load up the rulebase
            RuleBase ruleBase = readRule();
            WorkingMemory workingMemory = ruleBase.newWorkingMemory();
            
            //go !            
            Message message = new Message("hola");
            message.addToList( "hello" );
            message.setNumber( 42 );
            
            workingMemory.assertObject( message );
            workingMemory.assertObject( "boo" );
            workingMemory.fireAllRules();               
            assertTrue( message.isFired() );

    }

    /**
     * Please note that this is the "low level" rule assembly API.
     */
    private static RuleBase readRule() throws IOException, DroolsParserException, RuleIntegrationException, PackageIntegrationException, InvalidPatternException {
        //read in the source
        Reader reader = new InputStreamReader( HelloWorldTest.class.getResourceAsStream( "HelloWorld.drl" ) );
        DrlParser parser = new DrlParser();
        PackageDescr packageDescr = parser.parse( reader );
        
        //pre build the package
        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        Package pkg = builder.getPackage();
        
        //add the package to a rulebase
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        ruleBase.addPackage( pkg );
        return ruleBase;
    }
    
}