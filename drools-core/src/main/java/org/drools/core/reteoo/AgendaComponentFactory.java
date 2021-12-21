/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.kie.api.internal.utils.ServiceRegistry;

public interface AgendaComponentFactory {

    LeftTuple createTerminalTuple();
    LeftTuple createTerminalTuple(InternalFactHandle factHandle, Sink sink, boolean leftTupleMemoryEnabled);
    LeftTuple createTerminalTuple(InternalFactHandle factHandle, LeftTuple leftTuple, Sink sink);
    LeftTuple createTerminalTuple(LeftTuple leftTuple, Sink sink, PropagationContext pctx, boolean leftTupleMemoryEnabled);
    LeftTuple createTerminalTuple(LeftTuple leftTuple, RightTuple rightTuple, Sink sink);
    LeftTuple createTerminalTuple(LeftTuple leftTuple, RightTuple rightTuple, LeftTuple currentLeftChild, LeftTuple currentRightChild, Sink sink, boolean leftTupleMemoryEnabled);

    RuleAgendaItem createAgendaItem(long activationNumber, Tuple tuple, int salience, PropagationContext context, PathMemory pmem, TerminalNode rtn, boolean declarativeAgendaEnabled, InternalAgendaGroup agendaGroup);

    class Holder {
        private static final AgendaComponentFactory INSTANCE = createInstance();

        static AgendaComponentFactory createInstance() {
            AgendaComponentFactory factory = ServiceRegistry.getService( AgendaComponentFactory.class );
            return factory != null ? factory : new AgendaComponentFactoryImpl();
        }
    }

    static AgendaComponentFactory get() {
        return AgendaComponentFactory.Holder.INSTANCE;
    }

    class AgendaComponentFactoryImpl implements AgendaComponentFactory {

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
                                             final LeftTuple leftTuple,
                                             final Sink sink) {
            return new RuleTerminalNodeLeftTuple(factHandle,leftTuple, sink );
        }

        @Override
        public LeftTuple createTerminalTuple(LeftTuple leftTuple,
                                             Sink sink,
                                             PropagationContext pctx,
                                             boolean leftTupleMemoryEnabled) {
            return new RuleTerminalNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
        }

        @Override
        public LeftTuple createTerminalTuple(LeftTuple leftTuple,
                                             RightTuple rightTuple,
                                             Sink sink) {
            return new RuleTerminalNodeLeftTuple(leftTuple, rightTuple, sink );
        }

        @Override
        public LeftTuple createTerminalTuple(LeftTuple leftTuple,
                                             RightTuple rightTuple,
                                             LeftTuple currentLeftChild,
                                             LeftTuple currentRightChild,
                                             Sink sink,
                                             boolean leftTupleMemoryEnabled) {
            return new RuleTerminalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
        }

        @Override
        public RuleAgendaItem createAgendaItem(long activationNumber, Tuple tuple, int salience, PropagationContext context, PathMemory pmem, TerminalNode rtn, boolean declarativeAgendaEnabled, InternalAgendaGroup agendaGroup) {
            return new RuleAgendaItem(activationNumber, tuple, salience, context, pmem, rtn, declarativeAgendaEnabled, agendaGroup);
        }
    }
}
