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
import java.util.HashSet;
import java.util.List;

import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.common.Agenda;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.Duration;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * @author Alexander Bagerman
 */

public class SchedulerTest extends DroolsTestCase {
	public void testScheduledActivation() throws Exception {
		RuleBase ruleBase = new RuleBaseImpl();

		WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase
				.newWorkingMemory();

		final Rule rule = new Rule("test-rule");
		final List data = new ArrayList();

		// add consequence
		rule.setConsequence(new org.drools.spi.Consequence() {
			public void invoke(Activation activation,
					WorkingMemory workingMemory) {
				data.add("tested");
			}
		});

		/* 1/10th of a second */
		Duration duration = new Duration() {
			public long getDuration(Tuple tuple) {
				return 100;
			}
		};
		rule.setDuration(duration);

		PropagationContext context = new PropagationContextImpl(0,
				PropagationContext.ASSERTION, rule, null);

		FactHandleImpl tupleFactHandle = (FactHandleImpl) workingMemory
				.assertObject("tuple object");
		FactHandleImpl[] factHandlesTuple = new FactHandleImpl[1];
		factHandlesTuple[0] = tupleFactHandle;
		LeapsTuple tuple = new LeapsTuple(factHandlesTuple, null, null, context);

		assertEquals(0, data.size());

		workingMemory.assertTuple(tuple, rule);

		// sleep for 2 seconds
		Thread.sleep(300);

		// now check for update
		assertEquals(1, data.size());
	}

	public void testDoLoopScheduledActivation() throws Exception {
		RuleBase ruleBase = new RuleBaseImpl();

		final WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase
				.newWorkingMemory();
		final Agenda agenda = workingMemory.getAgenda();

		final Rule rule = new Rule("test-rule");
		final List data = new ArrayList();

		/* 1/10th of a second */
		Duration duration = new Duration() {
			public long getDuration(Tuple tuple) {
				return 100;
			}
		};

		rule.setDuration(duration);

		// add consequence
		rule.setConsequence(new org.drools.spi.Consequence() {
			public void invoke(Activation activation,
					WorkingMemory workingMemory) {
				/* on first invoke add another one to the agenda */
				if (data.size() < 3) {
					PropagationContext context2 = new PropagationContextImpl(0,
							0, rule, activation);

					FactHandleImpl tupleFactHandleIn = (FactHandleImpl) workingMemory
							.assertObject("tuple object in");
					FactHandleImpl[] factHandlesTupleIn = new FactHandleImpl[1];
					factHandlesTupleIn[0] = tupleFactHandleIn;
					LeapsTuple tupleIn = new LeapsTuple(factHandlesTupleIn, null, null, context2);
					((WorkingMemoryImpl) workingMemory).assertTuple(tupleIn, rule);
				}
				data.add("tested");
			}
		});

		PropagationContext context = new PropagationContextImpl(0,
				PropagationContext.ASSERTION, rule, null);

		FactHandleImpl tupleFactHandle = (FactHandleImpl) workingMemory
				.assertObject("tuple object");
		FactHandleImpl[] factHandlesTuple = new FactHandleImpl[1];
		factHandlesTuple[0] = tupleFactHandle;
		LeapsTuple tuple = new LeapsTuple(factHandlesTuple, null, null, context);

		workingMemory.assertTuple(tuple, rule);

		assertEquals(0, data.size());

		// sleep for 0.5 seconds
		Thread.sleep(1500);

		// now check for update
		assertEquals(4, data.size());

	}

	public void testNoLoopScheduledActivation() throws Exception {
		RuleBase ruleBase = new RuleBaseImpl();

		final WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase
				.newWorkingMemory();
		final Agenda agenda = workingMemory.getAgenda();

		final Rule rule = new Rule("test-rule");
		final List data = new ArrayList();

		/* 1/10th of a second */
		Duration duration = new Duration() {
			public long getDuration(Tuple tuple) {
				return 100;
			}

		};

		rule.setDuration(duration);
		rule.setNoLoop(true);

		// add consequence
		rule.setConsequence(new org.drools.spi.Consequence() {
			public void invoke(Activation activation,
					WorkingMemory workingMemory) {
				/* on first invoke add another one to the agenda */
				if (data.size() < 5) {
					FactHandleImpl tupleFactHandleIn = (FactHandleImpl) workingMemory
							.assertObject("tuple object in");
					FactHandleImpl[] factHandlesTupleIn = new FactHandleImpl[1];
					factHandlesTupleIn[0] = tupleFactHandleIn;
					LeapsTuple tupleIn = new LeapsTuple(factHandlesTupleIn, null, null, activation.getPropagationContext());
					((WorkingMemoryImpl) workingMemory).assertTuple(tupleIn, rule);
				}
				data.add("tested");
			}
		});

		PropagationContext context = new PropagationContextImpl(0,
				PropagationContext.ASSERTION, null, null);

		FactHandleImpl tupleFactHandle = (FactHandleImpl) workingMemory
				.assertObject("tuple object");
		FactHandleImpl[] factHandlesTuple = new FactHandleImpl[1];
		factHandlesTuple[0] = tupleFactHandle;
		LeapsTuple tuple = new LeapsTuple(factHandlesTuple, null, null, context);

		workingMemory.assertTuple(tuple, rule);

		assertEquals(0, data.size());

		// sleep for 0.5 seconds
		Thread.sleep(50000);

		// now check for update
		assertEquals(1, data.size());

	}
}
