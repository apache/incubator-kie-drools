/**
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.report.html;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.zip.ZipOutputStream;

import org.mvel2.templates.TemplateRuntime;

abstract class ReportModeller {

    protected ZipOutputStream zout;

    protected String formPage(String sourceFolder,
                              String content) {
        Map<String, Object> map = new HashMap<String, Object>();
        String myTemplate = VerifierMessagesVisitor.readFile( "frame.htm" );

        map.put( "cssStyle",
                 ReportVisitor.createStyleTag( sourceFolder + "/" + UrlFactory.CSS_FOLDER + "/" + UrlFactory.CSS_BASIC ) );
        map.put( "sourceFolder",
                 sourceFolder );
        map.put( "header",
                 ReportVisitor.processHeader( sourceFolder ) );
        map.put( "content",
                 content );

        return String.valueOf( TemplateRuntime.eval( myTemplate,
                                                     map ) );
    }

    public void copyFile(String destination,
                         String filename) throws IOException {
        zout.putNextEntry( new JarEntry( destination + File.separator + filename ) );

        InputStream in = HTMLReportWriter.class.getResourceAsStream( filename );

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ( (len = in.read( buf )) != -1 ) {
            zout.write( buf,
                        0,
                        len );
        }
        in.close();
        zout.closeEntry();
    }

    protected void writeToFile(String fileName,
                               String text) throws IOException {
        zout.putNextEntry( new JarEntry( fileName ) );

        ByteArrayInputStream i = new ByteArrayInputStream( text.getBytes() );

        int len = 0;
        byte[] copyBuf = new byte[1024];
        while ( len != -1 ) {

            len = i.read( copyBuf,
                          0,
                          copyBuf.length );
            if ( len > 0 ) {
                zout.write( copyBuf,
                            0,
                            len );
            }
        }

        i.close();
        zout.closeEntry();
    }
}
