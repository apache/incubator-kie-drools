package org.drools.analytics.incompatibility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.analytics.TestBase;
import org.drools.analytics.components.LiteralRestriction;
import org.drools.analytics.report.components.Cause;
import org.drools.analytics.report.components.CauseType;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.base.evaluators.MatchesEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;

public class IncompatibilityRestrictionsTest extends IncompatibilityBase {

	public void testLiteralRestrictionIncompatibility() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Incompatibility LiteralRestrictions"));

		Collection<Object> data = new ArrayList<Object>();

		LiteralRestriction r1 = new LiteralRestriction();
		r1.setFieldId(0);
		r1.setOperator(Operator.EQUAL);
		r1.setValue("1");

		LiteralRestriction r2 = new LiteralRestriction();
		r2.setFieldId(0);
		r2.setOperator(Operator.NOT_EQUAL);
		r2.setValue("1");

		LiteralRestriction r3 = new LiteralRestriction();
		r3.setFieldId(0);
		r3.setOperator(Operator.EQUAL);
		r3.setValue("1.0");

		LiteralRestriction r4 = new LiteralRestriction();
		r4.setFieldId(0);
		r4.setOperator(Operator.NOT_EQUAL);
		r4.setValue("1.0");

		LiteralRestriction r5 = new LiteralRestriction();
		r5.setFieldId(0);
		r5.setOperator(MatchesEvaluatorsDefinition.MATCHES);
		r5.setValue("foo");

		LiteralRestriction r6 = new LiteralRestriction();
		r6.setFieldId(0);
		r6.setOperator(MatchesEvaluatorsDefinition.NOT_MATCHES);
		r6.setValue("foo");

		data.add(r1);
		data.add(r2);
		data.add(r3);
		data.add(r4);
		data.add(r5);
		data.add(r6);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<Cause, Set<Cause>> map = createOppositesMap(CauseType.RESTRICTION,
				sessionResult.iterateObjects());

		assertTrue((TestBase.causeMapContains(map, r1, r2) ^ TestBase
				.causeMapContains(map, r2, r1)));
		assertTrue((TestBase.causeMapContains(map, r3, r4) ^ TestBase
				.causeMapContains(map, r4, r3)));
		assertTrue((TestBase.causeMapContains(map, r5, r6) ^ TestBase
				.causeMapContains(map, r6, r5)));

		if (!map.isEmpty()) {
			fail("More incompatibilities than was expected.");
		}
	}

	public void testLiteralRestrictionIncompatibilityWithRangesGreaterOrEqualAndLess()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incompatibility LiteralRestrictions with ranges, greater or equal - less"));

		Collection<Object> data = new ArrayList<Object>();

		LiteralRestriction r1 = new LiteralRestriction();
		r1.setFieldId(0);
		r1.setOperator(Operator.GREATER_OR_EQUAL);
		r1.setValue("1");

		LiteralRestriction r2 = new LiteralRestriction();
		r2.setFieldId(0);
		r2.setOperator(Operator.LESS);
		r2.setValue("1");

		data.add(r1);
		data.add(r2);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<Cause, Set<Cause>> map = createOppositesMap(CauseType.RESTRICTION,
				sessionResult.iterateObjects());

		assertTrue((TestBase.causeMapContains(map, r1, r2) ^ TestBase
				.causeMapContains(map, r2, r1)));

		if (!map.isEmpty()) {
			fail("More incompatibilities than was expected.");
		}
	}

	public void testLiteralRestrictionIncompatibilityWithRangesGreaterAndLessOrEqual()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incompatibility LiteralRestrictions with ranges, greater - less or equal"));

		Collection<Object> data = new ArrayList<Object>();

		LiteralRestriction r1 = new LiteralRestriction();
		r1.setFieldId(0);
		r1.setOperator(Operator.GREATER);
		r1.setValue("1");

		LiteralRestriction r2 = new LiteralRestriction();
		r2.setFieldId(0);
		r2.setOperator(Operator.LESS_OR_EQUAL);
		r2.setValue("1");

		data.add(r1);
		data.add(r2);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<Cause, Set<Cause>> map = createOppositesMap(CauseType.RESTRICTION,
				sessionResult.iterateObjects());

		assertTrue((TestBase.causeMapContains(map, r1, r2) ^ TestBase
				.causeMapContains(map, r2, r1)));

		if (!map.isEmpty()) {
			fail("More incompatibilities than was expected.");
		}
	}

	public void testLiteralRestrictionIncompatibilityWithRangesLessAndGreaterForIntsAndDates()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incompatibility LiteralRestrictions with ranges, less - greater for ints and dates"));

		Collection<Object> data = new ArrayList<Object>();

		LiteralRestriction r1 = new LiteralRestriction();
		r1.setFieldId(0);
		r1.setOperator(Operator.GREATER);
		r1.setValue("0");

		LiteralRestriction r2 = new LiteralRestriction();
		r2.setFieldId(0);
		r2.setOperator(Operator.LESS);
		r2.setValue("1");

		data.add(r1);
		data.add(r2);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<Cause, Set<Cause>> map = createOppositesMap(CauseType.RESTRICTION,
				sessionResult.iterateObjects());

		assertTrue((TestBase.causeMapContains(map, r1, r2) ^ TestBase
				.causeMapContains(map, r2, r1)));

		if (!map.isEmpty()) {
			fail("More incompatibility than was expected.");
		}
	}

	public void testLiteralRestrictionIncompatibilityWithRangesLessOrEqualAndGreaterOrEqualForIntsAndDates()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incompatibility LiteralRestrictions with ranges, less or equal - greater or equal for ints and dates"));

		Collection<Object> data = new ArrayList<Object>();

		LiteralRestriction r1 = new LiteralRestriction();
		r1.setFieldId(0);
		r1.setOperator(Operator.GREATER_OR_EQUAL);
		r1.setValue("1");

		LiteralRestriction r2 = new LiteralRestriction();
		r2.setFieldId(0);
		r2.setOperator(Operator.LESS_OR_EQUAL);
		r2.setValue("0");

		data.add(r1);
		data.add(r2);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<Cause, Set<Cause>> map = createOppositesMap(CauseType.RESTRICTION,
				sessionResult.iterateObjects());

		assertTrue((TestBase.causeMapContains(map, r1, r2) ^ TestBase
				.causeMapContains(map, r2, r1)));

		if (!map.isEmpty()) {
			fail("More incompatibilities than was expected.");
		}
	}
}
