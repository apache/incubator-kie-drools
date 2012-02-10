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

package org.drools.marshalling.impl;

import java.io.IOException;

import org.drools.common.RuleFlowGroupImpl.DeactivateCallback;
import org.drools.common.TruthMaintenanceSystem.LogicalRetractCallback;
import org.drools.common.WorkingMemoryAction;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.PropagationQueuingNode.PropagateAction;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteAssertAction;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteExpireAction;
import org.drools.rule.SlidingTimeWindow.BehaviorExpireWMAction;

public class PersisterHelper {
    public static WorkingMemoryAction readWorkingMemoryAction(MarshallerReaderContext context) throws IOException,
                                                                                              ClassNotFoundException {
        int type = context.readShort();
        switch ( type ) {
            case WorkingMemoryAction.WorkingMemoryReteAssertAction : {
                return new WorkingMemoryReteAssertAction( context );
            }
            case WorkingMemoryAction.DeactivateCallback : {
                return new DeactivateCallback( context );
            }
            case WorkingMemoryAction.PropagateAction : {
                return new PropagateAction( context );
            }
            case WorkingMemoryAction.LogicalRetractCallback : {
                return new LogicalRetractCallback( context );
            }
            case WorkingMemoryAction.WorkingMemoryReteExpireAction : {
                return new WorkingMemoryReteExpireAction( context );
            }
            case WorkingMemoryAction.WorkingMemoryBehahviourRetract : {
                return new BehaviorExpireWMAction( context );

            }
        }
        return null;
    }

    public static WorkingMemoryAction deserializeWorkingMemoryAction(MarshallerReaderContext context,
                                                                     ProtobufMessages.ActionQueue.Action _action) throws IOException,
                                                                                                                 ClassNotFoundException {
        switch ( _action.getType() ) {
            case ASSERT : {
                return new WorkingMemoryReteAssertAction( context, 
                                                          _action );
            }
            case DEACTIVATE_CALLBACK : {
                return new DeactivateCallback(context, 
                                              _action );
            }
            case PROPAGATE : {
                return new PropagateAction(context,
                                           _action );
            }
            case LOGICAL_RETRACT : {
                return new LogicalRetractCallback(context,
                                                  _action );
            }
            case EXPIRE : {
                return new WorkingMemoryReteExpireAction(context,
                                                         _action );
            }
            case BEHAVIOR_EXPIRE : {
                return new BehaviorExpireWMAction( context,
                                                   _action );

            }         
            case SIGNAL : {
                // need to fix this
            }
            case SIGNAL_PROCESS_INSTANCE : {
                // need to fix this
            }
        }
        return null;
    }

    public void write(MarshallerWriteContext context) throws IOException {

    }

    public static ProtobufInputMarshaller.ActivationKey createActivationKey(final String pkgName,
                                                                            final String ruleName,
                                                                            final ProtobufMessages.Tuple _tuple) {
        int[] tuple = createTupleArray( _tuple );
        return new ProtobufInputMarshaller.ActivationKey( pkgName, ruleName, tuple );
    }

    public static ProtobufInputMarshaller.ActivationKey createActivationKey(final String pkgName,
                                                                            final String ruleName,
                                                                            final LeftTuple leftTuple) {
        int[] tuple = createTupleArray( leftTuple );
        return new ProtobufInputMarshaller.ActivationKey( pkgName, ruleName, tuple );
    }

    public static ProtobufMessages.Tuple createTuple( final LeftTuple leftTuple ) {
        ProtobufMessages.Tuple.Builder _tuple = ProtobufMessages.Tuple.newBuilder();
        for( LeftTuple entry = leftTuple; entry != null ; entry = entry.getParent() ) {
            _tuple.addHandleId( entry.getLastHandle().getId() );
        }
        return _tuple.build();
    }
    
    public static int[] createTupleArray(final ProtobufMessages.Tuple _tuple) {
        int[] tuple = new int[_tuple.getHandleIdCount()];
        for ( int i = 0; i < tuple.length; i++ ) {
            // needs to reverse the tuple elements 
            tuple[i] = _tuple.getHandleId( tuple.length - i - 1 );
        }
        return tuple;
    }

    public static int[] createTupleArray(final LeftTuple leftTuple) {
        int[] tuple = new int[leftTuple.size()];
        // tuple iterations happens backwards
        int i = tuple.length;
        for( LeftTuple entry = leftTuple; entry != null && i > 0; entry = entry.getParent() ) {
            // have to decrement i before assignment
            tuple[--i] = entry.getLastHandle().getId();
        }
        return tuple;
    }

    public static ProtobufInputMarshaller.TupleKey createTupleKey(final ProtobufMessages.Tuple _tuple) {
        return new ProtobufInputMarshaller.TupleKey( createTupleArray( _tuple ) );
    }
    
    public static ProtobufInputMarshaller.TupleKey createTupleKey(final LeftTuple leftTuple) {
        return new ProtobufInputMarshaller.TupleKey( createTupleArray( leftTuple ) );
    }
    
    public static ProtobufMessages.Activation createActivation(final String packageName,
                                                               final String ruleName,
                                                               final LeftTuple tuple) {
        return ProtobufMessages.Activation.newBuilder()
                        .setPackageName( packageName )
                        .setRuleName( ruleName )
                        .setTuple( createTuple( tuple ) )
                        .build();
    }
    
}
