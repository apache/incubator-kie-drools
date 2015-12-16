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

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;

import org.junit.Assert;

import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.api.runtime.KieSession;

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
        kbuilder.add( ResourceFactory.newReaderResource(new StringReader(rule)),
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
