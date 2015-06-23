/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.scanner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

class IoUtils {

    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

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
