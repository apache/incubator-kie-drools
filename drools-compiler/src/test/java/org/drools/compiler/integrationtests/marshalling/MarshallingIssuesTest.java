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

package org.drools.compiler.integrationtests.marshalling;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.reteoo.ReteComparator;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;

public class MarshallingIssuesTest extends CommonTestMethodBase  {

    @Test
    public void testJBRULES_1946() {
        KnowledgeBase kbase = loadKnowledgeBase("../Sample.drl" );

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );

            oos.writeObject( kbase );
            oos.flush();
            oos.close();
            baos.flush();
            baos.close();

            byte[] serializedKb = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream( serializedKb );
            ObjectInputStream ois = new ObjectInputStream( bais );

            KnowledgeBase kb2 = (KnowledgeBase) ois.readObject();
        } catch ( OptionalDataException ode ) {
            ode.printStackTrace();
            fail( "EOF? " + ode.eof );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unexpected exception: " + e.getMessage() );
        }
    }

    @Test
    public void testJBRULES_1946_2() {
        KnowledgeBase kbase = loadKnowledgeBase("../Sample.drl" );

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DroolsObjectOutputStream oos = new DroolsObjectOutputStream( baos );

            oos.writeObject( kbase );
            oos.flush();
            oos.close();
            baos.flush();
            baos.close();

            byte[] serializedKb = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream( serializedKb );
            DroolsObjectInputStream ois = new DroolsObjectInputStream( bais );

            KnowledgeBase kb2 = (KnowledgeBase) ois.readObject();
        } catch ( OptionalDataException ode ) {
            ode.printStackTrace();
            fail( "EOF? " + ode.eof );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unexpected exception: " + e.getMessage() );
        }
    }

    @Test
    public void testJBRULES_1946_3() {
        KnowledgeBase kbase = loadKnowledgeBase("../Sample.drl" );

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DroolsObjectOutputStream oos = new DroolsObjectOutputStream( baos );

            oos.writeObject( kbase );
            oos.flush();
            oos.close();
            baos.flush();
            baos.close();

            byte[] serializedKb = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream( serializedKb );
            ObjectInputStream ois = new ObjectInputStream( bais );

            KnowledgeBase kb2 = (KnowledgeBase) ois.readObject();
            fail( "Should have raised an IllegalArgumentException since the kbase was serialized with a Drools Stream but deserialized with a regular stream" );
        } catch ( IllegalArgumentException ode ) {
            // success
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unexpected exception: " + e.getMessage() );
        }
    }

    @Test
    public void testJBRULES_2331() throws Exception {
        String source = "package test.drl\n";
        source += "rule dummy_rule\n";
        source += "when\n";
        source += "eval( false )\n";
        source += "then\n";
        source += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( source );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession = org.drools.compiler.integrationtests.SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

        assertNotNull( ksession );
        ksession.dispose();
    }

    @Test
    public void testMarshallWithAccumulate() throws Exception {
        String drl1 =
                "import java.util.concurrent.atomic.AtomicInteger\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $a : AtomicInteger( get() > 3 )\n" +
                "  $i : Integer( this == $a.get() )\n" +
                "  accumulate ( $s : String( length == $i ), $result : count( ) )\n" +
                "then\n" +
                "  list.add($result);\n" +
                "end";

        KieBase kb1 = new KieHelper().addContent( drl1, ResourceType.DRL )
                                       .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DroolsObjectOutputStream oos = new DroolsObjectOutputStream( baos );

        oos.writeObject( kb1 );
        oos.flush();
        oos.close();
        baos.flush();
        baos.close();

        byte[] serializedKb = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream( serializedKb );
        DroolsObjectInputStream ois = new DroolsObjectInputStream( bais );
        ois.close();
        bais.close();

        KieBase kb2 = (KieBase) ois.readObject();
        assertTrue( ReteComparator.areEqual( kb1, kb2 ) );
    }
}
