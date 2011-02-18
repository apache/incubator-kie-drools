/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class RangeCheckCleanTest {

    @Test
    public void testDummy() {
        // this is needed as eclipse will try to run this and produce a failure
        // if its not here.
        assertTrue(true);
    }
//	public void testUselessIntegerGapsLesser() throws Exception {
//		StatelessSession session = getStatelessSession(this.getClass()
//				.getResourceAsStream("rangeChecks/Clean.drl"));
//
//		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
//				"Remove useless integer gaps lesser or lesser and equal"));
//
//		Collection<Object> testData = new ArrayList<Object>();
//
//		Field f = new Field();
//		testData.add(f);
//
//		// > 10 covered
//		LiteralRestriction lower = new LiteralRestriction();
//		lower.setRuleName("> 10 covered");
//		lower.setFieldId(f.getId());
//		lower.setOperator(Operator.GREATER);
//		lower.setValue("10");
//		testData.add(lower);
//
//		// == 50 covered
//		LiteralRestriction r1 = new LiteralRestriction();
//		r1.setRuleName("== 50 covered");
//		r1.setFieldId(f.getId());
//		r1.setOperator(Operator.EQUAL);
//		r1.setValue("50");
//		testData.add(r1);
//
//		// > 50 gap
//		Gap g1 = new Gap(f, Operator.GREATER, r1);
//		// g1.setFiredRuleName("above");
//		testData.add(g1);
//
//		// < 50 gap
//		Gap g2 = new Gap(f, Operator.LESS, r1);
//		// g2.setFiredRuleName("below");
//		testData.add(g2);
//
//		// > 70 covered
//		LiteralRestriction r2 = new LiteralRestriction();
//		r2.setRuleName("> 70 covered");
//		r2.setFieldId(f.getId());
//		r2.setOperator(Operator.GREATER);
//		r2.setValue("70");
//		testData.add(r2);
//
//		// <= 70 gap
//		Gap g3 = new Gap(f, Operator.LESS_OR_EQUAL, r2);
//		// g3.setFiredRuleName("70gap");
//		testData.add(g3);
//
//		// < 100 covered
//		LiteralRestriction higher = new LiteralRestriction();
//		higher.setRuleName("< 100 covered");
//		higher.setFieldId(f.getId());
//		higher.setOperator(Operator.LESS);
//		higher.setValue("100");
//		testData.add(higher);
//
//		VerifierResult result = VerifierResultFactory.createVerifierResult();
//		session.setGlobal("result", result);
//
//		StatelessSessionResult sessionResult = session
//				.executeWithResults(testData);
//
//		Iterator<Object> iter = sessionResult.iterateObjects();
//
//		Set<Object> rulesThatHadErrors = new HashSet<Object>();
//		while (iter.hasNext()) {
//			Object o = (Object) iter.next();
//			if (o instanceof Gap || o instanceof VerifierComponent) {
//				rulesThatHadErrors.add(o);
//			}
//		}
//
//		assertTrue(rulesThatHadErrors.remove("> 10 covered"));
//		assertTrue(rulesThatHadErrors.remove("== 50 covered"));
//		assertTrue(rulesThatHadErrors.remove("> 70 covered"));
//		assertTrue(rulesThatHadErrors.remove("< 100 covered"));
//		assertFalse(rulesThatHadErrors.contains("below"));
//		assertFalse(rulesThatHadErrors.contains("above"));
//		assertFalse(rulesThatHadErrors.contains("70gap"));
//
//		if (!rulesThatHadErrors.isEmpty()) {
//			for (String string : rulesThatHadErrors) {
//				fail("Rule " + string + " caused an error.");
//			}
//		}
//	}
//
//	public void testUselessIntegerGapsGreater() throws Exception {
//		StatelessSession session = getStatelessSession(this.getClass()
//				.getResourceAsStream("rangeChecks/Clean.drl"));
//
//		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
//				"Remove useless integer gaps greater or greater and equal"));
//
//		Collection<Object> testData = new ArrayList<Object>();
//
//		Field f = new Field();
//		testData.add(f);
//
//		// > 10 covered
//		LiteralRestriction lower = new LiteralRestriction();
//		lower.setRuleName("> 10 covered");
//		lower.setFieldId(f.getId());
//		lower.setOperator(Operator.GREATER);
//		lower.setValue("10");
//		testData.add(lower);
//
//		// == 50 covered
//		LiteralRestriction r1 = new LiteralRestriction();
//		r1.setRuleName("== 50 covered");
//		r1.setFieldId(f.getId());
//		r1.setOperator(Operator.EQUAL);
//		r1.setValue("50");
//		testData.add(r1);
//
//		// > 50 gap
//		Gap g1 = new Gap(f, Operator.GREATER, r1);
//		g1.setFiredRuleName("above");
//		testData.add(g1);
//
//		// < 50 gap
//		Gap g2 = new Gap(f, Operator.LESS, r1);
//		g2.setFiredRuleName("below");
//		testData.add(g2);
//
//		// < 70 covered
//		LiteralRestriction r2 = new LiteralRestriction();
//		r2.setRuleName("< 70 covered");
//		r2.setFieldId(f.getId());
//		r2.setOperator(Operator.LESS);
//		r2.setValue("70");
//		testData.add(r2);
//
//		// >= 70 gap
//		Gap g3 = new Gap(f, Operator.GREATER_OR_EQUAL, r2);
//		g3.setFiredRuleName("70gap");
//		testData.add(g3);
//
//		// < 100 covered
//		LiteralRestriction higher = new LiteralRestriction();
//		higher.setRuleName("< 100 covered");
//		higher.setFieldId(f.getId());
//		higher.setOperator(Operator.LESS);
//		higher.setValue("100");
//		testData.add(higher);
//
//		VerifierResult result = VerifierResultFactory.createVerifierResult();
//		session.setGlobal("result", result);
//
//		StatelessSessionResult sessionResult = session
//				.executeWithResults(testData);
//
//		Iterator<Object> iter = sessionResult.iterateObjects();
//
//		Set<String> rulesThatHadErrors = new HashSet<String>();
//		while (iter.hasNext()) {
//			Object o = (Object) iter.next();
//			if (o instanceof Field) {
//				// Do nothing
//			} else if (o instanceof Gap) {
//				rulesThatHadErrors.add(((Gap) o).getFiredRuleName());
//			} else if (o instanceof VerifierComponent) {
//				rulesThatHadErrors.add(((VerifierComponent) o).getRuleName());
//			}
//			// System.out.println(o);
//		}
//
//		assertTrue(rulesThatHadErrors.remove("> 10 covered"));
//		assertTrue(rulesThatHadErrors.remove("== 50 covered"));
//		assertTrue(rulesThatHadErrors.remove("< 70 covered"));
//		assertTrue(rulesThatHadErrors.remove("< 100 covered"));
//		assertFalse(rulesThatHadErrors.contains("below"));
//		assertFalse(rulesThatHadErrors.contains("above"));
//		assertFalse(rulesThatHadErrors.contains("70gap"));
//
//		if (!rulesThatHadErrors.isEmpty()) {
//			for (String string : rulesThatHadErrors) {
//				fail("Rule " + string + " caused an error.");
//			}
//		}
//	}
//
//	public void testUselessDoubleGapsLesser() throws Exception {
//		StatelessSession session = getStatelessSession(this.getClass()
//				.getResourceAsStream("rangeChecks/Clean.drl"));
//
//		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
//				"Remove useless double gaps lesser or lesser and equal"));
//
//		Collection<Object> testData = new ArrayList<Object>();
//
//		Field f = new Field();
//		testData.add(f);
//
//		// > 10.0 covered
//		LiteralRestriction lower = new LiteralRestriction();
//		lower.setRuleName("> 10.0 covered");
//		lower.setFieldId(f.getId());
//		lower.setOperator(Operator.GREATER);
//		lower.setValue("10.0");
//		testData.add(lower);
//
//		// == 50.0 covered
//		LiteralRestriction r1 = new LiteralRestriction();
//		r1.setRuleName("== 50.0 covered");
//		r1.setFieldId(f.getId());
//		r1.setOperator(Operator.EQUAL);
//		r1.setValue("50.0");
//		testData.add(r1);
//
//		// > 50.0 gap
//		Gap g1 = new Gap(f, Operator.GREATER, r1);
//		g1.setFiredRuleName("above");
//		testData.add(g1);
//
//		// < 50.0 gap
//		Gap g2 = new Gap(f, Operator.LESS, r1);
//		g2.setFiredRuleName("below");
//		testData.add(g2);
//
//		// > 70.0 covered
//		LiteralRestriction r2 = new LiteralRestriction();
//		r2.setRuleName("> 70.0 covered");
//		r2.setFieldId(f.getId());
//		r2.setOperator(Operator.GREATER);
//		r2.setValue("70.0");
//		testData.add(r2);
//
//		// <= 70.0 gap
//		Gap g3 = new Gap(f, Operator.LESS_OR_EQUAL, r2);
//		g3.setFiredRuleName("70gap");
//		testData.add(g3);
//
//		// < 100.0 covered
//		LiteralRestriction higher = new LiteralRestriction();
//		higher.setRuleName("< 100.0 covered");
//		higher.setFieldId(f.getId());
//		higher.setOperator(Operator.LESS);
//		higher.setValue("100.0");
//		testData.add(higher);
//
//		VerifierResult result = VerifierResultFactory.createVerifierResult();
//		session.setGlobal("result", result);
//
//		StatelessSessionResult sessionResult = session
//				.executeWithResults(testData);
//
//		Iterator<Object> iter = sessionResult.iterateObjects();
//
//		Set<String> rulesThatHadErrors = new HashSet<String>();
//		while (iter.hasNext()) {
//			Object o = (Object) iter.next();
//			if (o instanceof Field) {
//				// Do nothing
//			} else if (o instanceof Gap) {
//				rulesThatHadErrors.add(((Gap) o).getFiredRuleName());
//			} else if (o instanceof VerifierComponent) {
//				rulesThatHadErrors.add(((VerifierComponent) o).getRuleName());
//			}
//			// System.out.println(o);
//		}
//
//		assertTrue(rulesThatHadErrors.remove("> 10.0 covered"));
//		assertTrue(rulesThatHadErrors.remove("== 50.0 covered"));
//		assertTrue(rulesThatHadErrors.remove("> 70.0 covered"));
//		assertTrue(rulesThatHadErrors.remove("< 100.0 covered"));
//		assertFalse(rulesThatHadErrors.contains("below"));
//		assertFalse(rulesThatHadErrors.contains("above"));
//		assertFalse(rulesThatHadErrors.contains("70gap"));
//
//		if (!rulesThatHadErrors.isEmpty()) {
//			for (String string : rulesThatHadErrors) {
//				fail("Rule " + string + " caused an error.");
//			}
//		}
//	}
//
//	public void testUselessDoubleGapsGreater() throws Exception {
//		StatelessSession session = getStatelessSession(this.getClass()
//				.getResourceAsStream("rangeChecks/Clean.drl"));
//
//		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
//				"Remove useless double gaps greater or greater and equal"));
//
//		Collection<Object> testData = new ArrayList<Object>();
//
//		Field f = new Field();
//		testData.add(f);
//
//		// > 10.0 covered
//		LiteralRestriction lower = new LiteralRestriction();
//		lower.setRuleName("> 10.0 covered");
//		lower.setFieldId(f.getId());
//		lower.setOperator(Operator.GREATER);
//		lower.setValue("10.0");
//		testData.add(lower);
//
//		// == 50.0 covered
//		LiteralRestriction r1 = new LiteralRestriction();
//		r1.setRuleName("== 50.0 covered");
//		r1.setFieldId(f.getId());
//		r1.setOperator(Operator.EQUAL);
//		r1.setValue("50.0");
//		testData.add(r1);
//
//		// > 50.0 gap
//		Gap g1 = new Gap(f, Operator.GREATER, r1);
//		// g1.setFiredRuleName("above");
//		testData.add(g1);
//
//		// < 50.0 gap
//		Gap g2 = new Gap(f, Operator.LESS, r1);
//		// g2.setFiredRuleName("below");
//		testData.add(g2);
//
//		// < 70.0 covered
//		LiteralRestriction r2 = new LiteralRestriction();
//		r2.setRuleName("< 70.0 covered");
//		r2.setFieldId(f.getId());
//		r2.setOperator(Operator.LESS);
//		r2.setValue("70.0");
//		testData.add(r2);
//
//		// >= 70.0 gap
//		Gap g3 = new Gap(f, Operator.GREATER_OR_EQUAL, r2);
//		// g3.setFiredRuleName("70gap");
//		testData.add(g3);
//
//		// < 100.0 covered
//		LiteralRestriction higher = new LiteralRestriction();
//		higher.setRuleName("< 100.0 covered");
//		higher.setFieldId(f.getId());
//		higher.setOperator(Operator.LESS);
//		higher.setValue("100.0");
//		testData.add(higher);
//
//		VerifierResult result = VerifierResultFactory.createVerifierResult();
//		session.setGlobal("result", result);
//
//		StatelessSessionResult sessionResult = session
//				.executeWithResults(testData);
//
//		Iterator<Object> iter = sessionResult.iterateObjects();
//
//		Set<String> rulesThatHadErrors = new HashSet<String>();
//		while (iter.hasNext()) {
//			Object o = (Object) iter.next();
//			if (o instanceof Field) {
//				// Do nothing
//			} else if (o instanceof Gap) {
//				rulesThatHadErrors.add(((Gap) o).getFiredRuleName());
//			} else if (o instanceof VerifierComponent) {
//				rulesThatHadErrors.add(((VerifierComponent) o).getRuleName());
//			}
//			// System.out.println(o);
//		}
//
//		assertTrue(rulesThatHadErrors.remove("> 10.0 covered"));
//		assertTrue(rulesThatHadErrors.remove("== 50.0 covered"));
//		assertTrue(rulesThatHadErrors.remove("< 70.0 covered"));
//		assertTrue(rulesThatHadErrors.remove("< 100.0 covered"));
//		assertFalse(rulesThatHadErrors.contains("below"));
//		assertFalse(rulesThatHadErrors.contains("above"));
//		assertFalse(rulesThatHadErrors.contains("70gap"));
//
//		if (!rulesThatHadErrors.isEmpty()) {
//			for (String string : rulesThatHadErrors) {
//				fail("Rule " + string + " caused an error.");
//			}
//		}
//	}
//
//	public void testUselessDateGapsLesser() throws Exception {
//		StatelessSession session = getStatelessSession(this.getClass()
//				.getResourceAsStream("rangeChecks/Clean.drl"));
//
//		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
//				"Remove useless date gaps lesser or lesser and equal"));
//
//		Collection<Object> testData = new ArrayList<Object>();
//
//		Field f = new Field();
//		testData.add(f);
//
//		// > "01-Oct-2007" covered
//		LiteralRestriction lower = new LiteralRestriction();
//		lower.setRuleName("> 01-Oct-2007 covered");
//		lower.setFieldId(f.getId());
//		lower.setOperator(Operator.GREATER);
//		lower.setValue("01-Oct-2007");
//		testData.add(lower);
//
//		// == "10-Oct-2007" covered
//		LiteralRestriction r1 = new LiteralRestriction();
//		r1.setRuleName("== 10-Oct-2007 covered");
//		r1.setFieldId(f.getId());
//		r1.setOperator(Operator.EQUAL);
//		r1.setValue("10-Oct-2007");
//		testData.add(r1);
//
//		// > "10-Oct-2007" gap
//		Gap g1 = new Gap(f, Operator.GREATER, r1);
//		g1.setFiredRuleName("above");
//		testData.add(g1);
//
//		// < "10-Oct-2007" gap
//		Gap g2 = new Gap(f, Operator.LESS, r1);
//		g2.setFiredRuleName("below");
//		testData.add(g2);
//
//		// > "15-Oct-2007" covered
//		LiteralRestriction r2 = new LiteralRestriction();
//		r2.setRuleName("> 15-Oct-2007 covered");
//		r2.setFieldId(f.getId());
//		r2.setOperator(Operator.GREATER);
//		r2.setValue("15-Oct-2007");
//		testData.add(r2);
//
//		// <= "15-Oct-2007" gap
//		Gap g3 = new Gap(f, Operator.LESS_OR_EQUAL, r2);
//		g3.setFiredRuleName("15-Oct-2007gap");
//		testData.add(g3);
//
//		// < "20-Oct-2007" covered
//		LiteralRestriction higher = new LiteralRestriction();
//		higher.setRuleName("< 20-Oct-2007 covered");
//		higher.setFieldId(f.getId());
//		higher.setOperator(Operator.LESS);
//		higher.setValue("20-Oct-2007");
//		testData.add(higher);
//
//		VerifierResult result = VerifierResultFactory.createVerifierResult();
//		session.setGlobal("result", result);
//
//		StatelessSessionResult sessionResult = session
//				.executeWithResults(testData);
//
//		Iterator<Object> iter = sessionResult.iterateObjects();
//
//		Set<String> rulesThatHadErrors = new HashSet<String>();
//		while (iter.hasNext()) {
//			Object o = (Object) iter.next();
//			if (o instanceof Field) {
//				// Do nothing
//			} else if (o instanceof Gap) {
//				rulesThatHadErrors.add(((Gap) o).getFiredRuleName());
//			} else if (o instanceof VerifierComponent) {
//				rulesThatHadErrors.add(((VerifierComponent) o).getRuleName());
//			}
//			// System.out.println(o);
//		}
//
//		assertTrue(rulesThatHadErrors.remove("> 01-Oct-2007 covered"));
//		assertTrue(rulesThatHadErrors.remove("== 10-Oct-2007 covered"));
//		assertTrue(rulesThatHadErrors.remove("> 15-Oct-2007 covered"));
//		assertTrue(rulesThatHadErrors.remove("< 20-Oct-2007 covered"));
//		assertFalse(rulesThatHadErrors.contains("below"));
//		assertFalse(rulesThatHadErrors.contains("above"));
//		assertFalse(rulesThatHadErrors.contains("15-Oct-2007gap"));
//
//		if (!rulesThatHadErrors.isEmpty()) {
//			for (String string : rulesThatHadErrors) {
//				fail("Rule " + string + " caused an error.");
//			}
//		}
//	}
//
//	public void testUselessDateGapsGreater() throws Exception {
//		StatelessSession session = getStatelessSession(this.getClass()
//				.getResourceAsStream("rangeChecks/Clean.drl"));
//
//		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
//				"Remove useless date gaps greater or greater and equal"));
//
//		Collection<Object> testData = new ArrayList<Object>();
//
//		Field f = new Field();
//		testData.add(f);
//
//		// > "01-Oct-2007" covered
//		LiteralRestriction lower = new LiteralRestriction();
//		lower.setRuleName("> 01-Oct-2007 covered");
//		lower.setFieldId(f.getId());
//		lower.setOperator(Operator.GREATER);
//		lower.setValue("01-Oct-2007");
//		testData.add(lower);
//
//		// == "10-Oct-2007" covered
//		LiteralRestriction r1 = new LiteralRestriction();
//		r1.setRuleName("== 10-Oct-2007 covered");
//		r1.setFieldId(f.getId());
//		r1.setOperator(Operator.EQUAL);
//		r1.setValue("10-Oct-2007");
//		testData.add(r1);
//
//		// > "10-Oct-2007" gap
//		Gap g1 = new Gap(f, Operator.GREATER, r1);
//		g1.setFiredRuleName("above");
//		testData.add(g1);
//
//		// < "10-Oct-2007" gap
//		Gap g2 = new Gap(f, Operator.LESS, r1);
//		g2.setFiredRuleName("below");
//		testData.add(g2);
//
//		// < "15-Oct-2007" covered
//		LiteralRestriction r2 = new LiteralRestriction();
//		r2.setRuleName("< 15-Oct-2007 covered");
//		r2.setFieldId(f.getId());
//		r2.setOperator(Operator.LESS);
//		r2.setValue("15-Oct-2007");
//		testData.add(r2);
//
//		// >= "15-Oct-2007" gap
//		Gap g3 = new Gap(f, Operator.GREATER_OR_EQUAL, r2);
//		g3.setFiredRuleName("15-Oct-2007gap");
//		testData.add(g3);
//
//		// < "20-Oct-2007" covered
//		LiteralRestriction higher = new LiteralRestriction();
//		higher.setRuleName("< 20-Oct-2007 covered");
//		higher.setFieldId(f.getId());
//		higher.setOperator(Operator.LESS);
//		higher.setValue("20-Oct-2007");
//		testData.add(higher);
//
//		VerifierResult result = VerifierResultFactory.createVerifierResult();
//		session.setGlobal("result", result);
//
//		StatelessSessionResult sessionResult = session
//				.executeWithResults(testData);
//
//		Iterator<Object> iter = sessionResult.iterateObjects();
//
//		Set<String> rulesThatHadErrors = new HashSet<String>();
//		while (iter.hasNext()) {
//			Object o = (Object) iter.next();
//			if (o instanceof Field) {
//				// Do nothing
//			} else if (o instanceof Gap) {
//				rulesThatHadErrors.add(((Gap) o).getFiredRuleName());
//			} else if (o instanceof VerifierComponent) {
//				rulesThatHadErrors.add(((VerifierComponent) o).getRuleName());
//			}
//			// System.out.println(o);
//		}
//
//		assertTrue(rulesThatHadErrors.remove("> 01-Oct-2007 covered"));
//		assertTrue(rulesThatHadErrors.remove("== 10-Oct-2007 covered"));
//		assertTrue(rulesThatHadErrors.remove("< 15-Oct-2007 covered"));
//		assertTrue(rulesThatHadErrors.remove("< 20-Oct-2007 covered"));
//		assertFalse(rulesThatHadErrors.contains("below"));
//		assertFalse(rulesThatHadErrors.contains("above"));
//		assertFalse(rulesThatHadErrors.contains("15-Oct-2007gap"));
//
//		if (!rulesThatHadErrors.isEmpty()) {
//			for (String string : rulesThatHadErrors) {
//				fail("Rule " + string + " caused an error.");
//			}
//		}
//	}
}
