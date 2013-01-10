package org.kie.builder;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.KieServices;
import org.kie.builder.model.KieModuleModel;

import static org.junit.Assert.*;
import static org.kie.builder.impl.KieBuilderImpl.*;

/**
 * Test for DSL expansion with KieBuilder
 */
public class DslExpansionTest {

    @Test
    @Ignore("This should expand the DSL in the DSLR file")
    public void testDSLExpansion_MessageImplNPE() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "dsl-test", "1.0-SNAPSHOT" );
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() )
                .writePomXML( generatePomXml( releaseId ) )
                .write( "src/main/resources/KBase1/test-dsl.dsl", createDSL() )
                .write( "src/main/resources/KBase1/test-rule.dslr", createDRL() );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        if ( !messages.isEmpty() ) {
            for ( final Message m : messages ) {
                System.out.println( m.getText() );
            }
        }
        assertTrue( messages.isEmpty() );
    }

    @Test
    @Ignore("This should not expand the DSL in the DRL file")
    //This test is probably not required, as a DRL file will not be expanded (only a DSLR) but it demonstrates neither approach works (in case I am mistaken that this should work)
    public void testDSLExpansion_NoExpansion() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "dsl-test", "1.0-SNAPSHOT" );
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() )
                .writePomXML( generatePomXml( releaseId ) )
                .write( "src/main/resources/KBase1/test-dsl.dsl", createDSL() )
                .write( "src/main/resources/KBase1/test-rule.drl", createDRL() );

        final KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        if ( !messages.isEmpty() ) {
            for ( final Message m : messages ) {
                System.out.println( m.getText() );
            }
        }
        assertFalse( messages.isEmpty() );
    }

    private String createDSL() {
        return "[when]There is a smurf=Smurf()\n";
    }

    private String createDRL() {
        return "package org.kie.test\n" +
                "declare Smurf\n" +
                "    name : String\n" +
                "end\n" +
                "rule Smurfs\n" +
                "when\n" +
                "    There is a smurf\n" +
                "then\n" +
                "    >System.out.println(\"Smurfs rock!\");\n" +
                "end\n";
    }

}
