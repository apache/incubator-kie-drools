/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.supply;

/**
 * Provides a {@link Supply} for subsystems that submit a {@link Demand}.
 */
public interface SupplyManager {

    /**
     * Returns the {@link Supply} for a {@link Demand}, preferably an existing one.
     * If the {@link Supply} doesn't exist yet (as part of the domain model or externalized), it creates and attaches it.
     *
     * @param demand never null
     * @param <Supply_> Subclass of {@link Supply}
     * @return never null
     */
    <Supply_ extends Supply> Supply_ demand(Demand<Supply_> demand);

}
