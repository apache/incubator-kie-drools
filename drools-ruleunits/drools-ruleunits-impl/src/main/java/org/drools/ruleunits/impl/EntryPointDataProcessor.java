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
package org.drools.ruleunits.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalFactHandle;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.util.bitmask.BitMask;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.impl.facthandles.RuleUnitInternalFactHandle;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleContext;

public class EntryPointDataProcessor implements DataProcessor {
    private final EntryPoint entryPoint;

    private final Map<DataHandle, InternalFactHandle> handles = new HashMap<>();

    public EntryPointDataProcessor(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Override
    public FactHandle insert(DataHandle handle, Object object) {
        InternalFactHandle fh = (InternalFactHandle) entryPoint.insert(object);
        if (handle != null) {
            handles.put(handle, fh);
        }
        return fh;
    }

    public void insertLogical(RuleContext ruleContext, Object object) {
        ruleContext.insertLogical(entryPoint, object);
    }

    public void update(DataHandle dh, Object obj, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch) {
        update(handles.get(dh), obj, mask, modifiedClass, internalMatch);
    }

    public void update(InternalFactHandle fh, Object obj, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch) {
        ((WorkingMemoryEntryPoint) entryPoint).update(fh, obj, mask, modifiedClass, internalMatch);
    }

    @Override
    public void update(DataHandle handle, Object object) {
        entryPoint.update(handles.get(handle), object);
    }

    @Override
    public void delete(DataHandle handle) {
        entryPoint.delete(handles.remove(handle));
    }

    public void delete(DataHandle dh, RuleImpl rule, TerminalNode terminalNode, FactHandle.State fhState) {
        delete((RuleUnitInternalFactHandle) handles.get(dh), rule, terminalNode, fhState);
    }

    public void delete(RuleUnitInternalFactHandle fh, RuleImpl rule, TerminalNode terminalNode, FactHandle.State fhState) {
        ((WorkingMemoryEntryPoint) entryPoint).delete(fh, rule, terminalNode, fhState);
        handles.remove(fh.getDataHandle());
    }
}
