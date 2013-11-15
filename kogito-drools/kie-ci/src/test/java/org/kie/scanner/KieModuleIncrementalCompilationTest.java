package org.kie.scanner;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static junit.framework.Assert.*;

public class KieModuleIncrementalCompilationTest extends AbstractKieCiTest {

    @Test
    @Ignore()
    public void testDeletedFileToni() throws Exception {
        String drl1 = "package org.kie.scanner\n" +
                "rule R1 when\n" +
                "   String()\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.kie.scanner\n" +
                "rule R2_2 when\n" +
                "   String( )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write("src/main/resources/r1.drl", drl1)
                .write("src/main/resources/r2.drl", drl2);

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();

        KieModuleMetaData.Factory.newKieModuleMetaData( ( (InternalKieBuilder) kieBuilder ).getKieModuleIgnoringErrors() );

        assertEquals(2, getRuleNames(kieBuilder).get("org.kie.scanner").size());

        kfs.delete("src/main/resources/KBase1/r2.drl");

        IncrementalResults addResults = ((InternalKieBuilder) kieBuilder).createFileSet("src/main/resources/KBase1/r2.drl").build();

        assertEquals(1, getRuleNames(kieBuilder).get("org.kie.scanner").size());
    }

    private HashMap<String, Collection<String>> getRuleNames(KieBuilder kieBuilder) {
        KieModuleMetaData kieModuleMetaData = getKieModuleMetaData(kieBuilder);
        HashMap<String, Collection<String>> ruleNames = new HashMap<String, Collection<String>>();


        // BUG Nro. 1. getPackages() only returns the packages that have classes in them, should also return the packages that only have rules in them.
        for (String packageName : kieModuleMetaData.getPackages()) {
            ruleNames.put(packageName, kieModuleMetaData.getRuleNamesInPackage(packageName));
        }

        // Temporal fix for the missing package names.
        ruleNames.put("org.kie.scanner", kieModuleMetaData.getRuleNamesInPackage("org.kie.scanner"));

        return ruleNames;
    }

    private KieModuleMetaData getKieModuleMetaData(KieBuilder kieBuilder) {
        return KieModuleMetaData.Factory.newKieModuleMetaData( ( (InternalKieBuilder) kieBuilder ).getKieModuleIgnoringErrors() );
    }

    @Test
    public void testIncrementalCompilationFirstBuildHasErrors() throws Exception {
        KieServices ks = KieServices.Factory.get();

        //Malformed POM - No Version information
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "incremental-test-with-invalid pom", "" );

        KieFileSystem kfs = createKieFileSystemWithKProject( ks );
        kfs.writePomXML( getPom( releaseId ) );

        //Valid
        String drl1 =
                "rule R1 when\n" +
                        "   $s : String()\n" +
                        "then\n" +
                        "end\n";

        //Invalid
        String drl2 =
                "rule R2 when\n" +
                        "   $s : Strin( )\n" +
                        "then\n" +
                        "end\n";

        //Write Rule 1 - No DRL errors, but POM is in error
        kfs.write( "src/main/resources/KBase1/r1.drl", drl1 );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 1,
                kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        //Add file with error - expect 1 "added" error message
        kfs.write( "src/main/resources/KBase1/r2.drl", drl2 );
        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/KBase1/r2.drl" ).build();

        assertEquals( 1, addResults.getAddedMessages().size() );
        assertEquals( 0, addResults.getRemovedMessages().size() );
    }

}
