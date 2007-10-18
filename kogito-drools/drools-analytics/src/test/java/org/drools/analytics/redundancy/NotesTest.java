package org.drools.analytics.redundancy;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.StatelessSession;
import org.drools.analytics.TestBase;
import org.drools.analytics.components.LiteralRestriction;
import org.drools.analytics.components.PatternPossibility;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.Redundancy;
import org.drools.base.RuleNameMatchesAgendaFilter;

public class NotesTest extends TestBase {

	public void testRedundantRestrictionsInPatternPossibilities()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Notes.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant restrictions from pattern possibilities"));

		Collection<Object> objects = new ArrayList<Object>();
		LiteralRestriction left = new LiteralRestriction();

		LiteralRestriction right = new LiteralRestriction();

		Redundancy redundancy = new Redundancy(
				Redundancy.RedundancyType.STRONG, left, right);

		PatternPossibility possibility = new PatternPossibility();
		possibility.add(left);
		possibility.add(right);

		objects.add(left);
		objects.add(right);
		objects.add(redundancy);
		objects.add(possibility);

		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(objects);

		Collection<AnalyticsMessageBase> notes = result
				.getBySeverity(AnalyticsMessage.Severity.NOTE);

		// Has at least one item.
		assertEquals(1, notes.size());

		AnalyticsMessageBase note = notes.iterator().next();
		assertTrue(note.getFaulty().equals(redundancy));
	}
}
