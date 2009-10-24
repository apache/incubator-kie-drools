package org.drools.verifier.report.html;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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

        File source = new File( HTMLReportWriter.class.getResource( filename ).getFile() );
        InputStream in = new FileInputStream( source );

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ( (len = in.read( buf )) > 0 ) {
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
