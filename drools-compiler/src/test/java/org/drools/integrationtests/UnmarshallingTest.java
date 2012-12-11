package org.drools.integrationtests;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.conf.EventProcessingOption;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.marshalling.MarshallerFactory;
import org.kie.runtime.KieSession;

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
        KieSession ksession = knowledgeBase.newStatefulKnowledgeSession();
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
        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
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
