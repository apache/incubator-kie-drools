package org.drools.compiler.integrationtests;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static org.junit.Assert.*;

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

    @Test
    public void testMessageWithIncrementalBuild() throws Exception {
        //Some suitably duff DSL to generate errors
        String dsl1 = "bananna\n";

        //Some suitably valid DRL
        String drl1 = "import org.drools.compiler.Person;\n"
                + "rule R1\n"
                + "when\n"
                + "There is a Person\n"
                + "then\n"
                + "end\n";

        //Some suitably valid DRL
        String drl2 = "rule R2\n"
                + "when\n"
                + "then\n"
                + "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/dsl.dsl", dsl1 )
                .write( "src/main/resources/drl.dslr", drl1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        Results fullBuildResults = kieBuilder.getResults();
        assertEquals( 3,
                      fullBuildResults.getMessages().size() );

        kfs.write( "src/main/resources/r2.drl", drl2 );
        IncrementalResults incrementalBuildResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 0, incrementalBuildResults.getAddedMessages().size() );
        assertEquals( 0, incrementalBuildResults.getRemovedMessages().size() );
    }

}
