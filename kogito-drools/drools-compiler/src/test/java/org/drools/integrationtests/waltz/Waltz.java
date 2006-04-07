package org.drools.integrationtests.waltz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public abstract class Waltz extends TestCase {

    protected abstract RuleBase getRuleBase() throws Exception;

    public void testWaltz() {
        try {
            
            //load up the rulebase
            RuleBase ruleBase = readRule();
            WorkingMemory workingMemory = ruleBase.newWorkingMemory();
            
//            DebugWorkingMemoryEventListener wmListener = new DebugWorkingMemoryEventListener();
//            DebugAgendaEventListener agendaListener = new DebugAgendaEventListener();
//            workingMemory.addEventListener( wmListener );
//            workingMemory.addEventListener( agendaListener );
            
            //go !     
            //this.loadLines( workingMemory, "waltz12.dat" );
            
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
    private RuleBase readRule() throws Exception, DroolsParserException, RuleIntegrationException, PackageIntegrationException, InvalidPatternException {
        //read in the source
        Reader reader = new InputStreamReader( Waltz.class.getResourceAsStream( "waltz.drl" ) );
        DrlParser parser = new DrlParser();
        PackageDescr packageDescr = parser.parse( reader );
        
        //pre build the package
        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        Package pkg = builder.getPackage();
        
        //add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        return ruleBase;
    }
    
    private void loadLines(WorkingMemory wm, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader( Waltz.class.getResourceAsStream( filename ) ));
        Pattern pat = Pattern.compile( ".*make line \\^p1 ([0-9]*) \\^p2 ([0-9]*).*" );
        String line = reader.readLine();
        while(line != null) {
            Matcher m = pat.matcher( line );
            if(m.matches()) {
                Line l = new Line(Integer.parseInt( m.group( 1 ) ),
                                  Integer.parseInt( m.group( 2 ) ) );
                wm.assertObject( l );
            }
            line = reader.readLine();
        }
        reader.close();
    }
    
}

