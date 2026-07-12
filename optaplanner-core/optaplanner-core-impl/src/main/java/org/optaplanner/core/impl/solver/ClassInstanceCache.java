/*
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

package org.optaplanner.core.impl.solver;

import java.util.IdentityHashMap;
import java.util.Map;

import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;

/**
 * Exists so that particular user-provided classes,
 * which are instantiated by OptaPlanner from {@link SolverConfig},
 * only ever have one instance.
 * Each solver instance needs to get a fresh instance of this class,
 * so that solvers can not share state between them.
 * <p>
 * Case in point, {@link NearbyDistanceMeter}.
 * If two places in the config reference the same implementation of this interface,
 * we can be sure that they represent the same logic.
 * And since it is us who instantiate them and they require no-arg constructors,
 * we can be reasonably certain that they will not contain any context-specific state.
 * Therefore it is safe to have all of these references served by a single instance,
 * allowing for all sorts of beneficial caching.
 * (Such as, in this case, nearby distance matrix caching across the solver.)
 *
 * @see ConfigUtils#newInstance(Object, String, Class)
 */
public final class ClassInstanceCache {

    public static ClassInstanceCache create() {
        return new ClassInstanceCache();
    }

    private final Map<Class, Object> singletonMap = new IdentityHashMap<>();

    private ClassInstanceCache() {

    }

    public <T> T newInstance(Object configBean, String propertyName, Class<T> clazz) {
        return (T) singletonMap.computeIfAbsent(clazz, key -> ConfigUtils.newInstance(configBean, propertyName, key));
    }

}
