package org.drools.api;

import java.io.StringReader;
import java.util.Collection;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;

import junit.framework.TestCase;

public class KnowledgeSessionTest extends TestCase {
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
		builder.addPackageFromDrl( new StringReader( str ) );
		
		str = "package org.test2\n";
		str += "rule rule3\n";
		str += "when\n";
		str += "then\n";
		str += "end\n\n";
		str += "rule rule4\n";
		str += "when\n";
		str += "then\n";
		str += "end\n";			
		builder.addPackageFromDrl( new StringReader( str ) );
		
		Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();

		
	}
}
