package org.drools.compiler.integrationtests;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;

import static junit.framework.Assert.*;

/**
 * Tests for MessageImpl
 */
public class MessageImplTests {

    @Test
    //See DROOLS-193 (KnowledgeBuilderResult does not always contain a Resource)
    public void testMessageFromInvalidDSL() throws Exception {
        //Some suitably duff DSL
        String dsl = "bananna\n";

        //Some suitably valid DRL
        String drl = "import org.drools.compiler.Person;\n"
                + "rule R1\n"
                + "when\n"
                + "There is a Person\n"
                + "then\n"
                + "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/dsl.dsl", dsl )
                .write( "src/main/resources/drl.dslr", drl );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        Results results = kieBuilder.getResults();

        assertEquals( 3,
                      results.getMessages().size() );
    }

}
