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

package org.drools.core.reteoo;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitTypeMap;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.HierarchyEncoderImpl;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.Collection;

public class TraitObjectTypeNode extends ObjectTypeNode {

    private BitSet typeMask;

    public TraitObjectTypeNode( int id, EntryPointNode source, ObjectType objectType, BuildContext context ) {
        super( id, source, objectType, context );

        typeMask = context.getKnowledgeBase().getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy().getCode(
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


    @Override
    public void assertObject( InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory ) {
        if ( factHandle.getObject() instanceof TraitProxy )  {
            BitSet vetoMask = ((TraitProxy) factHandle.getObject()).getTypeFilter();
            boolean isVetoed = vetoMask != null && ! typeMask.isEmpty() && HierarchyEncoderImpl.supersetOrEqualset( vetoMask, this.typeMask );
            if ( ! isVetoed || sameAndNotCoveredByDescendants( (TraitProxy) factHandle.getObject(), typeMask ) ) {
//                System.out.println( ((ClassObjectType) this.getObjectType()).getClassName() + " : Assert PASS " + ( (TraitProxy) factHandle.getObject() ).getTraitName() + " " + ( (TraitProxy) factHandle.getObject() ).getTypeCode() + " >> " + vetoMask + " checks in " + typeMask );
                super.assertObject( factHandle, context, workingMemory );
            } else {
//                System.out.println( ( (ClassObjectType) this.getObjectType() ).getClassName() + " : Assert BLOCK " + ( (TraitProxy) factHandle.getObject() ).getTraitName() + " >> " + vetoMask + " checks in " + typeMask );
            }
        } else {
            super.assertObject( factHandle, context, workingMemory );
        }

    }

    /**
     *  Edge case: due to the way traits are encoded, consider this hierarchy:
     *  A    B
     *    C
     *    D
     *  On don/insertion of C, C may be vetoed by its parents, but might have been
     *  already covered by one of its descendants (D)
     */
    private boolean sameAndNotCoveredByDescendants( TraitProxy proxy, BitSet typeMask ) {
        boolean isSameType = typeMask.equals( proxy.getTypeCode() );
        if ( isSameType ) {
            Collection descs = ((TraitTypeMap) proxy.getObject()._getTraitMap()).immediateChildren( typeMask );
            // we have to exclude the "mock" bottom proxy
            return descs.size() <= 1;
        } else {
            return false;
        }
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
                TraitProxy proxy = ((TraitProxy) factHandle.getObject());
                BitSet vetoMask = proxy.getTypeFilter();

                if ( vetoMask == null                                                               // no vetos
                     || typeMask.isEmpty()                                                          // Thing is permissive
                     || ! HierarchyEncoderImpl.supersetOrEqualset( vetoMask, this.typeMask ) ) {    // this node is not vetoed

                    // "don" update :
                    if ( context.getModificationMask().isSet(PropertySpecificUtil.TRAITABLE_BIT) ) {
                        // property reactivity may block trait proxies which have been asserted and then immediately updated because of another "don"
                        // however, PR must be disabled only once for each OTN: that is, a proxy will not pass an OTN if one of its ancestors can also pass it

                        // Example: given classes A <- B <-C, at OTN A, a proxy c can only pass if no proxy b exists

                        TraitableBean txBean = (TraitableBean) proxy.getObject();
                        TraitTypeMap tMap = (TraitTypeMap) txBean._getTraitMap();
                        Collection<Thing> x = tMap.immediateParents( this.typeMask );
                        Thing k = x.iterator().next();

                        BitMask originalMask = context.getModificationMask();
                        if ( ! k.isTop() ) {
                            context.setModificationMask( AllSetBitMask.get() );
                        }
                        //System.out.println(" MODIFY PASS !! " + factHandle.getObject() + " " + ( (TraitProxy) factHandle.getObject() ).getTypeCode() + " >> " + vetoMask + " checks in " + typeMask );
                        this.sink.propagateModifyObject( factHandle,
                                modifyPreviousTuples,
                                context.adaptModificationMaskForObjectType( objectType, workingMemory ),
                                workingMemory );
                        context.setModificationMask( originalMask );

                    } else {
                        //System.out.println(" MODIFY PASS !! " + factHandle.getObject() + " " + ( (TraitProxy) factHandle.getObject() ).getTypeCode() + " >> " + vetoMask + " checks in " + typeMask );
                        this.sink.propagateModifyObject( factHandle,
                                modifyPreviousTuples,
                                context.adaptModificationMaskForObjectType( objectType, workingMemory ),
                                workingMemory );
                    }

                } else {
                    //System.out.println( ((ClassObjectType) this.getObjectType()).getClassName() + " : MODIFY BLOCK !! " + ( (TraitProxy) factHandle.getObject() ).getTraitName() + " " + ( (TraitProxy) factHandle.getObject() ).getTypeCode() + " >> " + vetoMask + " checks in " + typeMask );
                }
            } else {
                this.sink.propagateModifyObject( factHandle,
                                                 modifyPreviousTuples,
                                                 !context.getModificationMask().isSet(PropertySpecificUtil.TRAITABLE_BIT) ?
                                                        context.adaptModificationMaskForObjectType( objectType, workingMemory ) :
                                                        context,
                                                 workingMemory );
            }
        }
    }

    public boolean needsMaskUpdate() {
        return true;
    }

    @Override
    public BitMask updateMask(BitMask mask) {
        BitMask returnMask;
        returnMask = declaredMask.clone().setAll( mask );
        inferredMask = inferredMask.setAll( returnMask );
        return returnMask;
    }

}
