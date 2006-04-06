package org.drools.integrationtests.waltz;

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
import org.drools.event.DebugAgendaEventListener;
import org.drools.event.DebugWorkingMemoryEventListener;
import org.drools.lang.descr.PackageDescr;
import org.drools.reteoo.RuleBaseImpl;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Package;
 
/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class WaltzTest extends TestCase {

    public void testWaltz() {
        try {
            
            //load up the rulebase
            RuleBase ruleBase = readRule();
            WorkingMemory workingMemory = ruleBase.newWorkingMemory();
            
            DebugWorkingMemoryEventListener wmListener = new DebugWorkingMemoryEventListener();
            DebugAgendaEventListener agendaListener = new DebugAgendaEventListener();
            workingMemory.addEventListener( wmListener );
            workingMemory.addEventListener( agendaListener );
            
            //go !            
            Stage stage = new Stage(Stage.START);
            workingMemory.assertObject( stage );
            workingMemory.fireAllRules();               
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
        Reader reader = new InputStreamReader( WaltzTest.class.getResourceAsStream( "waltz.drl" ) );
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

