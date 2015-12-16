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

package org.drools.compiler.integrationtests;

import org.drools.core.io.impl.FileSystemResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ChangesetUndoTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private static File[] resources = new File[ 5 ];

    private static final String drl1 = "package org.drools.test1; " +
                                       "declare Foo id : int end \n" +
                                       "rule A when then end \n";

    private static final String drl2 = "package org.drools.test2; " +
                                       "declare Foo2 id : Missing end \n" +
                                       "rule A when then end \n";

    private static final String drl3 = "package org.drools.test3; " +
                                       "declare Bar id : int end \n" +
                                       "rule A when end \n";

    private String getChangeset( boolean excludeCorrectOne ) {
        return "" +
               "<change-set xmlns='http://drools.org/drools-5.0/change-set'\n" +
               "            xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'\n" +
               "            xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >\n" +
               "            \n" +
               "    <add >\n" +
               ( excludeCorrectOne ? "" : "      <resource source='file:" + folder.getRoot().getAbsolutePath() + "/file1.drl' type='DRL' />\n " )+
               "      <resource source='file:" + folder.getRoot().getAbsolutePath() + "/file2.drl' type='DRL' />\n" +
               "      <resource source='file:" + folder.getRoot().getAbsolutePath() + "/file3.drl' type='DRL' />\n" +
               "    </add>\n" +
               "    \n" +
               "</change-set>\n";
    }

    @Before
    public void setup() {
        try {
            resources[ 0 ] = folder.newFile( "changeset1.xml" );
            resources[ 1 ] = folder.newFile( "file1.drl" );
            resources[ 2 ] = folder.newFile( "file2.drl" );
            resources[ 3 ] = folder.newFile( "file3.drl" );
            resources[ 4 ] = folder.newFile( "changeset2.xml" );

            FileOutputStream fos0 = new FileOutputStream( resources[ 0 ] );
            fos0.write( getChangeset( false ).getBytes() );
            fos0.flush();
            fos0.close();

            FileOutputStream fos1 = new FileOutputStream( resources[ 1 ] );
            fos1.write( drl1.getBytes() );
            fos1.flush();
            fos1.close();

            FileOutputStream fos2 = new FileOutputStream( resources[ 2 ] );
            fos2.write( drl2.getBytes() );
            fos2.flush();
            fos2.close();

            FileOutputStream fos3 = new FileOutputStream( resources[ 3 ] );
            fos3.write( drl3.getBytes() );
            fos3.flush();
            fos3.close();

            FileOutputStream fos4 = new FileOutputStream( resources[ 4 ] );
            fos4.write( getChangeset( true ).getBytes() );
            fos4.flush();
            fos4.close();

        } catch ( IOException ioe ) {
            ioe.printStackTrace();
            fail( ioe.getMessage() );
        }
    }

    @After
    public void tearDown() {
        folder.delete();
    }


    @Test
    public void testCompilationUndo() {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new FileSystemResource( resources[ 0 ] ), ResourceType.CHANGE_SET );

        assertTrue( knowledgeBuilder.hasErrors() );

        knowledgeBuilder.undo();

        assertFalse( knowledgeBuilder.hasErrors() );

        for ( KnowledgePackage kp : knowledgeBuilder.getKnowledgePackages() ) {
            assertTrue( kp.getRules().isEmpty() );
            assertTrue( kp.getFactTypes().isEmpty() );
        };

    }

    @Test
    public void testCompilationUndoAfterGoodResults() {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new FileSystemResource( resources[ 1 ] ), ResourceType.DRL );
        knowledgeBuilder.add( new FileSystemResource( resources[ 4 ] ), ResourceType.CHANGE_SET );

        assertTrue( knowledgeBuilder.hasErrors() );

        knowledgeBuilder.undo();

        assertFalse( knowledgeBuilder.hasErrors() );

        for ( KnowledgePackage kp : knowledgeBuilder.getKnowledgePackages() ) {
            if ( "org.drools.test1".equals( kp.getName() ) ) {
                assertEquals( 1, kp.getRules().size() );
                assertEquals( 1, kp.getFactTypes().size() );
            } else {
                assertTrue( kp.getRules().isEmpty() );
                assertTrue( kp.getFactTypes().isEmpty() );
            }
        };

    }
}
