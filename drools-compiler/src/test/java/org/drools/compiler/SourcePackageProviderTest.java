package org.drools.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.agent.RuleAgent;
import org.drools.agent.RuleBaseAssemblerTest;

public class SourcePackageProviderTest extends TestCase {

	public void testSourceProvider() throws Exception {
		new SourcePackageProvider();
	    File dir = RuleBaseAssemblerTest.getTempDirectory();

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
}
