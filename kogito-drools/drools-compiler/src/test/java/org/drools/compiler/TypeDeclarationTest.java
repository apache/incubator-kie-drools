package org.drools.compiler;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.junit.Test;

public class TypeDeclarationTest {
    
    @Test
    public void testClassNameClashing() {
        String str = "";
        str += "package org.drools \n" +
        		"declare Character \n" +
        		"    name : String \n" +
        		"end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
           fail( kbuilder.getErrors().toString() );
        }     
    }
}
