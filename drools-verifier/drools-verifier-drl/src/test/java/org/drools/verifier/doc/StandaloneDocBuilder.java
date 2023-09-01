package org.drools.verifier.doc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;

/**
 * Stand alone to test writing to a file.
 */
public class StandaloneDocBuilder {

    public static void main(String[] args) throws FileNotFoundException,
                                          ParseException {
        StandaloneDocBuilder docBuilder = new StandaloneDocBuilder();
        docBuilder.buildDoc();
    }

    @Test
    public void buildDoc() throws FileNotFoundException, ParseException {
        String drl = "";
        drl += "# important information\n";
        drl += "# about this package\n";
        drl += "# it contains some rules\n";
        drl += "package org.kie.test\n";
        drl += "global java.util.List list\n";
        drl += "# Really important information about this rule \n";
        drl += "# Another line because one was not enough \n";
        drl += "#  \n";
        drl += "# @author: trikkola \n";
        drl += "rule \"First\" extends \"OtherRule\" \n";
        drl += "	dialect \"mvel\" \n";
        drl += "	when \n ";
        drl += "		Person() \n ";
        drl += "		Cheesery() \n ";
        drl += "	then \n ";
        drl += "		applicant.setApproved(true) \n";
        drl += "		applicant.setName( \"Toni\" ) \n";
        drl += "		applicant.setAge( 10 ) \n";
        drl += "end \n";
        drl += "\n";
        drl += "# Really important information about this rule \n";
        drl += "# Another line because one was not enough \n";
        drl += "#  \n";
        drl += "# @author: trikkola \n";
        drl += "# @created: 29.12.2001 \n";
        drl += "# @edited: 5.5.2005 \n";
        drl += "rule \"Second\" \n";
        drl += "	dialect \"mvel\" \n";
        drl += "	when \n ";
        drl += "		Person() \n ";
        drl += "		Cheesery() \n ";
        drl += "	then \n ";
        drl += "		applicant.setApproved(true) \n";
        drl += "		applicant.setName( \"Toni\" ) \n";
        drl += "		applicant.setAge( 10 ) \n";
        drl += "end";
        drl += "\n";
        drl += "rule \"Third\" \n";
        drl += "	dialect \"mvel\" \n";
        drl += "	when \n ";
        drl += "		Person() \n ";
        drl += "		Cheesery() \n ";
        drl += "	then \n ";
        drl += "		applicant.setApproved(true) \n";
        drl += "		applicant.setName( \"Toni\" ) \n";
        drl += "		applicant.setAge( 10 ) \n";
        drl += "end";

        DroolsDocsBuilder ddBuilder = DroolsDocsBuilder.getInstance( drl );

        File file = new File( "DroolsDoc.pdf" );
        OutputStream out = new FileOutputStream( file );

        ddBuilder.writePDF( out );
    }

    @AfterAll
    static void cleanup() throws IOException {
        Files.delete(new File( "DroolsDoc.pdf" ).toPath());
    }

}
