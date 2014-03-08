package org.drools.decisiontable.integrationtests;

import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;

import static org.junit.Assert.*;

public class IncrementalCompilationTest {

    @Test
    public void testDuplicateXLSResources() throws Exception {

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        InputStream in1 = null;
        InputStream in2 = null;

        try {

            //Add XLS decision table
            in1 = this.getClass().getResourceAsStream( "incrementalBuild.dtable.xls" );
            kfs.write( "src/main/resources/incrementalBuild1.dtable.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource( in1 ) );

            //Add the same XLS decision table again as a different resource
            in2 = this.getClass().getResourceAsStream( "incrementalBuild.dtable.xls" );
            kfs.write( "src/main/resources/incrementalBuild2.dtable.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource( in2 ) );

            //Check errors on a full build
            assertEquals( 1, ks.newKieBuilder( kfs ).buildAll().getResults().getMessages().size() );

        } finally {
            if ( in1 != null ) {
                in1.close();
            }
            if ( in2 != null ) {
                in2.close();
            }
        }

    }

    @Test
    public void testIncrementalCompilationDuplicateXLSResources() throws Exception {

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        InputStream in1 = null;
        InputStream in2 = null;

        try {

            //Add XLS decision table
            in1 = this.getClass().getResourceAsStream( "incrementalBuild.dtable.xls" );
            kfs.write( "src/main/resources/incrementalBuild1.dtable.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource( in1 ) );

            //Expect no errors
            KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
            assertEquals( 0, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

            //Add the same XLS decision table again as a different resource
            in2 = this.getClass().getResourceAsStream( "incrementalBuild.dtable.xls" );
            kfs.write( "src/main/resources/incrementalBuild2.dtable.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource( in2 ) );
            IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/incrementalBuild2.dtable.xls" ).build();

            //Expect duplicate rule errors
            assertEquals( 1, addResults.getAddedMessages().size() );
            assertEquals( 0, addResults.getRemovedMessages().size() );

            //Check errors on a full build
            assertEquals( 1, ks.newKieBuilder( kfs ).buildAll().getResults().getMessages().size() );

        } finally {
            if ( in1 != null ) {
                in1.close();
            }
            if ( in2 != null ) {
                in2.close();
            }
        }

    }

}
