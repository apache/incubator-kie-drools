package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;

public class WorkingMemoryLoggerTest extends TestCase {
	
	private static final Reader DRL = new InputStreamReader(
			WorkingMemoryLoggerTest.class.getResourceAsStream("empty.drl"));

	private static final String LOG = "session";
	public void testOutOfMemory() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(DRL);
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(builder.getPackage());
		for (int i = 0; i < 10000; i++) {
			//System.out.println(i);
			StatefulSession session = ruleBase.newStatefulSession();
			WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(session);
			session.fireAllRules();
			session.dispose();
		}
	}

}
