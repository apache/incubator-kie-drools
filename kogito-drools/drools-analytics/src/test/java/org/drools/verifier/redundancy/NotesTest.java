package org.drools.verifier.redundancy;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.PatternPossibility;
import org.drools.verifier.components.RulePossibility;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.dao.VerifierResultFactory;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.RedundancyType;
import org.drools.verifier.report.components.Severity;

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
				RedundancyType.STRONG, left, right);

		PatternPossibility possibility = new PatternPossibility();
		possibility.add(left);
		possibility.add(right);

		objects.add(left);
		objects.add(right);
		objects.add(redundancy);
		objects.add(possibility);

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		session.setGlobal("result", result);

		session.executeWithResults(objects);

		Collection<VerifierMessageBase> notes = result
				.getBySeverity(Severity.NOTE);

		// Has at least one item.
		assertEquals(1, notes.size());

		VerifierMessageBase note = notes.iterator().next();
		assertTrue(note.getFaulty().equals(redundancy));
	}

	public void testRedundantPatternPossibilitiesInRulePossibilities()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Notes.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Find redundant pattern possibilities from rule possibilities"));

		Collection<Object> objects = new ArrayList<Object>();
		PatternPossibility left = new PatternPossibility();

		PatternPossibility right = new PatternPossibility();

		Redundancy redundancy = new Redundancy(
				RedundancyType.STRONG, left, right);

		RulePossibility possibility = new RulePossibility();
		possibility.add(left);
		possibility.add(right);

		objects.add(left);
		objects.add(right);
		objects.add(redundancy);
		objects.add(possibility);

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		session.setGlobal("result", result);

		session.executeWithResults(objects);

		Collection<VerifierMessageBase> notes = result
				.getBySeverity(Severity.NOTE);

		// Has at least one item.
		assertEquals(1, notes.size());

		VerifierMessageBase note = notes.iterator().next();
		assertTrue(note.getFaulty().equals(redundancy));
	}
}
