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
package org.drools.modelcompiler.consequence;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.model.BitMask;
import org.drools.model.DroolsEntryPoint;
import org.kie.api.runtime.rule.EntryPoint;

import static org.drools.modelcompiler.util.EvaluationUtil.adaptBitMask;

public class DroolsEntryPointImpl implements DroolsEntryPoint {

    private final EntryPoint entryPoint;
    private final FactHandleLookup fhLookup;

    public DroolsEntryPointImpl( EntryPoint entryPoint, FactHandleLookup fhLookup ) {
        this.entryPoint = entryPoint;
        this.fhLookup = fhLookup;
    }

    @Override
    public void insert( Object object ) {
        entryPoint.insert( object );
    }

    @Override
    public void insert(Object object, boolean dynamic) {
        ((WorkingMemoryEntryPoint ) entryPoint).insert(object, dynamic);
    }

    @Override
    public void update( Object object, String... modifiedProperties ) {
        entryPoint.update( fhLookup.get(object), object, modifiedProperties );
    }

    @Override
    public void update( Object object, BitMask modifiedProperties ) {
        Class<?> modifiedClass = modifiedProperties.getPatternClass();
        (( WorkingMemoryEntryPoint ) entryPoint).update( fhLookup.get(object), object, adaptBitMask(modifiedProperties), modifiedClass, null);
    }

    @Override
    public void delete( Object object ) {
        entryPoint.delete( fhLookup.get(object) );
    }
}