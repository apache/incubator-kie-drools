/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo.test.dsl;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.spi.PropagationContext;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.any;

public class BetaNodeStep
    implements
    Step {

    public BetaNodeStep(ReteTesterHelper reteTesterHelper) {
    }

    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        if ( args.size() == 1 ) {

            // The first argument list is the node parameters
            String[] a = args.get( 0 );
            String name = a[0];
            String leftInput = a[1];
            String rightInput = a[2];

            BetaNode betaNode = Mockito.mock( BetaNode.class, Mockito.withSettings().extraInterfaces( DSLMock.class ) );
            Mockito.doAnswer( new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    InternalFactHandle handle = (InternalFactHandle) args[0];
                    BetaNode node = (BetaNode) invocation.getMock();
                    // creating child RightTuple
                    new RightTupleImpl(handle, node);
                    return null;
                }
            }).when( betaNode ).assertObject( any(InternalFactHandle.class), 
                                              any(PropagationContext.class), 
                                              any(InternalWorkingMemory.class) );
            context.put( name,
                         betaNode );

            if ( ! leftInput.startsWith( "mock" ) ) {
                LeftTupleSource leftTupleSource = (LeftTupleSource) context.get( leftInput );
                leftTupleSource.addTupleSink( betaNode );
            }

            if ( ! rightInput.startsWith( "mock" ) ) {
                ObjectSource rightObjectSource = (ObjectSource) context.get( rightInput );
                rightObjectSource.addObjectSink( betaNode );
            }

        } else {
            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append( "Can not parse MockBetaNode step arguments: \n" );
            for ( String[] arg : args ) {
                msgBuilder.append( "    " );
                msgBuilder.append( Arrays.toString( arg ) );
                msgBuilder.append( "\n" );
            }
            throw new IllegalArgumentException( msgBuilder.toString() );
        }
    }

}
