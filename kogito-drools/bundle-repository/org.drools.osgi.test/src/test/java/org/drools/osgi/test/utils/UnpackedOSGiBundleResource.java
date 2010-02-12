package org.drools.osgi.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.core.io.UrlResource;

public class UnpackedOSGiBundleResource extends UrlResource {

    public UnpackedOSGiBundleResource(URL aUrl) {
        super( aUrl );             
        // copy META-INF and build.properties
        File file = new File( aUrl.getPath().substring( "file:///".length() ));

        File targetMetaInfDir = new File( file,
                                          "META-INF" );
        targetMetaInfDir.mkdir();
        File targetMetaInf = new File( targetMetaInfDir,
                                       "MANIFEST.MF" );

        File sourceMetaInfDir = new File( new File( new File( file.getParent() ).getParent() ),
                                          "META-INF" );
        File sourceMetaInf = new File( sourceMetaInfDir,
                                       "MANIFEST.MF" );

        copyfile( sourceMetaInf,
                  targetMetaInf );
        
        File sourceBuildProperties = new File (new File( file.getParent() ).getParent(), "build.properties" );
        File targetBuildProperties = new File (file, "build.properties" );
        
        copyfile( sourceBuildProperties,
                  targetBuildProperties );        
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return super.getURL().openStream();
    }

    private void copyfile(File f1,
                          File f2) {
        InputStream in = null;
        OutputStream out = null;
        try {            
             in = new FileInputStream( f1 );

            //For Overwrite the file.
            out = new FileOutputStream( f2 );

            byte[] buf = new byte[1024];
            int len;
            while ( (len = in.read( buf )) > 0 ) {
                out.write( buf,
                           0,
                           len );
            }

        } catch ( FileNotFoundException ex ) {
            throw new RuntimeException("Unable to copy file from '" + f1.getAbsolutePath() + " to " + f2.getAbsolutePath(), ex );
        } catch ( IOException e ) {
            throw new RuntimeException("Unable to copy file from '" + f1.getAbsolutePath() + " to " + f2.getAbsolutePath(), e );
        } finally {
            if ( in != null ) {
                try {
                    in.close();
                } catch ( IOException e ) {
                    throw new RuntimeException("Unable to close Input stream", e );
                } finally {
                    if ( out != null ) {
                        try {
                            out.close();
                        } catch ( IOException e ) {
                            throw new RuntimeException("Unable to close Input stream", e );
                        }
                    }                    
                }
            }
        }
    }
}
