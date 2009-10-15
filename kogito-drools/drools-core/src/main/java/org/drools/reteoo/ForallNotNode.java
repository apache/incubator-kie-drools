package org.drools.reteoo;

/*
 * Copyright 20059 JBoss Inc
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

/**
 * A 'forall' conditional element is translated into a specific set of notes in the network. As 
 * an example, the following construction:
 * 
 * forall( Bus( color == RED ) )
 * 
 * Is translated into:
 * 
 * not*( $bus : Bus( ) and not ( Bus( this == $bus, color == RED ) ) )
 * 
 * This class implements the modified 'not' node that corresponds to the 'not*' CE above.
 * It behaves like a not node in all aspects but for the right activations that never trigger
 * propagations.  
 * 
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 */
public class ForallNotNode extends NotNode {
    private static final long serialVersionUID = 510L;

    private ObjectType        baseObjectType   = null;

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public ForallNotNode() {
    }

    /**
     * Construct.
     *
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>TupleSource</code>.
     * @param objectType 
     */
    public ForallNotNode(final int id,
                         final LeftTupleSource leftInput,
                         final ObjectSource rightInput,
                         final BetaConstraints joinNodeBinder,
                         final Behavior[] behaviors,
                         final BuildContext context,
                         ObjectType objectType) {
        super( id,
               leftInput,
               rightInput,
               joinNodeBinder,
               behaviors,
               context );
        this.baseObjectType = objectType;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        baseObjectType = (ObjectType) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( baseObjectType );
    }

    @Override
    protected void propagateRetractLeftTuple(PropagationContext context,
                                             InternalWorkingMemory workingMemory,
                                             LeftTuple leftTuple) {
        if ( !this.baseObjectType.isAssignableFrom( workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.getEntryPoint(),
                                                                                                                          ((InternalFactHandle) context.getFactHandle()).getObject() ).getConcreteObjectTypeNode().getObjectType() ) ) {
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );
        }
    }

    @Override
    protected void propagateAssertLeftTuple(PropagationContext context,
                                            InternalWorkingMemory workingMemory,
                                            LeftTuple leftTuple) {
        if ( !this.baseObjectType.isAssignableFrom( workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.getEntryPoint(),
                                                                                                                          ((InternalFactHandle) context.getFactHandle()).getObject() ).getConcreteObjectTypeNode().getObjectType() ) ) {
            this.sink.propagateAssertLeftTuple( leftTuple,
                                                context,
                                                workingMemory,
                                                this.tupleMemoryEnabled );
        }
    }

    public short getType() {
        return NodeTypeEnums.ForallNotNode;
    }

    public ObjectType getBaseObjectType() {
        return baseObjectType;
    }

    public void setBaseObjectType(ObjectType baseObjectType) {
        this.baseObjectType = baseObjectType;
    }
}
