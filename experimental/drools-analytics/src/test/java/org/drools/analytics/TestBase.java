package org.drools.analytics;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

/**
 * 
 * @author Toni Rikkola
 *
 */
abstract class TestBase extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		System.setProperty("drools.accumulate.function.validatePattern",
				"com.analytics.accumulateFunction.ValidatePattern");

	}

	public Collection<Object> getTestData(InputStream stream) throws Exception {
		Reader drlReader = new InputStreamReader(stream);
		PackageDescr descr = new DrlParser().parse(drlReader);

		RuleFlattener ruleFlattener = new RuleFlattener();

		ruleFlattener.insert(descr);

		// Rules with relations
		return ruleFlattener.getDataObjects();
	}

	public StatelessSession getStatelessSession(InputStream stream)
			throws Exception {
		// read in the source
		Reader source = new InputStreamReader(stream);

		PackageBuilder builder = new PackageBuilder();

		builder.addPackageFromDrl(source);

		Package pkg = builder.getPackage();

		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(pkg);

		return ruleBase.newStatelessSession();
	}
}
