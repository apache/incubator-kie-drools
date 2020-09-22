/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.ancompiler;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;

public class CompiledObjectTypeNode extends ObjectTypeNode {

    protected CompiledNetwork compiledNetwork;

    public CompiledNetwork getCompiledNetwork() {
        return this.compiledNetwork;
    }

    public CompiledObjectTypeNode(final int id,
                                  final EntryPointNode source,
                                  final ObjectType objectType,
                                  final BuildContext context) {
        super(id, source, objectType, context);
    }

    public void setCompiledNetwork(CompiledNetwork compiledNetwork) {
        this.compiledNetwork = compiledNetwork;
        this.compiledNetwork.setObjectTypeNode(this);
    }

    public void propagateAssert(InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory) {
        checkDirty();
        compiledNetwork.assertObject(factHandle,
                                     context,
                                     workingMemory);
    }

    @Override
    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        checkDirty();

        compiledNetwork.modifyObject(factHandle,
                                     modifyPreviousTuples,
                                     context.adaptModificationMaskForObjectType(objectType, workingMemory),
                                     workingMemory);
    }
}
