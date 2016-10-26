/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.reteoo.nodes;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.PropagationContext;

public class ReteEntryPointNode extends EntryPointNode {
    public ReteEntryPointNode() {
    }

    public ReteEntryPointNode(int id, ObjectSource objectSource, BuildContext context) {
        super(id, objectSource, context);
    }

    public ReteEntryPointNode(int id, RuleBasePartitionId partitionId, boolean partitionsEnabled, ObjectSource objectSource, EntryPointId entryPoint) {
        super(id, partitionId, partitionsEnabled, objectSource, entryPoint);
    }

    public void assertQuery(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        if ( queryNode == null ) {
            this.queryNode = objectTypeNodes.get( ClassObjectType.DroolsQuery_ObjectType );
        }

        if ( queryNode != null ) {
            // There may be no queries defined
            this.queryNode.propagateAssert( factHandle, context, workingMemory );
        }
    }

    public void retractQuery(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        if ( queryNode == null ) {
            this.queryNode = objectTypeNodes.get( ClassObjectType.DroolsQuery_ObjectType );
        }

        if ( queryNode != null ) {
            // There may be no queries defined
            this.queryNode.retractObject(factHandle, context, workingMemory);
        }
    }

    public void modifyQuery(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        if ( queryNode == null ) {
            this.queryNode = objectTypeNodes.get( ClassObjectType.DroolsQuery_ObjectType );
        }

        if ( queryNode != null ) {
            ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(factHandle.detachLinkedTuples());

            // There may be no queries defined
            this.queryNode.modifyObject( factHandle, modifyPreviousTuples, context, workingMemory );
            modifyPreviousTuples.retractTuples(context, workingMemory);
        }
    }

    public void modifyObject(final InternalFactHandle handle,
                             final PropagationContext pctx,
                             final ObjectTypeConf objectTypeConf,
                             final InternalWorkingMemory workingMemory) {
        if ( log.isTraceEnabled() ) {
            log.trace( "Update {}", handle.toString()  );
        }

        workingMemory.addPropagation(new ReteUpdate( handle, pctx, objectTypeConf) );
    }

    public static class ReteUpdate extends PropagationEntry.AbstractPropagationEntry {
        private final InternalFactHandle handle;
        private final PropagationContext context;
        private final ObjectTypeConf objectTypeConf;

        public ReteUpdate(InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf) {
            this.handle = handle;
            this.context = context;
            this.objectTypeConf = objectTypeConf;
        }

        public void execute(InternalWorkingMemory wm) {
            EntryPointNode.propagateModify(handle, context, objectTypeConf, wm, new ReteModifyPreviousTuples( handle.detachLinkedTuples() ));
        }

        @Override
        public String toString() {
            return "Update of " + handle.getObject();
        }
    }

    public static class ReteModifyPreviousTuples extends ModifyPreviousTuples {
        public ReteModifyPreviousTuples(InternalFactHandle.LinkedTuples linkedTuples) {
            super(linkedTuples);
        }

        public void doRightDelete(PropagationContext pctx, InternalWorkingMemory wm, RightTuple rightTuple) {
            ((BetaNode) rightTuple.getTupleSink()).retractRightTuple( rightTuple, pctx, wm );
        }

        public void doDeleteObject(PropagationContext pctx, InternalWorkingMemory wm, LeftTuple leftTuple) {
            leftTuple.retractTuple( pctx, wm );
        }
    }
}
