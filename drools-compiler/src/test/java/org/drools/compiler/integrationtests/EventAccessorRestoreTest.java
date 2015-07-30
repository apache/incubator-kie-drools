/*
 * Copyright 2015 JBoss Inc
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

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kie.api.KieBase;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
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

        KieSession ksession = kbase.newStatefulKnowledgeSession();

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

    public void saveSession( FileOutputStream output, KieSession ksession ) throws IOException {
        DroolsObjectOutputStream droolsOut = new DroolsObjectOutputStream( output );
        droolsOut.writeObject( ksession.getKieBase() );
        Marshaller mas = createMarshaller( ksession.getKieBase() );
        mas.marshall( droolsOut, ksession );
        droolsOut.flush();
        droolsOut.close();
    }

    private Marshaller createMarshaller( KieBase kbase ) {
        ObjectMarshallingStrategyAcceptor acceptor = MarshallerFactory.newClassFilterAcceptor( new String[]{ "*.*" } );
        ObjectMarshallingStrategy strategy = MarshallerFactory.newSerializeMarshallingStrategy( acceptor );
        return MarshallerFactory.newMarshaller( kbase, new ObjectMarshallingStrategy[] { strategy } );
    }

    public KieSession loadSession( FileInputStream input ) throws IOException, ClassNotFoundException {
        KieSession ksession = null;
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
            KieSession knowledgeSession = loadSession( fis );

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
