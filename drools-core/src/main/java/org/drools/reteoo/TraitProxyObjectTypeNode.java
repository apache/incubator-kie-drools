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
import org.drools.common.PropagationContextImpl;
import org.drools.factmodel.traits.Key;
import org.drools.factmodel.traits.Thing;
import org.drools.factmodel.traits.TraitProxy;
import org.drools.factmodel.traits.TraitTypeMap;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.util.HierarchyEncoderImpl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.Collection;

public class TraitProxyObjectTypeNode extends ObjectTypeNode {


    public TraitProxyObjectTypeNode( int id, EntryPointNode source, ObjectType objectType, BuildContext context ) {
        super( id, source, objectType, context );
    }


    @Override
    public void assertObject( InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory ) {
        super.assertObject( factHandle, context, workingMemory );
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
        // node can't have sinks. Avoid mask recalculations and other operations on updates
    }


}
