/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.taskassigning.domain;

public enum Affinity {
    NONE(4),
    LOW(3),
    MEDIUM(2),
    HIGH(1);

    private final int durationMultiplier;

    Affinity(int durationMultiplier) {
        this.durationMultiplier = durationMultiplier;
    }

    public int getDurationMultiplier() {
        return durationMultiplier;
    }

    public String getLabel() {
        switch (this) {
            case NONE:
                return "No affinity";
            case LOW:
                return "Low affinity";
            case MEDIUM:
                return "Medium affinity";
            case HIGH:
                return "High affinity";
            default:
                throw new IllegalStateException("The affinity (" + this + ") is not implemented.");
        }
    }

}
