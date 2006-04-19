package com.sample.benchmark;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

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
 * @author Peter Lin
 *
 */
public class RuleSetBenchmark extends TestCase {
	public RuleSetBenchmark() {
		super();
	}
	
	public void setUp() {
		
	}
	
	public void tearDown() {
		
	}

    private static RuleBase readRule() throws IOException, DroolsParserException, RuleIntegrationException, PackageIntegrationException, InvalidPatternException {
        //read in the source
        Reader reader = new InputStreamReader( RuleSetBenchmark.class.getResourceAsStream( "1000_rules.drl" ) );
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
	
	public void testOneThousand() {
		try {
			long loadStart = System.currentTimeMillis();
	        RuleBase ruleBase = readRule();
	        long loadEnd = System.currentTimeMillis();
	        System.out.println("time to load 1000 rules " +
	        		(loadEnd - loadStart) + "ms");
	        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
