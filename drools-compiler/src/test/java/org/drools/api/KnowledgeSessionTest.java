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
import org.drools.io.ResourceFactory;

public class KnowledgeSessionTest {
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

		
	}
}
