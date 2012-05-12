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

package org.drools.integrationtests;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.Person;
import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.Triple;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run all the tests with the ReteOO engine implementation
 */
public class MiscTestJMV extends CommonTestMethodBase {

//    private static Logger logger = LoggerFactory.getLogger(MiscTestJMV.class);

    @Test
    public void testJBRULES3488() throws Exception {

    	StringBuilder rule = new StringBuilder();
    	rule.append("package net.sf.eulergui.accumulate_test;\n");
    	rule.append("import org.drools.Triple;\n");

    	rule.append("rule \"accumulate 2 times\"\n");
    	rule.append("when\n");
    	rule.append("  $LIST : java.util.List( )" +
    			"  from accumulate( $Triple_1 : Triple( $CN : subject," +
    			"    predicate == \"<http://deductions.sf.net/samples/princing.n3p.n3#number>\", $N : object )," +
    			"      collectList( $N ) )\n" +
    			"  $NUMBER : Number() from accumulate(" +
    			"    $NUMBER_STRING_ : String() from $LIST , sum( Double.parseDouble( $NUMBER_STRING_)) )\n" );
    	rule.append("then\n");
    	rule.append("  System.out.println(\"ok\");\n");
    	rule.append("end\n");
    	
		System.setProperty("drools.dialect.mvel.strict", "false");
		final RuleBaseConfiguration conf = new RuleBaseConfiguration();
		conf.setAssertBehaviour( AssertBehaviour.EQUALITY );
		RuleBase ruleBase = RuleBaseFactory.newRuleBase(conf);
		
    	StatefulSession ss = ruleBase.newStatefulSession();
    	// To reproduce, Need to have 3 object asserted (not less) :
    	ss.insert( new Triple( "<http://deductions.sf.net/samples/princing.n3p.n3#CN1>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "200" ) );
    	ss.insert( new Triple( "<http://deductions.sf.net/samples/princing.n3p.n3#CN2>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "100" ) );
    	ss.insert( new Triple( "<http://deductions.sf.net/samples/princing.n3p.n3#CN3>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "100" ) );
    	ss.insert( new Triple( "<CN4>", "<number>", "100" ) );

    	final PackageBuilder builder = new PackageBuilder();
    	builder.addPackageFromDrl( new StringReader( rule.toString() ) );
    	final Package pkg = builder.getPackage();
    	ruleBase.addPackage( pkg );
    	ss.fireAllRules();
    	ss.dispose();
    }

    @Test
    public void testDeclaredTypesDefaultHashCode() {
        // JBRULES-3481
        String str = "package com.sample\n" +
                "\n" +
                "global java.util.List list; \n" +
                "" +
                "declare Bean\n" +
                " id : int \n" +
                "end\n" +
                "\n" +
                "declare KeyedBean\n" +
                " id : int @key \n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule Create\n" +
                "when\n" +
                "then\n" +
                " list.add( new Bean(1) ); \n" +
                " list.add( new Bean(2) ); \n" +
                " list.add( new KeyedBean(1) ); \n" +
                " list.add( new KeyedBean(1) ); \n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();

        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        ksession.dispose();

        assertFalse( list.get( 0 ).hashCode() == 34 );
        assertFalse( list.get( 1 ).hashCode() == 34 );
        assertFalse( list.get( 0 ).hashCode() == list.get( 1 ).hashCode() );
        assertNotSame( list.get( 0 ), list.get( 1 ) );
        assertFalse( list.get( 0 ).equals( list.get( 1 ) ) );

        assertTrue( list.get( 2 ).hashCode() == 32 );
        assertTrue( list.get( 3 ).hashCode() == 32 );
        assertNotSame( list.get( 2 ), list.get( 3 ) );
        assertTrue( list.get( 2 ).equals( list.get( 3 ) ) );

    }

    @Test
    // was failing, now empty result 
    public void testJMV2() throws Exception {
    	String drl = "" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
    			"   $Triple_1 : Triple( $X : subject, $Y : predicate, $Z : object )" +
    			"   $Triple_2 : Triple( subject == $X, $P0 : predicate, $V0 : object )" +
    			"   Triple( this == $Triple_2 , $V0.toString().toLowerCase().contains( \"Giant\" .toString().toLowerCase() ) )" +
    			" end";
    	testOneQuery( drl );
    }

    
    @Test
    public void testJMV3() throws Exception {
    	// java.lang.InstantiationError: java.lang.CharSequence
    	testOneQuery("package com.sample\n" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
				"   $Triple_2 : Triple( $X : subject, $P0 : predicate, $V0 : object , $V0.contains( \"Giant\" ) )\n" +
    			"end");
    }

    @Test
    // now empty result 
   public void testJMV31() throws Exception {
    	String drl0 = "package com.sample\n" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
    			"   $Triple_2 : Triple( $X : subject, $P0 : predicate, $V0 : object )" +
    			"   Triple( this == $Triple_2 , $V0.toString().toLowerCase().contains( \"Giant\" .toString().toLowerCase() ) )" +
    			" end";
    	testOneQuery( drl0 );
    }
    
    @Test
    // now empty result 
    public void testJMV32() throws Exception {
    	String drl1 = "package com.sample\n" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
				"   $Triple_2 : Triple( $X : subject, $P0 : predicate, $V0 : object )" +
    			"   Triple( this == $Triple_2 , $V0.contains( \"Giant\" ) )" +
    			" end";
    	testOneQuery( drl1 );
    }

    @Test
    public void testJMV4() throws Exception {
    	testOneQuery("package com.sample\n" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
				"   $Triple_2 : Triple( $X : subject, $P0 : predicate, $V0 : object )" +
    			"   Triple( this == $Triple_2 , $V0.contains( \"Giant\" ) )" +
    			" end" );
    }

	private void testOneQuery(String drl) throws DroolsParserException,
			IOException {
		System.setProperty("drools.dialect.mvel.strict", "false");
    	final RuleBaseConfiguration conf = new RuleBaseConfiguration();
    	conf.setAssertBehaviour( AssertBehaviour.EQUALITY );
    	RuleBase ruleBase = RuleBaseFactory.newRuleBase(conf);

    	final PackageBuilder builder = new PackageBuilder();
    	builder.addPackageFromDrl( new StringReader( drl ) );
    	final Package pkg = builder.getPackage();
    	ruleBase.addPackage( pkg );

    	StatefulSession ss = ruleBase.newStatefulSession();
    	ss.insert( new Triple( "<http://deductions.sf.net/samples/n3#CN1>", "<http://deductions.sf.net/n3#number>", "Giant" ) );

    	QueryResults res = ss.getQueryResults( "persistent_audio: ");
    	System.out.println("MiscTest.testJMV2() "  +res.size() );
    	Assert.assertEquals( 1, res.size() );

    	Iterator<QueryResult> it = res.iterator();
    	while (it.hasNext()) {
			QueryResult r = (QueryResult) it.next();
			System.out.println( r );
		}
    	System.out.println( "KB");
        for ( Object element : ss.getFactHandles() ) {
            System.out.println( element );			
		}
    	ss.dispose();
	}
 
    @Test
   public void testJittingConstraintWithInvocationOnLiteral() {
        String str = "package com.sample\n" +
                "import org.drools.Person\n" +
                "rule XXX when\n" +
                "  Person( name.toString().toLowerCase().contains( \"mark\".toString().toLowerCase() ) )\n" +
                "then\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(new Person("mark", 37));
        ksession.insert(new Person("mario", 38));

        ksession.fireAllRules();
        for ( Object element : ksession.getObjects()) {
            System.out.println( element );			
		}
        ksession.dispose();
    }
}

