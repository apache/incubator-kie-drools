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

package org.drools.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.KnowledgePackage;
import org.drools.rule.Package;

public class RuleBaseAssemblerTest {

    @Test
    public void testAssemblePackages() throws Exception {
        RuleBase rb = RuleBaseFactory.newRuleBase();
        Package pkg = new Package( "goober" );
        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        rb.addPackage( pkg );

        Package p1 = new Package( "p1" );
        p1.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );

        File f = getTempDirectory();

        File p1file = new File( f,
                                "p1.pkg" );

        writePackage( p1,
                      p1file );

        Package p1_ = readPackage( p1file );

        rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( p1_ );

    }

    public static Package readPackage(File p1file) throws IOException,
                                                  FileNotFoundException,
                                                  ClassNotFoundException {
        return (Package) DroolsStreamUtils.streamIn( new FileInputStream( p1file ) );
    }

    public static void writePackage(Package pkg,
                                    File p1file) throws IOException,
                                                FileNotFoundException {
        FileOutputStream out = new FileOutputStream( p1file );
        try {
            DroolsStreamUtils.streamOut( out,
                                         pkg );
        } finally {
            out.close();
        }
    }

    public static void writePackage(KnowledgePackage kpackage,
                                    File p1file) throws IOException,
                                                FileNotFoundException {
        FileOutputStream out = new FileOutputStream( p1file );
        try {
            DroolsStreamUtils.streamOut( out,
                                         kpackage );
        } finally {
            out.close();
        }
    }

    public static File getTempDirectory() {
        File f = tempDir();
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

    private static File tempDir() {
        File tmp = new File( System.getProperty( "java.io.tmpdir" ) );

        return new File( tmp,
                         "__temp_test_drools_packages" );
    }

    public static boolean deleteDir(File dir) {

        if ( dir.isDirectory() ) {
            String[] children = dir.list();
            for ( String child : children ) {
                File file = new File( dir,
                                      child );
                boolean success = deleteDir( file );
                if ( !success ) {
                    // this is a hack, but some time you need to wait for a file release to release
                    // Windows was having intermittent issues with DirectoryScannerTest with the dir not being empty
                    System.gc();
                    try {
                        Thread.sleep( 300 );
                    } catch ( InterruptedException e ) {
                        throw new RuntimeException( "This should never happen" );
                    }
                    success = deleteDir( file );
                    if ( !success ) {
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

    public static void clearTempDirectory() {
        deleteDir( tempDir() );
    }

}
