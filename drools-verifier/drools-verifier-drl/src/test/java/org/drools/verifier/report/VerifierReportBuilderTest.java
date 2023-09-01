package org.drools.verifier.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.drools.io.ClassPathResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;

import static org.assertj.core.api.Assertions.assertThat;

public class VerifierReportBuilderTest {

    @Test
    void testHtmlReportTest() throws IOException {

        // Create report
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(new ClassPathResource( "Misc3.drl",
                        Verifier.class ),
                ResourceType.DRL);
        VerifierReportWriter writer = VerifierReportWriterFactory.newHTMLReportWriter();

        // Write to disk
        FileOutputStream out = new FileOutputStream( "testReport.zip" );

        writer.writeReport(out,
                verifier.getResult());

        // Check the files on disk
        File file = new File( "testReport.zip" );
        assertThat(file).isNotNull();
        assertThat(file.exists()).isTrue();

        // TODO: Check the file content
        
        // Remove the test file
        file.delete();

        assertThat(file.exists()).isFalse();

    }

    @Test
    void testPlainTextReportTest() throws IOException {
        //TODO:
        assertThat(true).isTrue();
    }

    @Test
    void testXMLReportTest() throws IOException {
        //TODO:
        assertThat(true).isTrue();
    }

    @Test
    void testPDFReportTest() throws IOException {
        //TODO:
        assertThat(true).isTrue();
    }
}
