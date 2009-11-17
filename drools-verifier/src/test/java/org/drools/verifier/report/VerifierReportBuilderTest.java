package org.drools.verifier.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;

public class VerifierReportBuilderTest extends TestCase {

    public void testHtmlReportTest() throws IOException {

        // Create report
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( new ClassPathResource( "Misc3.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );
        VerifierReportWriter writer = VerifierReportWriterFactory.newHTMLReportWriter();

        // Write to disk
        FileOutputStream out = new FileOutputStream( "testReport.zip" );

        writer.writeReport( out,
                            verifier.getResult() );

        // Check the files on disk
        File file = new File( "testReport.zip" );
        assertNotNull( file );
        assertTrue( file.exists() );

        // TODO: Check the file content
        
        // Remove the test file
        file.delete();

        assertFalse( file.exists() );

    }

    public void testPlainTextReportTest() throws IOException {
        //TODO:
        assertTrue( true );
    }

    public void testXMLReportTest() throws IOException {
        //TODO:
        assertTrue( true );
    }

    public void testPDFReportTest() throws IOException {
        //TODO:
        assertTrue( true );
    }
}