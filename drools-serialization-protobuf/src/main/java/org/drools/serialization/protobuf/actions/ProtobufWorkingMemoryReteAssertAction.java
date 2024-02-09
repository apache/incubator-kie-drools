/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.serialization.protobuf.actions;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.serialization.protobuf.WorkingMemoryReteAssertAction;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.core.reteoo.Tuple;
import org.drools.serialization.protobuf.PersisterHelper;
import org.drools.serialization.protobuf.ProtobufInputMarshaller;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.ProtobufWorkingMemoryAction;

public class ProtobufWorkingMemoryReteAssertAction
        extends WorkingMemoryReteAssertAction
        implements ProtobufWorkingMemoryAction {

    public ProtobufWorkingMemoryReteAssertAction( MarshallerReaderContext context,
                                                  ProtobufMessages.ActionQueue.Action _action) {
        ProtobufMessages.ActionQueue.Assert _assert = _action.getAssert();
        this.factHandle = context.getHandles().get( _assert.getHandleId() );
        this.removeLogical = _assert.getRemoveLogical();
        this.updateEqualsMap = _assert.getUpdateEqualsMap();

        if ( _assert.hasTuple() ) {
            String pkgName = _assert.getOriginPkgName();
            String ruleName = _assert.getOriginRuleName();
            InternalKnowledgePackage pkg = context.getKnowledgeBase().getPackage( pkgName );
            this.ruleOrigin = pkg.getRule( ruleName );
            this.tuple = ((ProtobufInputMarshaller.PBActivationsFilter)context.getFilter()).getTuplesCache().get( PersisterHelper.createActivationKey(pkgName, ruleName, _assert.getTuple()) );
        }
    }

    @Override
    public ProtobufMessages.ActionQueue.Action serialize( MarshallerWriteContext context) {
        ProtobufMessages.ActionQueue.Assert.Builder _assert = ProtobufMessages.ActionQueue.Assert.newBuilder();
        _assert.setHandleId( this.factHandle.getId() )
                .setRemoveLogical( this.removeLogical )
                .setUpdateEqualsMap( this.updateEqualsMap );

        if ( this.tuple != null ) {
            ProtobufMessages.Tuple.Builder _tuple = ProtobufMessages.Tuple.newBuilder();
            for( Tuple entry = this.tuple; entry != null; entry = entry.getParent() ) {
                _tuple.addHandleId( entry.getFactHandle().getId() );
            }
            _assert.setOriginPkgName( ruleOrigin.getPackageName() )
                    .setOriginRuleName( ruleOrigin.getName() )
                    .setTuple(_tuple.build());
        }
        return ProtobufMessages.ActionQueue.Action.newBuilder()
                .setType( ProtobufMessages.ActionQueue.ActionType.ASSERT )
                .setAssert( _assert.build() )
                .build();
    }
}