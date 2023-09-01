package org.drools.mvel.compiler.kie.builder;

import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.generatePomXml;

/**
 * Test for DSL expansion with KieBuilder
 */
@RunWith(Parameterized.class)
public class DslExpansionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DslExpansionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testDSLExpansion_MessageImplNPE() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "dsl-test", "1.0" );
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() )
                .writePomXML( generatePomXml( releaseId ) )
                .write( "src/main/resources/KBase1/test-dsl.dsl", createDSL() )
                .write( "src/main/resources/KBase1/test-rule.dslr", createDRL() );

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        final List<Message> messages = kieBuilder.getResults().getMessages();
        if ( !messages.isEmpty() ) {
            for ( final Message m : messages ) {
                System.out.println( m.getText() );
            }
        }
        assertThat(messages.isEmpty()).isTrue();
    }

    @Test
    public void testDSLExpansion_NoExpansion() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "dsl-test", "1.0" );
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() )
                .writePomXML( generatePomXml( releaseId ) )
                .write( "src/main/resources/KBase1/test-dsl.dsl", createDSL() )
                .write( "src/main/resources/KBase1/test-rule.drl", createDRL() );

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        final List<Message> messages = kieBuilder.getResults().getMessages();
        if ( !messages.isEmpty() ) {
            for ( final Message m : messages ) {
                System.out.println( m.getText() );
            }
        }
        assertThat(messages.isEmpty()).isFalse();
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
