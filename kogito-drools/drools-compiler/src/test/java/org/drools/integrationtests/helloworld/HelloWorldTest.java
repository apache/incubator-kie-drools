package org.drools.integrationtests.helloworld;

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

    public void testSomething() {
        try {
            
            //load up the rulebase
            RuleBase ruleBase = readRule();
            WorkingMemory workingMemory = ruleBase.newWorkingMemory();
            
            //go !            
            Message message = new Message("hola");
            message.addToList( "hello" );
            message.setNumber( 42 );
            
            workingMemory.assertObject( message );
            
            workingMemory.fireAllRules();               
            assertTrue( message.isFired() );
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.getMessage());
        }
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

