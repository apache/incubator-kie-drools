/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import org.optaplanner.core.impl.heuristic.selector.Selector;

public interface NearEntityNearbyMethod<O, S> {

    /**
     *
     * @param origin the entity it should be nearby to
     * @param nearbyIndex >= 0, 0 should return the origin (presuming {@link O} is an instanceof {@link S})
     * @return a selection that is compatible with the {@link Selector} that uses this.
     */
    S getByNearbyIndex(O origin, int nearbyIndex);


    // double getNearbyDistance(Entity from, Entity to)
}
