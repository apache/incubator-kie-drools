/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream.common;

public enum JoinerType {
    EQUAL_TO,
    LESS_THAN,
    LESS_THAN_OR_EQUAL_TO,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL_TO;

    public JoinerType opposite() {
        switch (this) {
            case LESS_THAN:
                return GREATER_THAN_OR_EQUAL_TO;
            case LESS_THAN_OR_EQUAL_TO:
                return GREATER_THAN;
            case GREATER_THAN:
                return LESS_THAN_OR_EQUAL_TO;
            case GREATER_THAN_OR_EQUAL_TO:
                return LESS_THAN;
            default:
                throw new IllegalStateException("The joinerType (" + this + ") is not supported.");
        }
    }

}
