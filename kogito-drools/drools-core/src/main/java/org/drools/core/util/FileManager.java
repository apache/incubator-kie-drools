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

/**
 * 
 */
package org.drools.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

import org.drools.io.impl.ResourceChangeScannerImpl;

public class FileManager {
    //private Set<File> files;
    private File root;

    public void setUp() {
        this.root = getRootDirectory();
    }

    public void tearDown() {
        deleteDir( root );
    }

    public File newFile(String name) {
        File file = new File( getRootDirectory(),
                              name );
        //files.add( file );
        return file;
    }

    private File newFile(String path,
                         String fileName) {
        File file = new File( getRootDirectory(),
                              path );
        
        file.mkdir();
        //files.add( file );
        return new File( file,
                         fileName );
    }

    public File newFile(File dir,
                        String name) {
        File file = new File( dir,
                              name );
        //files.add( file );
        return file;
    }

    public File getRootDirectory() {
        if ( this.root != null ) {
            return this.root;
        }
        File tmp = new File( System.getProperty( "java.io.tmpdir" ) );
        File f = new File( tmp,
                           "__drools__" + UUID.randomUUID().toString() );
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
        this.root = f;
        return this.root;
    }

    public void deleteDir(File dir) {
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

    public void deleteFile(File file) {
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

    public void write(File f,
                      String text) throws IOException {
        if ( f.exists() ) {
            // we want to make sure there is a time difference for lastModified and lastRead checks as Linux and http often round to seconds
            // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
            try {
                Thread.sleep( 1000 );
            } catch ( Exception e ) {
                throw new RuntimeException( "Unable to sleep" );
            }
        }

        // Attempt to write the file
        BufferedWriter output = new BufferedWriter( new FileWriter( f ) );
        output.write( text );
        output.close();

        // Now check the file was written and re-attempt if it was not        
        // Need to do this for testing, to ensure the texts are read the same way, otherwise sometimes you get tail \n sometimes you don't
        String t1 = StringUtils.toString( new StringReader( text ) );

        int count = 0;
        while ( !t1.equals( StringUtils.toString( new BufferedReader( new FileReader( f ) ) ) ) && count < 5 ) {
            // The file failed to write, try 5 times, calling GC and sleep between each iteration
            // Sometimes windows takes a while to release a lock on a file            
            System.gc();
            try {
                Thread.sleep( 250 );
            } catch ( InterruptedException e ) {
                throw new RuntimeException( "This should never happen" );
            }
            output = new BufferedWriter( new FileWriter( f ) );
            output.write( text );
            output.close();
            count++;
        }

        if ( count == 5 ) {
            throw new IOException( "Unable to write to file:" + f.getCanonicalPath() );
        }
    }

    public File write(String fileName,
                      String text) throws IOException {
        File f = newFile( fileName );

        write( f,
               text );

        return f;

    }

    public File write(String path,
                      String fileName,
                      String text) throws IOException {
        File f = newFile( path,
                          fileName );

        write( f,
               text );

        return f;
    }

}