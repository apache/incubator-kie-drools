/**
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

package org.drools.reteoo.test.dsl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.RightTuple;
import org.drools.spi.PropagationContext;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

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
                    new RightTuple(handle, node);
                    return null;
                }
            }).when( betaNode ).assertObject( any(InternalFactHandle.class), 
                                              any(PropagationContext.class), 
                                              any(InternalWorkingMemory.class) );
            context.put( name,
                         betaNode );

            if ( ! "mock".equals( leftInput ) ) {
                LeftTupleSource leftTupleSource = (LeftTupleSource) context.get( leftInput );
                leftTupleSource.addTupleSink( betaNode );
            }

            if ( ! "mock".equals( rightInput ) ) {
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