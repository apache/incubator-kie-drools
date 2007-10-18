package org.drools.analytics;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

/**
 * 
 * @author Toni Rikkola
 * 
 */
abstract public class TestBase extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		System.setProperty("drools.accumulate.function.validatePattern",
				"com.analytics.accumulateFunction.ValidatePattern");

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

	/**
	 * Returns true if map contains redundancy where ruleName1 is redundant to
	 * ruleName2.
	 * 
	 * @param map
	 * @param ruleName1
	 * @param ruleName2
	 * @return True if redundancy exists.
	 */
	protected static boolean mapContains(Map<String, Set<String>> map,
			String ruleName1, String ruleName2) {
		if (map.containsKey(ruleName1)) {
			Set<String> set = map.get(ruleName1);
			boolean exists = set.remove(ruleName2);

			// If set is empty remove key from map.
			if (set.isEmpty()) {
				map.remove(ruleName1);
			}
			return exists;
		}
		return false;
	}

	public Collection<? extends Object> getTestData(InputStream stream)
			throws Exception {
		Reader drlReader = new InputStreamReader(stream);
		PackageDescr descr = new DrlParser().parse(drlReader);

		PackageDescrFlattener ruleFlattener = new PackageDescrFlattener();

		ruleFlattener.insert(descr);

		// Rules with relations
		return AnalyticsDataFactory.getAnalyticsData().getAll();
	}
}
