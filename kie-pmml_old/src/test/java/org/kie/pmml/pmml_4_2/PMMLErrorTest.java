/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.pmml_4_2;


import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public class PMMLErrorTest {

    String pmlm = "<PMML version=\"4.2\" xsi:schemaLocation=\"http://www.dmg.org/PMML-4_2 http://www.dmg.org/v4-1/pmml-4-2.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.dmg.org/PMML-4_2\">\n" +
                  "  <Header copyright=\"opensource\" description=\"test\">\n" +
                  "    <Application name=\"handmade\" version=\"1.0\"/>\n" +
                  "    <Annotation>notes here</Annotation>\n" +
                  "    <Timestamp>now</Timestamp>\n" +
                  "  </Header>\n" +
                  "<IllegalModel>\n" +
                  "</IllegalModel>" +
                  "</PMML>";

    String pmml = "<PMML version=\"4.2\" xsi:schemaLocation=\"http://www.dmg.org/PMML-4_2 http://www.dmg.org/v4-1/pmml-4-2.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.dmg.org/PMML-4_2\">\n" +
                  "  <Header copyright=\"opensource\" description=\"test\">\n" +
                  "    <Application name=\"handmade\" version=\"1.0\"/>\n" +
                  "    <Annotation>notes here</Annotation>\n" +
                  "    <Timestamp>now</Timestamp>\n" +
                  "  </Header>" +
                  "<DataDictionary>\n" +
                  " <DataField name=\"fld\" dataType=\"string\" optype=\"categorical\" />" +
                  "</DataDictionary>\n" +
                  "</PMML>";


    @Test
    public void testErrorDuringGenrationAPICompatibility() {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( pmlm.getBytes() ), ResourceType.PMML );

        System.out.print( kbuilder.getErrors() );
        assertTrue( kbuilder.hasErrors() );

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.add( ResourceFactory.newByteArrayResource( pmml.getBytes() ),ResourceType.PMML );

        System.out.print( kbuilder2.getErrors() );
        assertFalse( kbuilder2.hasErrors() );

    }


    @Test
    public void testErrorDuringGeneration() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( ResourceFactory.newByteArrayResource( pmlm.getBytes() )
                           .setResourceType( ResourceType.PMML )
                           .setSourcePath( "pmlm.pmml" )
        );

        KieBuilder kb = ks.newKieBuilder( kfs );
        kb.buildAll();
        assertEquals( 1, kb.getResults().getMessages( Message.Level.ERROR ).size() );
    }

    @Test
    public void testNoErrorDuringGeneration() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( ResourceFactory.newByteArrayResource( pmml.getBytes() )
                           .setResourceType( ResourceType.PMML )
                           .setSourcePath( "pmml.pmml" )
        );

        KieBuilder kb = ks.newKieBuilder( kfs );
        kb.buildAll();
        assertEquals( 0, kb.getResults().getMessages( Message.Level.ERROR ).size() );
    }


}
