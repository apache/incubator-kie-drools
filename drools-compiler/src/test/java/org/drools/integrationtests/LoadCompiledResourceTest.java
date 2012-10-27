package org.drools.integrationtests;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoadCompiledResourceTest {
	
	private final static String RULES = 
			"package load.compiled;\n" +
			"import org.drools.Cheese;\n" +
			"declare Person\n" +
			"	firstName : String\n" +
			"	lastName : String\n" +
			"	pref : Cheese\n" +
			"end\n" +
			"rule one\n" +
			"	when\n"+
			"		$p : Person( $first, $last; )\n" +
			"		not Cheese( )\n" +
			"	then\n" +
			"		retract( $p )\n" +
			"end\n" +
			"rule two\n" +
			"	when\n" +
			"		Person( \"aap\", $surname; )\n" +
			"		$c : Cheese( )\n" +
			"	then\n" +
			"		insert( new Person( \"noot\", $surname, $c ) );\n" +
			"end\n";	

	private File compileDrl() throws IOException {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		kbuilder.add(ResourceFactory.newByteArrayResource(RULES.getBytes()), ResourceType.DRL);
		
		File compiledFile = File.createTempFile("load-compiled-resource-test-", ".drl.compiled");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(compiledFile));
		out.writeObject( kbuilder.getKnowledgePackages());
        out.close();
        
        return compiledFile;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testLoadCompiled() throws IOException, ClassNotFoundException {
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		
		File file = compileDrl();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		Collection<KnowledgePackage> o = (Collection<KnowledgePackage>) in.readObject();
		in.close();
		file.delete();
		
		kbase.addKnowledgePackages(o);
		
		assertNotNull(kbase.getRule("load.compiled", "one"));
	}
}
