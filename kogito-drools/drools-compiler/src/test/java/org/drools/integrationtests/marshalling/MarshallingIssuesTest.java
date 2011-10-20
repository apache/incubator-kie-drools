package org.drools.integrationtests.marshalling;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Reader;
import java.io.StringReader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.integrationtests.SerializationHelper;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class MarshallingIssuesTest {

    @Test
    public void testJBRULES_1946() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "../Sample.drl" ) ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

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
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "../Sample.drl" ) ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

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
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "../Sample.drl" ) ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

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
        Reader reader = new StringReader( source );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( reader ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );

        assertNotNull( ksession );
        ksession.dispose();
    }
    

}
