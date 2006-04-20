package com.sample.benchmark;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

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

import com.sample.benchmark.models.Account;

/**
 * @author Peter Lin
 *
 */
public class RuleSetLoad extends TestCase {
	public RuleSetLoad() {
		super();
	}
	
	public void setUp() {
		
	}
	
	public void tearDown() {
		
	}

    private static RuleBase readRule(String file) throws IOException, DroolsParserException, RuleIntegrationException, PackageIntegrationException, InvalidPatternException {
        //read in the source
        Reader reader = 
        	new InputStreamReader( 
        			RuleSetLoad.class.getResourceAsStream( file ) );
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
	
	public void testOneThousandLoad() {
		try {
			String file = "1000_rules.drl";
			long loadStart = System.currentTimeMillis();
	        RuleBase ruleBase = readRule(file);
	        long loadEnd = System.currentTimeMillis();
	        System.out.println("time to load " + file +
	        		" " + (loadEnd - loadStart) + "ms");
	        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testFourThousandLoad() {
		try {
			String file = "4000_rules.drl";
			long loadStart = System.currentTimeMillis();
	        RuleBase ruleBase = readRule(file);
	        long loadEnd = System.currentTimeMillis();
	        System.out.println("time to load " + file +
	        		" " + (loadEnd - loadStart) + "ms");
	        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
