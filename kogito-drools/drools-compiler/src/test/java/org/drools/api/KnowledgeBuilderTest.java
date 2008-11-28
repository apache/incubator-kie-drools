package org.drools.api;

import java.io.StringReader;
import java.util.Collection;

import junit.framework.TestCase;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.definition.rule.Rule;
import org.drools.io.ResourceFactory;

public class KnowledgeBuilderTest extends TestCase {
	
	public void testKnowledgeProvider() {
		KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		assertNotNull( builder );
	}
	
	public void testKnowledgeProviderWithRules() {
		KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		String str = "";
		str += "package org.test1\n";
		str += "rule rule1\n";
		str += "when\n";
		str += "then\n";
		str += "end\n\n";
		str += "rule rule2\n";
		str += "when\n";
		str += "then\n";
		str += "end\n";				
		builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), KnowledgeType.DRL );
		
		str = "package org.test2\n";
		str += "rule rule3\n";
		str += "when\n";
		str += "then\n";
		str += "end\n\n";
		str += "rule rule4\n";
		str += "when\n";
		str += "then\n";
		str += "end\n";			
		builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), KnowledgeType.DRL );
		
		Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();
		assertNotNull( pkgs );
		assertEquals( 2, pkgs.size() );
		
		KnowledgePackage test1 = getKnowledgePackage(pkgs, "org.test1" );
		Collection<Rule> rules = test1.getRules();		
		assertEquals( 2, rules.size() );
		Rule rule = getRule( rules, "rule1" );
		assertEquals("rule1", rule.getName() );
		rule = getRule( rules, "rule2" );
		assertEquals( "rule2", rule.getName() );
		
		KnowledgePackage test2 = getKnowledgePackage(pkgs, "org.test2" );
		rules = test2.getRules();
		assertEquals( 2, rules.size() );
		rule = getRule( rules, "rule3" );
		assertEquals("rule3", rule.getName() );
		rule = getRule( rules, "rule4" );
		assertEquals( "rule4", rule.getName() );			
	}
	
	public void testKnowledgeProviderWithProcesses() {
		KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		String str = "";
		str += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		str += "<process xmlns=\"http://drools.org/drools-4.0/process\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\" ";
		str += "         type=\"RuleFlow\" name=\"flow1\" id=\"0\" package-name=\"org.test1\" >";
		str += "  <header/>\n";
		str += "  <nodes><start id=\"1\" name=\"Start\" /><end id=\"2\" name=\"End\" /></nodes>\n";
	    str += "  <connections><connection from=\"1\" to=\"2\"/></connections>";
	    str += "</process>";
	    builder.add(ResourceFactory.newByteArrayResource( str.getBytes() ), KnowledgeType.DRF );
	    
		str = "";
		str += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		str += "<process xmlns=\"http://drools.org/drools-4.0/process\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\" ";
		str += "         type=\"RuleFlow\" name=\"flow2\" id=\"0\" package-name=\"org.test2\" >";
		str += "  <header/>\n";
		str += "  <nodes><start id=\"1\" name=\"Start\" /><end id=\"2\" name=\"End\" /></nodes>\n";
	    str += "  <connections><connection from=\"1\" to=\"2\"/></connections>";
	    str += "</process>";	
	    builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), KnowledgeType.DRF );
	    
		Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();
		assertNotNull( pkgs );
		assertEquals( 2, pkgs.size() );	 
		
		KnowledgePackage test1 = getKnowledgePackage(pkgs, "org.test1" );
		Collection<Process> processes = test1.getProcesses();		
		assertEquals( 1, processes.size() );
		Process process = getProcess( processes, "flow1" );
		assertEquals("flow1", process.getName() );	
		
		KnowledgePackage test2 = getKnowledgePackage(pkgs, "org.test2" );
		processes = test2.getProcesses();		
		assertEquals( 1, processes.size() );
		process = getProcess( processes, "flow2" );
		assertEquals("flow2", process.getName() );		
	    	    
	}
	
	public Rule getRule(Collection<Rule> rules, String name) {
		for ( Rule rule : rules ) {
			if ( rule.getName().equals( name ) ) {
				return rule;
			}
		}
		return null;		
	}
	
	public Process getProcess(Collection<Process> processes, String name) {
		for ( Process process : processes ) {
			if ( process.getName().equals( name ) ) {
				return process;
			}
		}
		return null;		
	}	
	
	public KnowledgePackage getKnowledgePackage(Collection<KnowledgePackage> pkgs, String name) {
		for ( KnowledgePackage pkg : pkgs ) {
			if ( pkg.getName().equals( name ) ) {
				return pkg;
			}
		}
		return null;
	}
	
}
