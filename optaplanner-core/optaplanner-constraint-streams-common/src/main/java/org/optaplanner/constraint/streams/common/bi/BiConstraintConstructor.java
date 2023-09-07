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

package org.optaplanner.constraint.streams.common.bi;

import java.util.Collection;
import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.common.ConstraintConstructor;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;

@FunctionalInterface
public interface BiConstraintConstructor<A, B, Score_ extends Score<Score_>>
        extends ConstraintConstructor<Score_, TriFunction<A, B, Score_, Object>, BiFunction<A, B, Collection<?>>> {

}
