package org.drools.verifier.incompatibility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.base.evaluators.Operator;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

public class IncompatibilityRestrictionsTest extends IncompatibilityBase {

	public void testLiteralRestrictionsIncompatibilityLessOrEqual()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incompatible LiteralRestrictions with ranges in pattern possibility, impossible equality less or equal"));

		Collection<Object> data = new ArrayList<Object>();

		/*
		 * Working pair
		 */
		LiteralRestriction r1 = new LiteralRestriction();
		r1.setOperator(Operator.EQUAL);
		r1.setPatternId(0);
		r1.setFieldId(0);
		r1.setValue("10");

		LiteralRestriction r2 = new LiteralRestriction();
		r2.setOperator(Operator.LESS);
		r2.setPatternId(0);
		r2.setFieldId(0);
		r2.setValue("1");

		/*
		 * Pair that doesn't work.
		 */
		LiteralRestriction r3 = new LiteralRestriction();
		r3.setOperator(Operator.GREATER_OR_EQUAL);
		r3.setPatternId(1);
		r3.setFieldId(1);
		r3.setValue("1");

		LiteralRestriction r4 = new LiteralRestriction();
		r4.setOperator(Operator.EQUAL);
		r4.setPatternId(1);
		r4.setFieldId(1);
		r4.setValue("10");

		data.add(r1);
		data.add(r2);
		data.add(r3);
		data.add(r4);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<Cause, Set<Cause>> map = createIncompatibilityMap(CauseType.RESTRICTION,
				sessionResult.iterateObjects());

		assertTrue((TestBase.causeMapContains(map, r1, r2) ^ TestBase
				.causeMapContains(map, r2, r1)));

		if (!map.isEmpty()) {
			fail("More incompatibilities than was expected.");
		}
	}

	public void testLiteralRestrictionsIncompatibilityGreater()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incompatible LiteralRestrictions with ranges in pattern possibility, impossible equality greater"));

		Collection<Object> data = new ArrayList<Object>();

		/*
		 * Working pair
		 */
		LiteralRestriction r1 = new LiteralRestriction();
		r1.setOperator(Operator.GREATER);
		r1.setPatternId(0);
		r1.setFieldId(0);
		r1.setValue("10");

		LiteralRestriction r2 = new LiteralRestriction();
		r2.setOperator(Operator.EQUAL);
		r2.setPatternId(0);
		r2.setFieldId(0);
		r2.setValue("1");

		/*
		 * Pair that doesn't work.
		 */
		LiteralRestriction r3 = new LiteralRestriction();
		r3.setOperator(Operator.GREATER_OR_EQUAL);
		r3.setPatternId(1);
		r3.setFieldId(1);
		r3.setValue("1");

		LiteralRestriction r4 = new LiteralRestriction();
		r4.setOperator(Operator.EQUAL);
		r4.setPatternId(1);
		r4.setFieldId(1);
		r4.setValue("10");

		data.add(r1);
		data.add(r2);
		data.add(r3);
		data.add(r4);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<Cause, Set<Cause>> map = createIncompatibilityMap(CauseType.RESTRICTION,
				sessionResult.iterateObjects());

		assertTrue((TestBase.causeMapContains(map, r1, r2) ^ TestBase
				.causeMapContains(map, r2, r1)));

		if (!map.isEmpty()) {
			fail("More incompatibilities than was expected.");
		}
	}

	public void testLiteralRestrictionsIncompatibilityImpossibleRange()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incompatible LiteralRestrictions with ranges in pattern possibility, impossible range"));

		Collection<Object> data = new ArrayList<Object>();

		/*
		 * Working pair
		 */
		LiteralRestriction r1 = new LiteralRestriction();
		r1.setOperator(Operator.GREATER);
		r1.setPatternId(0);
		r1.setFieldId(0);
		r1.setValue("10");

		LiteralRestriction r2 = new LiteralRestriction();
		r2.setOperator(Operator.LESS);
		r2.setPatternId(0);
		r2.setFieldId(0);
		r2.setValue("10");

		/*
		 * Pair that doesn't work.
		 */
		LiteralRestriction r3 = new LiteralRestriction();
		r3.setOperator(Operator.GREATER_OR_EQUAL);
		r3.setPatternId(1);
		r3.setFieldId(1);
		r3.setValue("1");

		LiteralRestriction r4 = new LiteralRestriction();
		r4.setOperator(Operator.EQUAL);
		r4.setPatternId(1);
		r4.setFieldId(1);
		r4.setValue("10");

		data.add(r1);
		data.add(r2);
		data.add(r3);
		data.add(r4);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<Cause, Set<Cause>> map = createIncompatibilityMap(CauseType.RESTRICTION,
				sessionResult.iterateObjects());

		assertTrue((TestBase.causeMapContains(map, r1, r2) ^ TestBase
				.causeMapContains(map, r2, r1)));

		if (!map.isEmpty()) {
			fail("More incompatibilities than was expected.");
		}
	}

	public void testVariableRestrictionsIncompatibilityImpossibleRange()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent VariableRestrictions in pattern possibility, impossible range"));

		Collection<Object> data = new ArrayList<Object>();

		/*
		 * Working pair
		 */
		Variable variable1 = new Variable();
		variable1.setObjectId(0);
		variable1.setObjectType(VerifierComponentType.FIELD);

		VariableRestriction r1 = new VariableRestriction();
		r1.setOperator(Operator.GREATER);
		r1.setPatternId(0);
		r1.setFieldId(0);
		r1.setVariable(variable1);

		VariableRestriction r2 = new VariableRestriction();
		r2.setOperator(Operator.LESS);
		r2.setPatternId(0);
		r2.setFieldId(0);
		r2.setVariable(variable1);

		/*
		 * Pair that doesn't work.
		 */
		Variable variable2 = new Variable();
		variable2.setObjectId(1);
		variable2.setObjectType(VerifierComponentType.FIELD);

		VariableRestriction r3 = new VariableRestriction();
		r3.setOperator(Operator.GREATER_OR_EQUAL);
		r3.setPatternId(1);
		r3.setFieldId(1);
		r3.setVariable(variable2);

		VariableRestriction r4 = new VariableRestriction();
		r4.setOperator(Operator.EQUAL);
		r4.setPatternId(1);
		r4.setFieldId(1);
		r4.setVariable(variable2);

		data.add(r1);
		data.add(r2);
		data.add(r3);
		data.add(r4);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<Cause, Set<Cause>> map = createIncompatibilityMap(CauseType.RESTRICTION,
				sessionResult.iterateObjects());

		assertTrue((TestBase.causeMapContains(map, r1, r2) ^ TestBase
				.causeMapContains(map, r2, r1)));

		if (!map.isEmpty()) {
			fail("More incompatibilities than was expected.");
		}
	}
}
