/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.doc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;

import org.drools.verifier.doc.DroolsDocsBuilder;

/**
 * Stand alone to test writing to a file.
 */
public class StandaloneDocBuilder {

    public static void main(String[] args) throws FileNotFoundException,
                                          ParseException {

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
