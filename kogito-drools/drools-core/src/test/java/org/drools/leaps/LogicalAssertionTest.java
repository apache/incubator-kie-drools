package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
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

import java.util.ArrayList;
import java.util.Iterator;

import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.PackageIntegrationException;
import org.drools.RuleIntegrationException;
import org.drools.WorkingMemory;
import org.drools.common.Agenda;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.Consequence;
import org.drools.spi.PropagationContext;

/**
 * @author Alexander Bagerman
 */
public class LogicalAssertionTest extends DroolsTestCase {

	public void testEqualsMap() throws Exception {
		RuleBaseImpl ruleBase = new RuleBaseImpl();
		// create a RuleBase with a single ObjectTypeNode we attach a
		// MockObjectSink so we can detect assertions and retractions

		final Rule rule1 = new Rule("test-rule1");
		WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase
				.newWorkingMemory();

		final Agenda agenda = workingMemory.getAgenda();

		Consequence consequence = new Consequence() {
			public void invoke(Activation activation,
					WorkingMemory workingMemory) {
				// do nothing
			}
		};

		rule1.setConsequence(consequence);

		FactHandleImpl[] factHandles = new FactHandleImpl[1];
		PropagationContext context1;
		LeapsTuple tuple1;

		String logicalString1 = new String("logical");
		FactHandle handle1 = ((HandleFactory) ruleBase.getFactHandleFactory())
				.newFactHandle(logicalString1);
		context1 = new PropagationContextImpl(1, PropagationContext.ASSERTION,
				null, null);
		factHandles[0] = (FactHandleImpl) handle1;
		tuple1 = new LeapsTuple(factHandles);
		workingMemory.assertTuple(tuple1, new ArrayList(), new ArrayList(),
				context1, rule1);
		Iterator activations = workingMemory.getFactHandleActivations(handle1);
		FactHandle logicalHandle1 = workingMemory.assertObject(logicalString1,
				false, true, null,
				(activations != null) ? ((PostedActivation) activations.next())
						.getAgendaItem() : null);

		String logicalString2 = new String("logical");
		FactHandle logicalHandle2 = workingMemory.assertObject(logicalString2,
				false, true, rule1,
				((PostedActivation) workingMemory
						.getFactHandleActivations(handle1).next())
						.getAgendaItem());
		factHandles[0] = (FactHandleImpl) logicalHandle2;
		tuple1 = new LeapsTuple(factHandles);
		workingMemory.assertTuple(tuple1, new ArrayList(), new ArrayList(), context1, rule1);

		assertSame(logicalHandle1, logicalHandle2);

		// little sanity check using normal assert
		logicalHandle1 = workingMemory.assertObject(logicalString1);
		logicalHandle2 = workingMemory.assertObject(logicalString2);
		assertNotSame(logicalHandle1, logicalHandle2);

	}

	/**
	 * This tests that Stated asserts always take precedent
	 * 
	 * @throws Exception
	 */
	public void testStatedOverride() throws Exception {
		RuleBaseImpl ruleBase = new RuleBaseImpl();

		final Rule rule1 = new Rule("test-rule1");
		WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase
				.newWorkingMemory();

		final Agenda agenda = workingMemory.getAgenda();

		Consequence consequence = new Consequence() {
			public void invoke(Activation activation,
					WorkingMemory workingMemory) {
				// do nothing
			}
		};

		rule1.setConsequence(consequence);

		FactHandleImpl[] factHandles = new FactHandleImpl[1];
		PropagationContext context1;
		LeapsTuple tuple1;

		String logicalString1 = new String("logical");
		FactHandle handle1 = ((HandleFactory) ruleBase.getFactHandleFactory())
				.newFactHandle(logicalString1);
		context1 = new PropagationContextImpl(1, PropagationContext.ASSERTION,
				null, null);
		factHandles[0] = (FactHandleImpl) handle1;
		tuple1 = new LeapsTuple(factHandles);
		workingMemory.assertTuple(tuple1, new ArrayList(), new ArrayList(), context1, rule1);
		Iterator activations = workingMemory.getFactHandleActivations(handle1);
		FactHandle logicalHandle1 = workingMemory.assertObject(logicalString1,
				false, true, null,
				(activations != null) ? ((PostedActivation) activations.next())
						.getAgendaItem() : null);

		String logicalString2 = new String("logical");
		FactHandle logicalHandle2 = workingMemory.assertObject(logicalString2);

		// Should keep the same handle when overriding
		assertSame(logicalHandle1, logicalHandle2);

		// so while new STATED assertion is equal
		assertEquals(logicalString1, workingMemory.getObject(logicalHandle2));
		// they are not - not identity same - leaps can not store two objects if handles are the same
		assertSame(logicalString1, workingMemory.getObject(logicalHandle2));

		// Test that a logical assertion cannot override a STATED assertion
		factHandles[0] = (FactHandleImpl) logicalHandle2;
		tuple1 = new LeapsTuple(factHandles);
		workingMemory.assertTuple(tuple1, new ArrayList(), new ArrayList(),  context1, rule1);

		logicalString2 = new String("logical");
		logicalHandle2 = workingMemory.assertObject(logicalString2);

		// This logical assertion will be ignored as there is already
		// an equals STATED assertion.
		logicalString1 = new String("logical");
		logicalHandle1 = workingMemory.assertObject(logicalString1, false,
				true, null, ((PostedActivation) workingMemory
						.getFactHandleActivations(handle1).next())
						.getAgendaItem());
		// Already an equals object but not identity same, so will do nothing
		// and return null
		assertNull(logicalHandle1);

		// Alreyad identify same so return previously assigned handle
		logicalHandle1 = workingMemory.assertObject(logicalString2, false,
				true, null, ((PostedActivation) workingMemory
						.getFactHandleActivations(handle1).next())
						.getAgendaItem());
		// return the matched handle
		assertSame(logicalHandle2, logicalHandle1);

		workingMemory.retractObject(handle1);

		// Should keep the same handle when overriding
		assertSame(logicalHandle1, logicalHandle2);

		// so while new STATED assertion is equal
		assertEquals(logicalString1, workingMemory.getObject(logicalHandle2));

		// they are not identity same
		assertNotSame(logicalString1, workingMemory.getObject(logicalHandle2));

	}

