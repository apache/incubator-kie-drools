/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.maven.integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class IoUtils {

    public static File copyInTempFile( InputStream input, String fileExtension ) throws IOException {
        File tempFile = File.createTempFile( UUID.randomUUID().toString(), "." + fileExtension );
        tempFile.deleteOnExit();
        copy(input, new FileOutputStream( tempFile) );
        return tempFile;
    }

    public static long copy( InputStream input, OutputStream output ) throws IOException {
        byte[] buffer = createBytesBuffer( input );
        long count = 0;
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private static byte[] createBytesBuffer( InputStream input ) throws IOException {
        return new byte[Math.max(input.available(), 8192)];
    }

    public static File getTmpDirectory() {
        File tmp = new File( System.getProperty( "java.io.tmpdir" ) );
        File f = new File( tmp, "_kie_repo_" + UUID.randomUUID().toString() );
        //files.add( f );
        if ( f.exists() ) {
            if ( f.isFile() ) {
                throw new IllegalStateException( "The temp directory exists as a file. Nuke it now !" );
            }
            deleteDir( f );
            f.mkdir();
        } else {
            f.mkdir();
        }
        return f;
    }

    private static void deleteDir(File dir) {
        // Will throw RuntimeException is anything fails to delete
        String[] children = dir.list();
        for ( String child : children ) {
            File file = new File( dir,
                    child );
            if ( file.isFile() ) {
                deleteFile( file );
            } else {
                deleteDir( file );
            }
        }

        deleteFile( dir );
    }

    private static void deleteFile(File file) {
        // This will attempt to delete a file 5 times, calling GC and Sleep between each iteration
        // Sometimes windows takes a while to release a lock on a file.
        // Throws an exception if it fails to delete
        if ( !file.delete() ) {
            int count = 0;
            while ( !file.delete() && count++ < 5 ) {
                System.gc();
                try {
                    Thread.sleep( 250 );
                } catch ( InterruptedException e ) {
                    throw new RuntimeException( "This should never happen" );
                }
            }
        }

        if ( file.exists() ) {
            try {
                throw new RuntimeException( "Unable to delete file:" + file.getCanonicalPath() );
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to delete file", e);
            }
        }
    }

}
