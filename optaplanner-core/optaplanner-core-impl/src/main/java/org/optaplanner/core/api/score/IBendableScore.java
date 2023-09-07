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

package org.optaplanner.core.api.score;

import java.io.Serializable;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;

/**
 * Bendable score is a {@link Score} whose {@link #hardLevelsSize()} and {@link #softLevelsSize()}
 * are only known at runtime.
 *
 * @apiNote Interfaces in OptaPlanner are usually not prefixed with "I".
 *          However, the conflict in name with its implementation ({@link BendableScore}) made this necessary.
 *          All the other options were considered worse, some even harmful.
 *          This is a minor issue, as users will access the implementation and not the interface anyway.
 * @implSpec As defined by {@link Score}.
 * @param <Score_> the actual score type to allow addition, subtraction and other arithmetic
 */
public interface IBendableScore<Score_ extends IBendableScore<Score_>>
        extends Score<Score_>, Serializable {

    /**
     * The sum of this and {@link #softLevelsSize()} equals {@link #levelsSize()}.
     *
     * @return {@code >= 0} and {@code <} {@link #levelsSize()}
     */
    int hardLevelsSize();

    /**
     * As defined by {@link #hardLevelsSize()}.
     *
     * @deprecated Use {@link #hardLevelsSize()} instead.
     */
    @Deprecated(forRemoval = true)
    default int getHardLevelsSize() {
        return hardLevelsSize();
    }

    /**
     * The sum of {@link #hardLevelsSize()} and this equals {@link #levelsSize()}.
     *
     * @return {@code >= 0} and {@code <} {@link #levelsSize()}
     */
    int softLevelsSize();

    /**
     * As defined by {@link #softLevelsSize()}.
     *
     * @deprecated Use {@link #softLevelsSize()} instead.
     */
    @Deprecated(forRemoval = true)
    default int getSoftLevelsSize() {
        return softLevelsSize();
    }

    /**
     * @return {@link #hardLevelsSize()} + {@link #softLevelsSize()}
     */
    default int levelsSize() {
        return hardLevelsSize() + softLevelsSize();
    }

    /**
     * As defined by {@link #levelsSize()}.
     *
     * @deprecated Use {@link #levelsSize()} instead.
     */
    @Deprecated(forRemoval = true)
    default int getLevelsSize() {
        return levelsSize();
    }

}
