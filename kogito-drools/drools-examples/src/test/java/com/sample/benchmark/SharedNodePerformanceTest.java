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

import com.sample.benchmark.models.Address;

/**
 * @author Peter Lin
 *
 */
public class SharedNodePerformanceTest extends TestCase {
	public SharedNodePerformanceTest() {
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
        			SharedNodePerformanceTest.class.getResourceAsStream( file ) );
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
	
    /**
     * test the performance with 50 facts and 5 rules. The test measures
     * the load, assert and fire time.
     */
	public void testFiveSharedNodes() {
		try {
			int factCount = 5000;
			String file = "20rules_5shared.drl";
			int loop = 5;
			long totalload = 0;
			long totalassert = 0;
			long totalfire = 0;
			for (int c=0; c < loop; c++) {
				long loadStart = System.currentTimeMillis();
		        RuleBase ruleBase = readRule(file);
		        long loadEnd = System.currentTimeMillis();
		        long loadet = loadEnd - loadStart;
		        totalload += loadet;
		        System.out.println("time to load " + file +
		        		" " + loadet + "ms");
		        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		        ArrayList objects = new ArrayList();
		        // create the objects
		        for (int idx=0; idx < factCount; idx++) {
		        	Address addr = new Address();
		        	addr.setCity("boston");
		        	addr.setState("ma");
		        	addr.setHouseType("single family");
		        	addr.setStatus("not listed");
		        	addr.setCountry("usa");
		        	addr.setAccountId("acc" + idx);
		        	objects.add(addr);
		        }
		        Iterator itr = objects.iterator();
		        long assertStart = System.currentTimeMillis();
		        while( itr.hasNext() ) {
		        	workingMemory.assertObject(itr.next());
		        }
		        long assertEnd = System.currentTimeMillis();
		        long assertet = assertEnd - assertStart;
		        totalassert += assertet;
		        System.out.println("time to assert " + assertet +
		        		" ms");
		        long fireStart = System.currentTimeMillis();
		        workingMemory.fireAllRules();
		        long fireEnd = System.currentTimeMillis();
		        long fireet = fireEnd - fireStart;
		        totalfire += fireet;
		        System.out.println("time to fireAllRules " + fireet +
		        		" ms");
		        workingMemory.dispose();
			}
			System.out.println(file);
			System.out.println("number of objects asserted " + factCount);
			System.out.println("average load " + (totalload/loop) + " ms");
			System.out.println("average assert " + (totalassert/loop) + " ms");
			System.out.println("average fire " + (totalfire/loop) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 *
	 */
	public void testFourSharedNodes() {
		try {
			int factCount = 5000;
			String file = "20rules_4shared.drl";
			int loop = 5;
			long totalload = 0;
			long totalassert = 0;
			long totalfire = 0;
			for (int c=0; c < loop; c++) {
				long loadStart = System.currentTimeMillis();
		        RuleBase ruleBase = readRule(file);
		        long loadEnd = System.currentTimeMillis();
		        long loadet = loadEnd - loadStart;
		        totalload += loadet;
		        System.out.println("time to load " + file +
		        		" " + loadet + "ms");
		        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		        ArrayList objects = new ArrayList();
		        // create the objects
		        for (int idx=0; idx < factCount; idx++) {
		        	Address addr = new Address();
		        	addr.setCity("boston");
		        	addr.setState("ma");
		        	addr.setHouseType("single family");
		        	addr.setStatus("not listed");
		        	addr.setCountry("usa" + idx);
		        	objects.add(addr);
		        }
		        Iterator itr = objects.iterator();
		        long assertStart = System.currentTimeMillis();
		        while( itr.hasNext() ) {
		        	workingMemory.assertObject(itr.next());
		        }
		        long assertEnd = System.currentTimeMillis();
		        long assertet = assertEnd - assertStart;
		        totalassert += assertet;
		        System.out.println("time to assert " + assertet +
		        		" ms");
		        long fireStart = System.currentTimeMillis();
		        workingMemory.fireAllRules();
		        long fireEnd = System.currentTimeMillis();
		        long fireet = fireEnd - fireStart;
		        totalfire += fireet;
		        System.out.println("time to fireAllRules " + fireet +
		        		" ms");
		        workingMemory.dispose();
			}
			System.out.println(file);
			System.out.println("number of objects asserted " + factCount);
			System.out.println("average load " + (totalload/loop) + " ms");
			System.out.println("average assert " + (totalassert/loop) + " ms");
			System.out.println("average fire " + (totalfire/loop) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 *
	 */
	public void testThreeSharedNodes() {
		try {
			int factCount = 5000;
			String file = "20rules_3shared.drl";
			int loop = 5;
			long totalload = 0;
			long totalassert = 0;
			long totalfire = 0;
			for (int c=0; c < loop; c++) {
				long loadStart = System.currentTimeMillis();
		        RuleBase ruleBase = readRule(file);
		        long loadEnd = System.currentTimeMillis();
		        long loadet = loadEnd - loadStart;
		        totalload += loadet;
		        System.out.println("time to load " + file +
		        		" " + loadet + "ms");
		        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		        ArrayList objects = new ArrayList();
		        // create the objects
		        for (int idx=0; idx < factCount; idx++) {
		        	Address addr = new Address();
		        	addr.setCity("boston");
		        	addr.setState("ma");
		        	addr.setHouseType("single family");
		        	addr.setStatus("not listed" + idx);
		        	addr.setCountry("usa");
		        	objects.add(addr);
		        }
		        Iterator itr = objects.iterator();
		        long assertStart = System.currentTimeMillis();
		        while( itr.hasNext() ) {
		        	workingMemory.assertObject(itr.next());
		        }
		        long assertEnd = System.currentTimeMillis();
		        long assertet = assertEnd - assertStart;
		        totalassert += assertet;
		        System.out.println("time to assert " + assertet +
		        		" ms");
		        long fireStart = System.currentTimeMillis();
		        workingMemory.fireAllRules();
		        long fireEnd = System.currentTimeMillis();
		        long fireet = fireEnd - fireStart;
		        totalfire += fireet;
		        System.out.println("time to fireAllRules " + fireet +
		        		" ms");
		        workingMemory.dispose();
			}
			System.out.println(file);
			System.out.println("number of objects asserted " + factCount);
			System.out.println("average load " + (totalload/loop) + " ms");
			System.out.println("average assert " + (totalassert/loop) + " ms");
			System.out.println("average fire " + (totalfire/loop) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 *
	 */
	public void testTwoSharedNodes() {
		try {
			int factCount = 5000;
			String file = "20rules_2shared.drl";
			int loop = 5;
			long totalload = 0;
			long totalassert = 0;
			long totalfire = 0;
			for (int c=0; c < loop; c++) {
				long loadStart = System.currentTimeMillis();
		        RuleBase ruleBase = readRule(file);
		        long loadEnd = System.currentTimeMillis();
		        long loadet = loadEnd - loadStart;
		        totalload += loadet;
		        System.out.println("time to load " + file +
		        		" " + loadet + "ms");
		        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		        ArrayList objects = new ArrayList();
		        // create the objects
		        for (int idx=0; idx < factCount; idx++) {
		        	Address addr = new Address();
		        	addr.setCity("boston");
		        	addr.setState("ma");
		        	addr.setHouseType("single family" + idx);
		        	addr.setStatus("not listed");
		        	addr.setCountry("usa");
		        	objects.add(addr);
		        }
		        Iterator itr = objects.iterator();
		        long assertStart = System.currentTimeMillis();
		        while( itr.hasNext() ) {
		        	workingMemory.assertObject(itr.next());
		        }
		        long assertEnd = System.currentTimeMillis();
		        long assertet = assertEnd - assertStart;
		        totalassert += assertet;
		        System.out.println("time to assert " + assertet +
		        		" ms");
		        long fireStart = System.currentTimeMillis();
		        workingMemory.fireAllRules();
		        long fireEnd = System.currentTimeMillis();
		        long fireet = fireEnd - fireStart;
		        totalfire += fireet;
		        System.out.println("time to fireAllRules " + fireet +
		        		" ms");
		        workingMemory.dispose();
			}
			System.out.println(file);
			System.out.println("number of objects asserted " + factCount);
			System.out.println("average load " + (totalload/loop) + " ms");
			System.out.println("average assert " + (totalassert/loop) + " ms");
			System.out.println("average fire " + (totalfire/loop) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testOneSharedNodes() {
		try {
			int factCount = 5000;
			String file = "20rules_1shared.drl";
			int loop = 5;
			long totalload = 0;
			long totalassert = 0;
			long totalfire = 0;
			for (int c=0; c < loop; c++) {
				long loadStart = System.currentTimeMillis();
		        RuleBase ruleBase = readRule(file);
		        long loadEnd = System.currentTimeMillis();
		        long loadet = loadEnd - loadStart;
		        totalload += loadet;
		        System.out.println("time to load " + file +
		        		" " + loadet + "ms");
		        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		        ArrayList objects = new ArrayList();
		        // create the objects
		        for (int idx=0; idx < factCount; idx++) {
		        	Address addr = new Address();
		        	addr.setCity("boston");
		        	addr.setState("ma" + idx);
		        	addr.setHouseType("single family");
		        	addr.setStatus("not listed");
		        	addr.setCountry("usa");
		        	objects.add(addr);
		        }
		        Iterator itr = objects.iterator();
		        long assertStart = System.currentTimeMillis();
		        while( itr.hasNext() ) {
		        	workingMemory.assertObject(itr.next());
		        }
		        long assertEnd = System.currentTimeMillis();
		        long assertet = assertEnd - assertStart;
		        totalassert += assertet;
		        System.out.println("time to assert " + assertet +
		        		" ms");
		        long fireStart = System.currentTimeMillis();
		        workingMemory.fireAllRules();
		        long fireEnd = System.currentTimeMillis();
		        long fireet = fireEnd - fireStart;
		        totalfire += fireet;
		        System.out.println("time to fireAllRules " + fireet +
		        		" ms");
		        workingMemory.dispose();
			}
			System.out.println(file);
			System.out.println("number of objects asserted " + factCount);
			System.out.println("average load " + (totalload/loop) + " ms");
			System.out.println("average assert " + (totalassert/loop) + " ms");
			System.out.println("average fire " + (totalfire/loop) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testNoneSharedNodes() {
		try {
			int factCount = 5000;
			String file = "20rules_0shared.drl";
			int loop = 5;
			long totalload = 0;
			long totalassert = 0;
			long totalfire = 0;
			for (int c=0; c < loop; c++) {
				long loadStart = System.currentTimeMillis();
		        RuleBase ruleBase = readRule(file);
		        long loadEnd = System.currentTimeMillis();
		        long loadet = loadEnd - loadStart;
		        totalload += loadet;
		        System.out.println("time to load " + file +
		        		" " + loadet + "ms");
		        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		        ArrayList objects = new ArrayList();
		        // create the objects
		        for (int idx=0; idx < factCount; idx++) {
		        	Address addr = new Address();
		        	addr.setAccountId("acc" + idx);
		        	addr.setCity("boston");
		        	addr.setState("ma");
		        	addr.setHouseType("single family");
		        	addr.setStatus("not listed");
		        	addr.setCountry("usa");
		        	objects.add(addr);
		        }
		        Iterator itr = objects.iterator();
		        long assertStart = System.currentTimeMillis();
		        while( itr.hasNext() ) {
		        	workingMemory.assertObject(itr.next());
		        }
		        long assertEnd = System.currentTimeMillis();
		        long assertet = assertEnd - assertStart;
		        totalassert += assertet;
		        System.out.println("time to assert " + assertet +
		        		" ms");
		        long fireStart = System.currentTimeMillis();
		        workingMemory.fireAllRules();
		        long fireEnd = System.currentTimeMillis();
		        long fireet = fireEnd - fireStart;
		        totalfire += fireet;
		        System.out.println("time to fireAllRules " + fireet +
		        		" ms");
		        workingMemory.dispose();
			}
			System.out.println(file);
			System.out.println("number of objects asserted " + factCount);
			System.out.println("average load " + (totalload/loop) + " ms");
			System.out.println("average assert " + (totalassert/loop) + " ms");
			System.out.println("average fire " + (totalfire/loop) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
