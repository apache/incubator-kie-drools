package org.drools.testframework;

import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;

/**
 * A class with some utilities for testing rules.
 * @author Michael Neale
 *
 */
public abstract class RuleUnit extends TestCase {

	/**
	 * Return a wm ready to go based on the rules in a drl at the specified uri (in the classpath).
	 */
	public StatefulSession getWorkingMemory(String uri)
			throws DroolsParserException, IOException, Exception {
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(new InputStreamReader(this.getClass()
				.getResourceAsStream(uri)));
		assertFalse(builder.getErrors().toString(), builder.hasErrors());
		RuleBase rb = RuleBaseFactory.newRuleBase();
		rb.addPackage(builder.getPackage());

		return rb.newStatefulSession();
	}
}