	public void testRetract() throws Exception {
		final Rule rule1 = new Rule("test-rule1");

		RuleBaseImpl ruleBase = new RuleBaseImpl();
		WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase
				.newWorkingMemory();

		Consequence consequence = new Consequence() {
			public void invoke(Activation activation,
					WorkingMemory workingMemory) {
				// do nothing
			}
		};

		// create the first agendaItem which will justify the fact "logical"
		rule1.setConsequence(consequence);

		FactHandleImpl tuple1FactHandle = (FactHandleImpl) workingMemory
				.assertObject("tuple1 object");
		FactHandleImpl tuple2FactHandle = (FactHandleImpl) workingMemory
				.assertObject("tuple2 object");
		FactHandleImpl[] factHandlesTuple1 = new FactHandleImpl[1];
		FactHandleImpl[] factHandlesTuple2 = new FactHandleImpl[1];
		factHandlesTuple1[0] = tuple1FactHandle;
		factHandlesTuple2[0] = tuple2FactHandle;
		PropagationContext context1;
		LeapsTuple tuple1 = new LeapsTuple(factHandlesTuple1);
		LeapsTuple tuple2 = new LeapsTuple(factHandlesTuple2);

		PropagationContext context = new PropagationContextImpl(0,
				PropagationContext.ASSERTION, null, null);
		workingMemory.assertTuple(tuple1, new ArrayList(), new ArrayList(), context, rule1);
		Activation activation1 = workingMemory.getAgenda().getActivations()[0];

		// Assert the logical "logical" fact
		String logicalString1 = new String("logical");
		FactHandle logicalHandle1 = workingMemory.assertObject(logicalString1,
				false, true, rule1, activation1);

		// create the second agendaItem to justify the "logical" fact
		final Rule rule2 = new Rule("test-rule2");
		rule2.setConsequence(consequence);
		tuple1 = new LeapsTuple(factHandlesTuple2);
		workingMemory.assertTuple(tuple1, new ArrayList(), new ArrayList(), context, rule1);
		Activation activation2 = workingMemory.getAgenda().getActivations()[1];
		//
		String logicalString2 = new String("logical");
		FactHandle logicalHandle2 = workingMemory.assertObject(logicalString2,
				false, true, rule1, activation2);
		// "logical" should only appear once
		assertLength(1, workingMemory.getJustified().values());

		// retract the logical object
		workingMemory.retractObject(logicalHandle2);

		// The logical object should never appear
		assertLength(0, workingMemory.getJustified().values());

	}

	public void testMultipleLogicalRelationships() throws FactException, InvalidPatternException,
			RuleIntegrationException, PackageIntegrationException {
		final Rule rule1 = new Rule("test-rule1");

		RuleBaseImpl ruleBase = new RuleBaseImpl();

		WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase
				.newWorkingMemory();

		Consequence consequence = new Consequence() {
			public void invoke(Activation activation,
					WorkingMemory workingMemory) {
				// do nothing
			}
		};

		// create the first agendaItem which will justify the fact "logical"
		rule1.setConsequence(consequence);
		FactHandleImpl tuple1Fact = (FactHandleImpl) workingMemory
				.assertObject("tuple1 object");
		FactHandleImpl tuple2Fact = (FactHandleImpl) workingMemory
				.assertObject("tuple2 object");
		FactHandleImpl[] tuple1Handles = new FactHandleImpl[1];
		FactHandleImpl[] tuple2Handles = new FactHandleImpl[1];
		tuple1Handles[0] = tuple1Fact;
		tuple2Handles[0] = tuple2Fact;
		LeapsTuple tuple1 = new LeapsTuple(tuple1Handles);
		LeapsTuple tuple2 = new LeapsTuple(tuple2Handles);

		PropagationContext context = new PropagationContextImpl(0,
				PropagationContext.ASSERTION, null, null);
		workingMemory.assertTuple(tuple1, new ArrayList(), new ArrayList(), context, rule1);
		Activation activation1 = workingMemory.getAgenda().getActivations()[0];

		// Assert the logical "logical" fact
		String logicalString1 = new String("logical");
		FactHandle logicalHandle1 = workingMemory.assertObject(logicalString1,
				false, true, rule1, activation1);

		// create the second agendaItem to justify the "logical" fact
		final Rule rule2 = new Rule("test-rule2");
		rule2.setConsequence(consequence);
		tuple2 = new LeapsTuple(tuple2Handles);
		workingMemory.assertTuple(tuple2, new ArrayList(), new ArrayList(),  context, rule1);
		// "logical" should only appear once
		Activation activation2 = workingMemory.getAgenda().getActivations()[1];
		//
		String logicalString2 = new String("logical");
		FactHandle logicalHandle2 = workingMemory.assertObject(logicalString2,
				false, true, rule2, activation2);

		assertLength(1, workingMemory.getJustified().values());
		//
		workingMemory.retractObject(tuple1Fact);
		// check "logical" is still in the system
		assertLength(1, workingMemory.getJustified().values());

		// now remove that final justification
		workingMemory.retractObject(tuple2Fact);
		// "logical" fact should no longer be in the system
		assertLength(0, workingMemory.getJustified().values());
	}
}
