package org.drools.integrationtests;

import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;

import junit.framework.Assert;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.MarshallerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class UnmarshallingTest {

    @Test
    public void testMarshallWithNot() throws Exception {
        String whenBenNotVilgaxRule =
                "import " + getClass().getCanonicalName() + ".*\n" +
                        "rule one\n" +
                        "when\n" +
                        "   Ben()\n" +
                        "   not(Vilgax())\n" +
                        "then\n" +
                        "   //System.out.println(\"Ben!\");\n" +
                        "end\n" +
                        "\n" +
                        "rule two\n" +
                        "when\n" +
                        "   Ben()\n" +
                        "then\n" +
                        "   //System.out.println(\"Vilgax..\");\n" +
                        "end\n";

        KnowledgeBase knowledgeBase = initializeKnowledgeBase( whenBenNotVilgaxRule );

        // Initialize Knowledge session and insert Ben
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();
        ksession.insert( new Ben() );

        // Marshall
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MarshallerFactory.newMarshaller( knowledgeBase ).marshall( baos,
                                                                   ksession );

        // Clean up
        //  - mimicing when a session is reloaded from a database.
        ksession.dispose();

        // Re-initialize 
        knowledgeBase = initializeKnowledgeBase( whenBenNotVilgaxRule );

        // Unmarshall
        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        try {
            ksession = MarshallerFactory.newMarshaller( knowledgeBase ).unmarshall( bais );
        } catch ( Throwable t ) {
            t.printStackTrace();
            fail( t.getClass().getSimpleName() + " thrown when trying to unmarshall (see stack trace in output)." );
        }
        int rules = ksession.fireAllRules();
        Assert.assertEquals( 2,
                             rules );
    }

    private KnowledgeBase initializeKnowledgeBase( String rule ) {
        // Setup knowledge base
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( rule ) ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( config );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return knowledgeBase;
    }

    public static class Ben
        implements
        Serializable {
        private static final long serialVersionUID = 9127145048523653863L;

        @Override
        public String toString() {
            return "Ben[]";
        }

    }

    public static class Vilgax
        implements
        Serializable {
        private static final long serialVersionUID = 5337858943537739516L;

        @Override
        public String toString() {
            return "Vilgax[]";
        }
    }

}
