package org.drools.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

	    InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("/org/drools/integrationtests/HelloWorld.drl"));
	    assertNotNull(reader);


	    File target = new File(dir, "Something.drl");


	    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target));

        BufferedReader in = new BufferedReader(reader);
        BufferedWriter out = new BufferedWriter(writer);
        String str;
        while ((str = in.readLine()) != null) {

            out.write(str + "\n");
        }
        in.close();
        out.flush();
        out.close();



        Properties config = new Properties();
        config.setProperty(RuleAgent.FILES, target.getPath());



		RuleAgent ag = RuleAgent.newRuleAgent(config);
		RuleBase rb = ag.getRuleBase();
		assertNotNull(rb);
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
}
