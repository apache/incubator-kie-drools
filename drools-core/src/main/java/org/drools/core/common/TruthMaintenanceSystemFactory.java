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
package org.drools.core.common;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

import org.drools.base.common.MissingDependencyException;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.kie.api.internal.utils.KieService;

public interface TruthMaintenanceSystemFactory extends KieService {
    String NO_TMS = "You're trying to use the Truth Maintenance System without having imported it. Please add the module org.drools:drools-tms to your classpath.";

    TruthMaintenanceSystem getOrCreateTruthMaintenanceSystem(ReteEvaluator reteEvaluator);
    TruthMaintenanceSystem getOrCreateTruthMaintenanceSystem(InternalWorkingMemoryEntryPoint entryPoint);
    void clearTruthMaintenanceSystem(InternalWorkingMemoryEntryPoint entryPoint);

    class Holder {
        private static final TruthMaintenanceSystemFactory INSTANCE = KieService.load( TruthMaintenanceSystemFactory.class );
    }

    static TruthMaintenanceSystemFactory get() {
        return present() ? TruthMaintenanceSystemFactory.Holder.INSTANCE : throwExceptionForMissingTms();
    }

    static boolean present() {
        return TruthMaintenanceSystemFactory.Holder.INSTANCE != null;
    }

    static QueryImpl createQuery(String name, Predicate<Class<? extends Annotation>> hasAnnotation) {
        return present() ? get().createTmsQuery(name, hasAnnotation) : new QueryImpl(name);
    }

    QueryImpl createTmsQuery(String name, Predicate<Class<? extends Annotation>> hasAnnotation);

    static <T> T throwExceptionForMissingTms() {
        throw new MissingDependencyException(NO_TMS);
    }
}
