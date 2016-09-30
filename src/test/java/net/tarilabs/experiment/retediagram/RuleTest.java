/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package net.tarilabs.experiment.retediagram;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.reteoo.ReteDumper;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tarilabs.experiment.retediagram.ReteDiagram.Pref;
import net.tarilabs.model.Measurement;

public class RuleTest extends CommonTestMethodBase {
	static final Logger LOG = LoggerFactory.getLogger(RuleTest.class);
	
	@Test
	public void test() {
	    System.out.println("x");
		KieServices kieServices = KieServices.Factory.get();
        
		KieBase kieBase = new KieHelper()
		        .addFromClassPath("/rules.drl")
		        .build();

	    LOG.info("Creating kieSession");
	    KieSession session = kieBase.newKieSession();
        
        LOG.info("Populating globals");
        Set<String> check = new HashSet<String>();
        session.setGlobal("controlSet", check);
        
        LOG.info("Now running data");
        
        Measurement mRed= new Measurement("color", "red");
        session.insert(mRed);
        session.fireAllRules();
        
        Measurement mGreen= new Measurement("color", "green");
        session.insert(mGreen);
        session.fireAllRules();
        
        Measurement mBlue= new Measurement("color", "blue");
        session.insert(mBlue);
        session.fireAllRules();
        
        LOG.info("Final checks");

	    assertEquals("Size of object in Working Memory is 3", 3, session.getObjects().size());
	    assertTrue("contains red", check.contains("red"));
	    assertTrue("contains green", check.contains("green"));
	    assertTrue("contains blue", check.contains("blue"));
	    
	    ReteDumper.dumpRete(session);
	    System.out.println("---");
	    ReteDiagram.diagramRete(((InternalKnowledgeBase)session.getKieBase()).getRete(), Pref.VLEVEL);
	}
	
	@Test
    public void testManyAccumulatesWithSubnetworks() {
        String drl = "package org.drools.compiler.tests; \n" +
                     "" +
                     "declare FunctionResult\n" +
                     "    father  : Applied\n" +
                     "end\n" +
                     "\n" +
                     "declare Field\n" +
                     "    applied : Applied\n" +
                     "end\n" +
                     "\n" +
                     "declare Applied\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "rule \"Seed\"\n" +
                     "when\n" +
                     "then\n" +
                     "    Applied app = new Applied();\n" +
                     "    Field fld = new Field();\n" +
                     "\n" +
                     "    insert( app );\n" +
                     "    insert( fld );\n" +
                     "end\n" +
                     "\n" +
                     "\n" +
                     "\n" +
                     "\n" +
                     "rule \"complexSubNetworks\"\n" +
                     "when\n" +
                     "    $fld : Field( $app : applied )\n" +
                     "    $a : Applied( this == $app )\n" +
                     "    accumulate (\n" +
                     "        $res : FunctionResult( father == $a ),\n" +
                     "        $args : collectList( $res )\n" +
                     "    )\n" +
                     "    accumulate (\n" +
                     "        $res : FunctionResult( father == $a ),\n" +
                     "        $deps : collectList( $res )\n" +
                     "    )\n" +
                     "    accumulate (\n" +
                     "        $x : String()\n" +
                     "        and\n" +
                     "        not String( this == $x ),\n" +
                     "        $exprFieldList : collectList( $x )\n" +
                     "    )\n" +
                     "then\n" +
                     "end\n" +
                     "\n";

        KnowledgeBuilderConfiguration kbConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        int num = ksession.fireAllRules();
        // only one rule should fire, but the partial propagation of the asserted facts should not cause a runtime NPE
        assertEquals( 1, num );
        ReteDiagram.diagramRete((KieSession)ksession);
    }


    @Test
    public void testLinkRiaNodesWithSubSubNetworks() {
        String drl = "package org.drools.compiler.tests; \n" +
                     "" +
                     "import java.util.*; \n" +
                     "" +
                     "global List list; \n" +
                     "" +
                     "declare MyNode\n" +
                     "end\n" +
                     "" +
                     "rule Init\n" +
                     "when\n" +
                     "then\n" +
                     "    insert( new MyNode() );\n" +
                     "    insert( new MyNode() );\n" +
                     "end\n" +
                     "" +
                     "" +
                     "rule \"Init tree nodes\"\n" +
                     "salience -10\n" +
                     "when\n" +
                     "    accumulate (\n" +
                     "                 MyNode(),\n" +
                     "                 $x : count( 1 )\n" +
                     "               )\n" +
                     "    accumulate (\n" +
                     "                 $n : MyNode()\n" +
                     "                 and\n" +
                     "                 accumulate (\n" +
                     "                    $val : Double( ) from Arrays.asList( 1.0, 2.0, 3.0 ),\n" +
                     "                    $rc : count( $val );\n" +
                     "                    $rc == 3 \n" +
                     "                 ),\n" +
                     "                 $y : count( $n )\n" +
                     "               )\n" +
                     "then\n" +
                     "  list.add( $x ); \n" +
                     "  list.add( $y ); \n" +
                     "  System.out.println( $x ); \n" +
                     "  System.out.println( $y ); \n" +
                     "end\n";

        KnowledgeBuilderConfiguration kbConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        KnowledgeBase kbase = loadKnowledgeBaseFromString( kbConf, drl );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<Long> list = new ArrayList<Long>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( 2, list.get( 0 ).intValue() );
        assertEquals( 2, list.get( 1 ).intValue() );
        
        ReteDiagram.diagramRete((KieSession)ksession);
    }
    
    @Test
    public void testMArio() {
        String drl =
                "rule R1y ruleflow-group \"Y\" when\n" +
                "    Integer() \n" +
                "    Number() from accumulate ( Integer( ) and $s : String( ) ; count($s) )\n" +
                "then\n" +
                "    System.out.println(\"R1\");" +
                "end\n" +
                "\n" +
                "rule R1x ruleflow-group \"X\" when\n" +
                "    Integer() \n" +
                "    Number() from accumulate ( Integer( ) and $s : String( ) ; count($s) )\n" +
                "then\n" +
                "    System.out.println(\"R1\");" +
                "end\n" +
                "" +
                "rule R2 ruleflow-group \"X\" when\n" +
                "    $i : Integer()\n" +
                "then\n" +
                "    System.out.println(\"R2\");" +
                "    update($i);" +
                "end\n";
 
        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();
 
        ReteDiagram.diagramRete(((InternalKnowledgeBase)kieSession.getKieBase()).getRete(), Pref.VLEVEL);
    }
}