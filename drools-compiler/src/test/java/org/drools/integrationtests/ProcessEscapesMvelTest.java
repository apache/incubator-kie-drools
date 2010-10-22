package org.drools.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.drools.Bin;
import org.drools.BinTask;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.conf.ProcessStringEscapesOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import junit.framework.TestCase;

public class ProcessEscapesMvelTest extends TestCase {
	
	public void testProcessStringEscapesOptionOn() throws Exception {
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		
		List list = new ArrayList();
		ksession.setGlobal( "list", list );
		
		BinTask bt = new BinTask();
		Bin b = new Bin();
		b.setName("aa123bb");
		bt.setBin(b);
		
		ksession.insert(b);
		ksession.insert(bt);
		ksession.fireAllRules();
		
		assertEquals(1, list.size());
		
	}
	
	public void testProcessStringEscapesOptionOff() throws Exception {
		KnowledgeBase kbase = readKnowledgeBaseWithEscapesOff();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		
		List list = new ArrayList();
		ksession.setGlobal( "list", list );
		
		BinTask bt = new BinTask();
		Bin b = new Bin();
		b.setName("aa123bb");
		bt.setBin(b);
		
		ksession.insert(bt);
		ksession.insert(b);
		ksession.fireAllRules();
		
		assertEquals(1, list.size());
	}
	
	private KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "processescapes.drl" ) ),
                ResourceType.DRL );
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge." + errors.toArray());
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}
	
	private KnowledgeBase readKnowledgeBaseWithEscapesOff() throws Exception {
		KnowledgeBuilderConfiguration kbconf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		kbconf.setProperty("drools.parser.processStringEscapes", "false");
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbconf);
		//kbuilder.add(ResourceFactory.newClassPathResource("processescapes.drl"), ResourceType.DRL);
		kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "processescapesoff.drl" ) ),
                ResourceType.DRL );
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge." + errors.toArray());
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}
}
