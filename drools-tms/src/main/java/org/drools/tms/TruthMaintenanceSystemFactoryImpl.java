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
package org.drools.tms;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.tms.beliefsystem.abductive.Abductive;

public class TruthMaintenanceSystemFactoryImpl implements TruthMaintenanceSystemFactory {

    private final Map<InternalWorkingMemoryEntryPoint, TruthMaintenanceSystem> tmsForEntryPoints = Collections.synchronizedMap(new IdentityHashMap<>());

    @Override
    public TruthMaintenanceSystem getOrCreateTruthMaintenanceSystem(ReteEvaluator reteEvaluator) {
        return getOrCreateTruthMaintenanceSystem((InternalWorkingMemoryEntryPoint) reteEvaluator.getDefaultEntryPoint());
    }

    @Override
    public TruthMaintenanceSystem getOrCreateTruthMaintenanceSystem(InternalWorkingMemoryEntryPoint entryPoint) {
        return tmsForEntryPoints.computeIfAbsent(entryPoint, TruthMaintenanceSystemImpl::new);
    }

    @Override
    public void clearTruthMaintenanceSystem(InternalWorkingMemoryEntryPoint entryPoint) {
        TruthMaintenanceSystem tms = tmsForEntryPoints.remove(entryPoint);
        if (tms != null) {
            tms.clear();
        }
    }

    @Override
    public QueryImpl createTmsQuery(String name, Predicate<Class<? extends Annotation>> hasAnnotation) {
        return hasAnnotation.test(Abductive.class) ? new AbductiveQuery(name) : new QueryImpl(name);
    }

    public int getEntryPointsMapSize() {
        // only for testing purposes
        return tmsForEntryPoints.size();
    }

    public void clearEntryPointsMap() {
        // only for testing purposes
        tmsForEntryPoints.clear();
    }
}
