package org.drools.compiler.integrationtests.marshalling;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Reader;
import java.io.StringReader;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.StatefulKnowledgeSession;

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
    

}
