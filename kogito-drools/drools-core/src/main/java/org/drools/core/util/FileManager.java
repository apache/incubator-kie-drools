/**
 * 
 */
package org.drools.core.util;

import java.io.File;
import java.util.UUID;

public class FileManager {
    //private Set<File> files;
    private File root;
    
    public void setUp() {
        this.root = getRootDirectory();
    }
    
    public void tearDown() {            
        // GC and Sleep, to give OS maximum chance of allowing stuff to be deleted. Attempt 5 times
        boolean result = false;
        for ( int i = 0; i < 5 && !result; i++ ) {
            System.gc();
            try {
                Thread.sleep( 250 );
            } catch ( InterruptedException e ) {
                throw new RuntimeException( "This should never happen" );
            }
            
            result = deleteDir( root );
        }
    }
    
    public File newFile(String name) {
        File file = new File( getRootDirectory(), name);
        //files.add( file );
        return file;
    }
    
    public File newFile(File dir, String name) {
        File file = new File( dir, name);
        //files.add( file );
        return file;        
    }
    
    public File getRootDirectory() {
        if ( this.root != null ) {
            return this.root;
        }
        File tmp = new File(System.getProperty( "java.io.tmpdir" ));
        File f = new File(tmp, "__drools__" + UUID.randomUUID().toString() );
        //files.add( f );
        if (f.exists()) {
            if (f.isFile()) {
                throw new IllegalStateException("The temp directory exists as a file. Nuke it now !");
            }
            deleteDir( f );
            f.mkdir();
        } else {
            f.mkdir();
        }
        this.root = f;
        return this.root;
    }


    public boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                File file = new File(dir, child);
                boolean success = deleteDir( file );
                if (!success) {                    
                    // this is a hack, but some time you need to wait for a file release to release
                    // Windows was having intermittent issues with DirectoryScannerTest with the dir not being empty
                    System.gc();
                    try {
                        Thread.sleep( 250 );
                    } catch ( InterruptedException e ) {
                        throw new RuntimeException( "This should never happen" );
                    }
                    success = deleteDir( file );
                    if ( !success) {
                        //ok now give up 
                        //throw new RuntimeException("Unable to delete !");
                        return false;
                    }
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }      
}