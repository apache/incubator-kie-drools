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

package org.optaplanner.core.impl.heuristic.selector;

import org.optaplanner.core.impl.domain.variable.supply.Demand;

/**
 * It is expected that if two instances share the same properties,
 * they are {@link Object#equals(Object) equal} to one another.
 * This is necessary for proper performance of {@link Demand}-based caches,
 * such as pillar cache or nearby distance matrix cache.
 *
 * @param <Solution_>
 */
public abstract class AbstractDemandEnabledSelector<Solution_> extends AbstractSelector<Solution_> {

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

}
