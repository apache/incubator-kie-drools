package org.drools.modelcompiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.kie.api.builder.ReleaseId;

public class TestFileUtils {
    public static File bytesToTempKJARFile( ReleaseId releaseId, byte[] bytes, String extension ) {
        File file = new File( System.getProperty( "java.io.tmpdir" ), releaseId.getArtifactId() + "-" + releaseId.getVersion() + extension );
        try {
            new PrintWriter(file).close();
            FileOutputStream fos = new FileOutputStream( file, false );
            fos.write( bytes );
            fos.flush();
            fos.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        return file;
    }
}
