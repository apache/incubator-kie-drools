package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;
import org.drools.marshalling.impl.ProtobufMarshaller;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.time.SessionClock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EventAccessorRestoreTest extends CommonTestMethodBase {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File kbaseFile = null;

    @Before
    public void setUp() {
        String str =
                "package org.drools.test;\n" +
                "" +
                "global java.util.List list; \n" +
                "\n" +
                "declare Tick @role(event)  \n" +
                " @timestamp( time ) \n" +
                " id : int \n" +
                " time : long \n" +
                "end \n" +
                "" +
                "" +
                "rule \"Init\" when\n" +
                "   $i : Integer() \n" +
                "then\n" +
                "   Tick tick = new Tick( $i, new java.util.Date().getTime() ); \n" +
                "   insert( tick ); \n" +
                "   System.out.println( tick ); \n" +
                "   list.add( tick ); \n" +
                "end\n" +
                "";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        try {
            kbaseFile = temp.newFile( "test.bin" );
            FileOutputStream fos = new FileOutputStream( kbaseFile ) ;
            saveSession( fos, ksession );
            fos.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    public void saveSession( FileOutputStream output, StatefulKnowledgeSession ksession ) throws IOException {
        DroolsObjectOutputStream droolsOut = new DroolsObjectOutputStream( output );
        droolsOut.writeObject( ksession.getKnowledgeBase() );
        Marshaller mas = createMarshaller( ksession.getKnowledgeBase() );
        mas.marshall( droolsOut, ksession );
        droolsOut.flush();
        droolsOut.close();
    }

    private Marshaller createMarshaller( KnowledgeBase kbase ) {
        ObjectMarshallingStrategyAcceptor acceptor = MarshallerFactory.newClassFilterAcceptor( new String[]{ "*.*" } );
        ObjectMarshallingStrategy strategy = MarshallerFactory.newSerializeMarshallingStrategy( acceptor );
        return MarshallerFactory.newMarshaller( kbase, new ObjectMarshallingStrategy[] { strategy } );
    }

    public StatefulKnowledgeSession loadSession( FileInputStream input ) throws IOException, ClassNotFoundException {
        StatefulKnowledgeSession ksession = null;
        DroolsObjectInputStream droolsIn = new DroolsObjectInputStream( input, this.getClass().getClassLoader() );
        try {
            KnowledgeBase kbase = (KnowledgeBase) droolsIn.readObject();
            Marshaller mas = createMarshaller( kbase );
            ksession = mas.unmarshall(droolsIn);
        } catch ( EOFException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        } finally {
            droolsIn.close();
        }
        return ksession;
    }


    @Test
    public void testDeserialization() {
        try {
            FileInputStream fis = new FileInputStream( kbaseFile );
            StatefulKnowledgeSession knowledgeSession = loadSession( fis );

            ArrayList list = new ArrayList();
            knowledgeSession.setGlobal( "list", list );

            knowledgeSession.insert( 30 );
            knowledgeSession.fireAllRules();

            assertEquals( 1, list.size() );
            assertEquals( "Tick", list.get( 0 ).getClass().getSimpleName() );

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }

}
