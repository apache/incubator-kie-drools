package org.drools.analytics.subsumption;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.analytics.TestBase;
import org.drools.analytics.components.AnalyticsComponent;
import org.drools.analytics.report.components.Subsumption;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class SubsumptionTestBase extends TestBase {

	public void testDummy() {
		// this is needed as eclipse will try to run this and produce a failure
		// if its not here.
	}

	/**
	 * Creates subsumption map from Subsumption objects, one rule may have
	 * several subsumption dependencies.
	 * 
	 * @param iter
	 * @return
	 */
	protected Map<String, Set<String>> createSubsumptionMap(
			Iterator<Object> iter) {

		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Subsumption) {
				Subsumption s = (Subsumption) o;
				AnalyticsComponent left = (AnalyticsComponent) s.getLeft();
				AnalyticsComponent right = (AnalyticsComponent) s.getRight();

				if (map.containsKey(left.getRuleName())) {
					Set<String> set = map.get(left.getRuleName());
					set.add(right.getRuleName());
				} else {
					Set<String> set = new HashSet<String>();
					set.add(right.getRuleName());
					map.put(left.getRuleName(), set);
				}
			}
		}

		return map;
	}
}
