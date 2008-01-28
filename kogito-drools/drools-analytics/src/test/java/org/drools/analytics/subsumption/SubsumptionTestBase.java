package org.drools.analytics.subsumption;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.analytics.TestBase;
import org.drools.analytics.report.components.Cause;
import org.drools.analytics.report.components.Redundancy;
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
	protected Map<Cause, Set<Cause>> createSubsumptionMap(Iterator<Object> iter) {

		Map<Cause, Set<Cause>> map = new HashMap<Cause, Set<Cause>>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Subsumption && !(o instanceof Redundancy)) {
				Subsumption s = (Subsumption) o;
				Cause left = s.getLeft();
				Cause right = s.getRight();

				if (map.containsKey(left)) {
					Set<Cause> set = map.get(left);
					set.add(right);
				} else {
					Set<Cause> set = new HashSet<Cause>();
					set.add(right);
					map.put(left, set);
				}
			}
		}

		return map;
	}
}
