/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.score.definition;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum ScoreDefinitionType {
    SIMPLE,
    SIMPLE_LONG,
    /**
     * WARNING: NOT RECOMMENDED TO USE DUE TO ROUNDING ERRORS THAT CAUSE SCORE CORRUPTION.
     * Use {@link #SIMPLE_BIG_DECIMAL} instead.
     */
    @Deprecated(forRemoval = true)
    SIMPLE_DOUBLE,
    SIMPLE_BIG_DECIMAL,
    HARD_SOFT,
    HARD_SOFT_LONG,
    /**
     * WARNING: NOT RECOMMENDED TO USE DUE TO ROUNDING ERRORS THAT CAUSE SCORE CORRUPTION.
     * Use {@link #HARD_SOFT_BIG_DECIMAL} instead.
     */
    @Deprecated(forRemoval = true)
    HARD_SOFT_DOUBLE,
    HARD_SOFT_BIG_DECIMAL,
    HARD_MEDIUM_SOFT,
    HARD_MEDIUM_SOFT_LONG,
    BENDABLE,
    BENDABLE_LONG,
    BENDABLE_BIG_DECIMAL;
}
