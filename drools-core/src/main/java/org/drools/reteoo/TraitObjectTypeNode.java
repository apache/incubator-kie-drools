/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reteoo;

import org.drools.base.ClassObjectType;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.factmodel.traits.TraitProxy;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;

public class TraitObjectTypeNode extends ObjectTypeNode {

    private BitSet typeMask;

    public TraitObjectTypeNode( int id, EntryPointNode source, ObjectType objectType, BuildContext context ) {
        super( id, source, objectType, context );

        typeMask = context.getRuleBase().getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy().getCode(
                ((ClassObjectType) objectType).getClassName()
        );
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        typeMask = (BitSet) in.readObject();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( typeMask );
    }

    public void modifyObject( InternalFactHandle factHandle,
                              ModifyPreviousTuples modifyPreviousTuples,
                              PropagationContext context,
                              InternalWorkingMemory workingMemory ) {
        if ( dirty ) {
            resetIdGenerator();
            updateTupleSinkId( this, this );
            dirty = false;
        }

        context.setObjectType( objectType );
        if ( compiledNetwork != null ) {
            compiledNetwork.modifyObject( factHandle,
                    modifyPreviousTuples,
                    context.adaptModificationMaskForObjectType( objectType, workingMemory ),
                    workingMemory );
        } else {
            if ( factHandle.getObject() instanceof TraitProxy )  {
                BitSet vetoMask = ((TraitProxy) factHandle.getObject()).getTypeFilter();
                if ( vetoMask == null ) {
                    this.sink.propagateModifyObject( factHandle,
                            modifyPreviousTuples,
                            context.adaptModificationMaskForObjectType( objectType, workingMemory ),
                            workingMemory );
                } else {
                    BitSet checkMask = (BitSet) typeMask.clone();
                    checkMask.and( vetoMask );
                    if ( ! checkMask.equals( typeMask ) ) {
                        this.sink.propagateModifyObject( factHandle,
                                modifyPreviousTuples,
                                context.adaptModificationMaskForObjectType( objectType, workingMemory ),
                                workingMemory );
                    }
                }
            } else {
                this.sink.propagateModifyObject( factHandle,
                        modifyPreviousTuples,
                        context.adaptModificationMaskForObjectType( objectType, workingMemory ),
                        workingMemory );
            }


        }
    }



}
