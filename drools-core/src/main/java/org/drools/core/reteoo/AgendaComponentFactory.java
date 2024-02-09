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
package org.drools.core.reteoo;

import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.phreak.RuleAgendaItem;
import org.kie.api.internal.utils.KieService;

public interface AgendaComponentFactory extends KieService {

    LeftTuple createTerminalTuple();
    LeftTuple createTerminalTuple(InternalFactHandle factHandle, Sink sink, boolean leftTupleMemoryEnabled);
    LeftTuple createTerminalTuple(InternalFactHandle factHandle, TupleImpl leftTuple, Sink sink);
    LeftTuple createTerminalTuple(TupleImpl leftTuple, Sink sink, PropagationContext pctx, boolean leftTupleMemoryEnabled);
    LeftTuple createTerminalTuple(TupleImpl leftTuple, TupleImpl rightTuple, Sink sink);
    LeftTuple createTerminalTuple(TupleImpl leftTuple, TupleImpl rightTuple, TupleImpl currentLeftChild, TupleImpl currentRightChild, Sink sink, boolean leftTupleMemoryEnabled);

    RuleAgendaItem createAgendaItem(int salience, PathMemory pmem, TerminalNode rtn, boolean declarativeAgendaEnabled, InternalAgendaGroup agendaGroup);

    class Holder {
        private static final AgendaComponentFactory INSTANCE = createInstance();

        static AgendaComponentFactory createInstance() {
            AgendaComponentFactory factory = KieService.load( AgendaComponentFactory.class );
            return factory != null ? factory : new AgendaComponentFactoryImpl();
        }
    }

    static AgendaComponentFactory get() {
        return AgendaComponentFactory.Holder.INSTANCE;
    }

    class AgendaComponentFactoryImpl implements AgendaComponentFactory {

        public AgendaComponentFactoryImpl() {
        }

        @Override
        public LeftTuple createTerminalTuple() {
            return new RuleTerminalNodeLeftTuple();
        }

        @Override
        public LeftTuple createTerminalTuple(InternalFactHandle factHandle,
                                             Sink sink,
                                             boolean leftTupleMemoryEnabled) {
            return new RuleTerminalNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
        }

        @Override
        public LeftTuple createTerminalTuple(final InternalFactHandle factHandle,
                                             final TupleImpl leftTuple,
                                             final Sink sink) {
            return new RuleTerminalNodeLeftTuple(factHandle,leftTuple, sink );
        }

        @Override
        public LeftTuple createTerminalTuple(TupleImpl leftTuple,
                                             Sink sink,
                                             PropagationContext pctx,
                                             boolean leftTupleMemoryEnabled) {
            return new RuleTerminalNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
        }

        @Override
        public LeftTuple createTerminalTuple(TupleImpl leftTuple,
                                             TupleImpl rightTuple,
                                             Sink sink) {
            return new RuleTerminalNodeLeftTuple(leftTuple, rightTuple, sink );
        }

        @Override
        public LeftTuple createTerminalTuple(TupleImpl leftTuple,
                                             TupleImpl rightTuple,
                                             TupleImpl currentLeftChild,
                                             TupleImpl currentRightChild,
                                             Sink sink,
                                             boolean leftTupleMemoryEnabled) {
            return new RuleTerminalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
        }

        @Override
        public RuleAgendaItem createAgendaItem(int salience, PathMemory pmem, TerminalNode rtn, boolean declarativeAgendaEnabled, InternalAgendaGroup agendaGroup) {
            return new RuleAgendaItem(salience, pmem, rtn, declarativeAgendaEnabled, agendaGroup);
        }
    }
}
