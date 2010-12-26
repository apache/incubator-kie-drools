package org.drools.decisiontable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuleBase;
import org.drools.agent.RuleAgent;

public class SourcePackageProviderTest {

    @Test
    public void testSourceProvider() throws Exception {
        new SourcePackageProvider();

        File dir = getTempDirectory();

        InputStream in = this.getClass().getResourceAsStream( "/data/ExamplePolicyPricing.xls" );

        File target = new File( dir,
                                "Something.xls" );

        OutputStream out = new FileOutputStream( target );

        byte[] buf = new byte[1024];
        int len;
        while ( (len = in.read( buf )) > 0 ) {
            out.write( buf,
                       0,
                       len );
        }
        in.close();
        out.close();

        Properties config = new Properties();
        config.setProperty( RuleAgent.FILES,
                            target.getPath() );

        RuleAgent ag = RuleAgent.newRuleAgent( config );

        assertNotNull( ag );

        RuleBase rb = ag.getRuleBase();
        assertNotNull( rb );
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
            for ( int i = 0; i < children.length; i++ ) {
                boolean success = deleteDir( new File( dir,
                                                       children[i] ) );
                if ( !success ) {
                    //throw new RuntimeException("Unable to delete !");
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }
}
