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

package org.optaplanner.core.config.exhaustivesearch;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;

@XmlEnum
public enum ExhaustiveSearchType {
    BRUTE_FORCE,
    BRANCH_AND_BOUND;

    public EntitySorterManner getDefaultEntitySorterManner() {
        switch (this) {
            case BRUTE_FORCE:
                return EntitySorterManner.NONE;
            case BRANCH_AND_BOUND:
                return EntitySorterManner.DECREASING_DIFFICULTY_IF_AVAILABLE;
            default:
                throw new IllegalStateException("The exhaustiveSearchType ("
                        + this + ") is not implemented.");
        }
    }

    public ValueSorterManner getDefaultValueSorterManner() {
        switch (this) {
            case BRUTE_FORCE:
                return ValueSorterManner.NONE;
            case BRANCH_AND_BOUND:
                return ValueSorterManner.INCREASING_STRENGTH_IF_AVAILABLE;
            default:
                throw new IllegalStateException("The exhaustiveSearchType ("
                        + this + ") is not implemented.");
        }
    }

    public boolean isScoreBounderEnabled() {
        switch (this) {
            case BRUTE_FORCE:
                return false;
            case BRANCH_AND_BOUND:
                return true;
            default:
                throw new IllegalStateException("The exhaustiveSearchType ("
                        + this + ") is not implemented.");
        }
    }

}
