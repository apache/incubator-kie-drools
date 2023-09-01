package org.drools.mvel.compiler;

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtil {

    public static void assertDrlHasCompilationError( String str, int errorNr ) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        org.kie.api.builder.Results results = ks.newKieBuilder( kfs ).buildAll( DrlProject.class).getResults();
        if ( errorNr > 0 ) {
            assertThat(results.getMessages().size()).isEqualTo(errorNr);
        } else {
            assertThat(results.getMessages().size() > 0).isTrue();
        }
    }
}
