/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests.phreak.sequencing;

import org.drools.base.rule.Pattern;
import org.drools.core.common.InternalFactHandle;
import org.drools.base.reteoo.sequencing.signalprocessors.ConditionalSignalCounter;
import org.drools.base.reteoo.sequencing.signalprocessors.Gates;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate.DelayFromActivatedTimer;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate.DelayFromMatchTimer;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate.TimeoutTimer;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.steps.Step;
import org.drools.base.reteoo.sequencing.signalprocessors.TerminatingSignalProcessor;
import org.drools.core.time.impl.DurationTimer;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.C;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class PhreakSequencerSignalProcessorTimerTest extends AbstractPhreakSequencerSubsequenceTest {


    @Before
    public void setup() {
        initKBaseWithEmptyRule();
    }

    @Test
    public void testTimeout() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B and C
                                        new int[] {0, 1}, // Each SignalAdapter must be in a unique index  for the Sequence
                                        0);

        gate1.setPropagationTimer(new TimeoutTimer(gate1, new DurationTimer(1000)));

        gate1.setOutput(TerminatingSignalProcessor.get());

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        seq.setFilters(new Pattern[]{bpattern, cpattern});
        rule.addSequence(seq);
        kbase.addPackage(pkg);

        createSession();
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle   fhB0   = (InternalFactHandle) session.insert(new B(0, "b"));
        PseudoClockScheduler pseudo = (PseudoClockScheduler) session.getTimerService();
        assertThat(pseudo.getQueue().size()).isEqualTo(1);
        pseudo.advanceTime(2000, TimeUnit.MILLISECONDS);
        session.fireAllRules(); // if the rest of the system is immediate, why isn't this?
        assertThat(pseudo.getQueue().size()).isEqualTo(0);

        createSession();
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        fhB0   = (InternalFactHandle) session.insert(new B(0, "b"));
        pseudo = (PseudoClockScheduler) session.getTimerService();
        assertThat(pseudo.getQueue().size()).isEqualTo(1);

        InternalFactHandle fhC0   = (InternalFactHandle) session.insert(new C(0, "c"));
        pseudo = (PseudoClockScheduler) session.getTimerService();
        assertThat(pseudo.getQueue().peek().isCanceled()).isTrue(); // cancelled timers, stay on the queue until they fire (where they noop)
        pseudo.advanceTime(2000, TimeUnit.MILLISECONDS);
        session.fireAllRules(); // if the rest of the system is immediate, why isn't this?
    }

    @Test
    public void testTimeoutWithCountFailure() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0}, // B
                                        new int[] {0},
                                        0);

        ConditionalSignalCounter counter = new ConditionalSignalCounter(0, 0, c -> c <= 2);
        counter.setOutput(gate1);
        gate1.setInputSignalCounters(new ConditionalSignalCounter[] {counter});

        //gate1.setPropagationTimer(new TimeoutTimer(gate1, new DurationTimer(1000)));
        gate1.setOutput(TerminatingSignalProcessor.get());

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        seq.setFilters(new Pattern[]{bpattern});
        rule.addSequence(seq);
        kbase.addPackage(pkg);

        createSession();

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle   fhB0   = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle   fhB1   = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle   fhB2   = (InternalFactHandle) session.insert(new B(0, "b"));
        PseudoClockScheduler pseudo = (PseudoClockScheduler) session.getTimerService();
//        assertThat(pseudo.getQueue().size()).isEqualTo(1);
//        pseudo.advanceTime(2000, TimeUnit.MILLISECONDS);
        session.fireAllRules(); // if the rest of the system is immediate, why isn't this?
//        assertThat(pseudo.getQueue().size()).isEqualTo(0);
//
//        mnode.getSequencer().start(sequencerMemory, session);
//        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
//        fhB0   = (InternalFactHandle) session.insert(new B(0, "b"));
//        pseudo = (PseudoClockScheduler) session.getTimerService();
//        assertThat(pseudo.getQueue().size()).isEqualTo(1);
//
//        InternalFactHandle fhC0   = (InternalFactHandle) session.insert(new C(0, "c"));
//        pseudo = (PseudoClockScheduler) session.getTimerService();
//        assertThat(pseudo.getQueue().peek().isCanceled()).isTrue(); // cancelled timers, stay on the queue until they fire (where they noop)
//        pseudo.advanceTime(2000, TimeUnit.MILLISECONDS);
//        session.fireAllRules(); // if the rest of the system is immediate, why isn't this?
    }


    @Test
    public void testDelayFromActivation() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B and C
                                        new int[] {0, 1}, // Each SignalAdapter must be in a unique index  for the Sequence
                                        0);

        gate1.setPropagationTimer(new DelayFromActivatedTimer(gate1, new DurationTimer(1000)));

        gate1.setOutput(TerminatingSignalProcessor.get());

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        seq.setFilters(new Pattern[]{bpattern, cpattern});
        rule.addSequence(seq);
        kbase.addPackage(pkg);

        createSession();

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle   fhB0   = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle fhC0   = (InternalFactHandle) session.insert(new C(0, "c"));
        PseudoClockScheduler pseudo = (PseudoClockScheduler) session.getTimerService();
        assertThat(pseudo.getQueue().size()).isEqualTo(1);
        pseudo.advanceTime(2000, TimeUnit.MILLISECONDS);
        session.fireAllRules(); // if the rest of the system is immediate, why isn't this?
        assertThat(pseudo.getQueue().size()).isEqualTo(0);

        createSession();

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        fhB0   = (InternalFactHandle) session.insert(new B(0, "b"));
        pseudo = (PseudoClockScheduler) session.getTimerService();
        assertThat(pseudo.getQueue().size()).isEqualTo(1);
        pseudo.advanceTime(2000, TimeUnit.MILLISECONDS);
        session.fireAllRules(); // if the rest of the system is immediate, why isn't this?
        assertThat(pseudo.getQueue().size()).isEqualTo(0);
    }

    @Test
    public void testDelayFromMatch() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B and C
                                        new int[] {0, 1}, // Each SignalAdapter must be in a unique index  for the Sequence
                                        0);

        gate1.setPropagationTimer(new DelayFromMatchTimer(gate1, new DurationTimer(1000)));

        gate1.setOutput(TerminatingSignalProcessor.get());

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        seq.setFilters(new Pattern[]{bpattern, cpattern});
        rule.addSequence(seq);
        kbase.addPackage(pkg);

        createSession();

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle   fhB0   = (InternalFactHandle) session.insert(new B(0, "b"));
        PseudoClockScheduler pseudo = (PseudoClockScheduler) session.getTimerService();
        assertThat(pseudo.getQueue().size()).isEqualTo(0); // not created activation, only on match
        InternalFactHandle fhC0   = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(pseudo.getQueue().size()).isEqualTo(1);
        pseudo.advanceTime(500, TimeUnit.MILLISECONDS);
        session.fireAllRules(); // if the rest of the system is immediate, why isn't this?
        assertThat(pseudo.getQueue().size()).isEqualTo(1); // still 1
        pseudo.advanceTime(1000, TimeUnit.MILLISECONDS);
        session.fireAllRules();
        assertThat(pseudo.getQueue().size()).isEqualTo(0);
    }

}
