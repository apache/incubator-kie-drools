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

package org.drools.reteoo.test.dsl;

import java.util.List;
import java.util.Map;

import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleImpl;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.RightTuple;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

public class LeftTupleSinkStep
    implements
    Step {

    public LeftTupleSinkStep(ReteTesterHelper reteTesterHelper) {
    }

    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        if ( args.size() != 0 ) {
            String[] a = args.get( 0 );
            String name = a[0].trim();
            String leftInput = a[1].trim();

            LeftTupleSource leftTupleSource = (LeftTupleSource) context.get( leftInput );

            LeftTupleSink mockedSink = Mockito.mock( LeftTupleSink.class,
                                                     Mockito.withSettings().extraInterfaces( DSLMock.class ) );

            Mockito.when( mockedSink.createLeftTuple( (InternalFactHandle) anyObject(), (LeftTupleSink) anyObject(), anyBoolean() ) ).thenAnswer( new Answer() {

                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();                   
                    return new LeftTupleImpl((InternalFactHandle)args[0], (LeftTupleSink)args[1], ((Boolean)args[2]).booleanValue()  );
                }
            });
                        
            
            Mockito.when( mockedSink.createLeftTuple( (LeftTuple) anyObject(), (LeftTupleSink) anyObject(), anyBoolean() ) ).thenAnswer( new Answer() {

                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();                   
                    return new LeftTupleImpl((LeftTuple)args[0], (LeftTupleSink)args[1], ((Boolean)args[2]).booleanValue()  );
                }
            });      
            
            Mockito.when( mockedSink.createLeftTuple( (LeftTuple) anyObject(), (RightTuple) anyObject(), (LeftTupleSink) anyObject()) ).thenAnswer( new Answer() {
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();                   
                    return new LeftTupleImpl((LeftTuple)args[0], (RightTuple)args[1], (LeftTupleSink) args[2] );
                }
            });              
            
            Mockito.when( mockedSink.createLeftTuple( (LeftTuple) anyObject(), (RightTuple) anyObject(),  (LeftTuple) anyObject(), (LeftTuple) anyObject(), (LeftTupleSink) anyObject(), anyBoolean()) ).thenAnswer( new Answer() {
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();                   
                    return new LeftTupleImpl((LeftTuple)args[0], (RightTuple)args[1], (LeftTuple)args[2], (LeftTuple)args[3], (LeftTupleSink)args[4], ((Boolean)args[5]).booleanValue() );
                }
            });             
            
            
                          //Mockito.doCallRealMethod().when( mockedSink ).createLeftTuple( (LeftTuple) null, null, null);
            
            leftTupleSource.addTupleSink( mockedSink );

            context.put( name,
                         mockedSink );

        } else {
            throw new IllegalArgumentException( "Cannot parse arguments " + args );

        }
    }
}
