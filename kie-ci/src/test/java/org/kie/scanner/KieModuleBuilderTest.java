package org.kie.scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

import static org.junit.Assert.*;

public class KieModuleBuilderTest extends AbstractKieCiTest {

    @Test
    public void testKieModuleUsingPOMMissingKBaseDefinition() throws Exception {
        KieServices ks = KieServices.Factory.get();

        //Build a KieModule jar, deploy it into local Maven repository
        ReleaseId releaseId = ks.newReleaseId( "org.kie",
                                               "metadata-test2",
                                               "1.0-SNAPSHOT" );
        String pomText = getPom( releaseId );
        File pomFile = new File( System.getProperty( "java.io.tmpdir" ),
                                 MavenRepository.toFileName( releaseId, null ) + ".pom" );
        try {
            FileOutputStream fos = new FileOutputStream( pomFile );
            fos.write( pomText.getBytes() );
            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writePomXML( getPom( releaseId ) );
        kfs.write( "src/main/java/org/kie/test/Bean.java",
                   createJavaSourceInPackage() );

        KieBuilder kieBuilder1 = ks.newKieBuilder( kfs );
        Assert.assertTrue( kieBuilder1.buildAll().getResults().getMessages().isEmpty() );
        InternalKieModule kieModule = (InternalKieModule) kieBuilder1.getKieModule();

        MavenRepository.getMavenRepository().deployArtifact( releaseId,
                                                             kieModule,
                                                             pomFile );

        //Build a second KieModule, depends on the first KieModule jar which we have deployed into Maven
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie",
                                                "metadata-test-using-pom",
                                                "1.0-SNAPSHOT" );
        String pomText2 = getPom( releaseId2,
                                  releaseId );
        File pomFile2 = new File( System.getProperty( "java.io.tmpdir" ),
                                  MavenRepository.toFileName( releaseId2, null ) + ".pom" );
        try {
            FileOutputStream fos = new FileOutputStream( pomFile2 );
            fos.write( pomText2.getBytes() );
            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        //Try building the second KieModule
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        InputStream is = new FileInputStream( pomFile2 );
        KieModuleModel kproj2 = ks.newKieModuleModel();

        kieFileSystem.write( "pom.xml",
                             KieServices.Factory.get().getResources().newInputStreamResource( is ) );
        kieFileSystem.writeKModuleXML( kproj2.toXML() );
        kieFileSystem.write( "src/main/resources/rule.drl",
                             createDRLWithImport( "rule1" ) );
        KieBuilder kieBuilder = kieServices.newKieBuilder( kieFileSystem );
        kieBuilder.buildAll();
        assertTrue( kieBuilder.getResults().getMessages().isEmpty() );
    }

    private String createJavaSourceInPackage() {
        return "package org.kie.test;\n" +
                "public class Bean {\n" +
                "   private int value;\n" +
                "   public int getValue() {\n" +
                "       return value;\n" +
                "   }\n" +
                "}";
    }

    protected String createDRLWithImport( String ruleName ) {
        return "import org.kie.test.Bean\n" +
                "rule " + ruleName + "\n" +
                "when\n" +
                "Bean()\n" +
                "then\n" +
                "end\n";
    }

}
