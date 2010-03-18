package org.drools.doc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;

/**
 * Stand alone to test writing to a file.
 * 
 * @author Toni Rikkola
 * 
 */
public class StandaloneDocBuilder {

    public static void main(String[] args) throws FileNotFoundException,
                                          ParseException {

        String drl = "";
        drl += "# important information\n";
        drl += "# about this package\n";
        drl += "# it contains some rules\n";
        drl += "package org.drools.test\n";
        drl += "global java.util.List list\n";
        drl += "# Really important information about this rule \n";
        drl += "# Another line because one was not enough \n";
        drl += "#  \n";
        drl += "# @author: trikkola \n";
        drl += "rule \"First\" \n";
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

        File file = new File( "/Users/rikkola/Desktop/DroolsDoc.pdf" );
        OutputStream out = new FileOutputStream( file );

        ddBuilder.writePDF( out );
    }
}
