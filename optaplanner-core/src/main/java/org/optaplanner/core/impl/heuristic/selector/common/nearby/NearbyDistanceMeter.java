/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

public interface NearbyDistanceMeter<O, D> {

    /**
     * Measures the distance from the origin to the destination.
     * The distance can be in any unit, such a meters, foot, seconds or milliseconds.
     * For example, vehicle routing often uses driving time in seconds.
     * <p>
     * Distances can be asymmetrical: the distance from an origin to a destination
     * often differs from the distance from that destination to that origin.
     * @param origin never null
     * @param destination never null
     * @return Preferably always {@code >= 0.0}. If origin == destination, it usually returns 0.0.
     */
    double getNearbyDistance(O origin, D destination);

}
