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

package org.optaplanner.core.config.heuristic.selector.move.generic;

import java.util.Comparator;

import jakarta.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum SubPillarType {

    /**
     * Pillars will only be affected in their entirety.
     */
    NONE,
    /**
     * Pillars may also be affected partially, and the resulting subpillar returned in an order according to a given
     * {@link Comparator}.
     */
    SEQUENCE,
    /**
     * Pillars may also be affected partially, the resulting subpillar returned in random order.
     */
    ALL;
}
