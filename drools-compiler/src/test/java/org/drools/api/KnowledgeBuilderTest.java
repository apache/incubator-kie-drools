package org.drools.api;

import java.io.StringReader;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.definition.rule.Rule;
import org.drools.io.ResourceFactory;

public class KnowledgeBuilderTest {
	
    @Test
    public void testKnowledgeProvider() {
		KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		assertNotNull( builder );
	}
	
    @Test
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
		builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
		
		str = "package org.test2\n";
		str += "rule rule3\n";
		str += "when\n";
		str += "then\n";
		str += "end\n\n";
		str += "rule rule4\n";
		str += "when\n";
		str += "then\n";
		str += "end\n";
		builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
		
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
	
	public Rule getRule(Collection<Rule> rules, String name) {
		for ( Rule rule : rules ) {
			if ( rule.getName().equals( name ) ) {
				return rule;
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
