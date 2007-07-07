package org.drools.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.common.DroolsObjectInputStream;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class RuleBaseAssemblerTest extends TestCase {

    
    public void testAssemblePackages() throws Exception {
        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( new Package("goober") );
        
        Package p1 = new Package("p1");
        
        File f = getTempDirectory();
        
        File p1file = new File(f, "p1.pkg");
        
        writePackage( p1, p1file );
        
        Package p1_ = readPackage( p1file );
        
        rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( p1_ );
        
        
    }

    public static Package readPackage(File p1file) throws IOException,
                                            FileNotFoundException,
                                            ClassNotFoundException {
        ObjectInputStream in = new DroolsObjectInputStream(new FileInputStream(p1file));
        Package p1_ = (Package) in.readObject();
        in.close();
        return p1_;
    }

    public static void writePackage(Package pkg, File p1file) throws IOException,
                                                      FileNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(p1file));
        out.writeObject( pkg );
        out.flush(); out.close();
    }

    public static File getTempDirectory() {
        File f = tempDir();
        if (f.exists()) {
            if (f.isFile()) {
                throw new IllegalStateException("The temp directory exists as a file. Nuke it now !");
            }
            deleteDir( f );
            f.mkdir();
        } else {
            f.mkdir();
        }
        return f;
    }

    private static File tempDir() {
        File tmp = new File(System.getProperty( "java.io.tmpdir" ));
        
        return new File(tmp, "__temp_test_drools_packages");
    }
    
    public static boolean deleteDir(File dir) {
        
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    //throw new RuntimeException("Unable to delete !");
                    return false;
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
