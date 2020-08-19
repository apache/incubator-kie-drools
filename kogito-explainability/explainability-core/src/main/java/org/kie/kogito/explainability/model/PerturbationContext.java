/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.model;

import java.util.Random;

/**
 * Context object for performing perturbations.
 * This is meant to provide metadata and utilities that are needed to perturb different {@code TYpe}s.
 *
 * see {{@link Type#perturb(Value, PerturbationContext)}}
 */
public class PerturbationContext {

    private final Random random;

    private final int noOfPerturbations;

    public PerturbationContext(Random random, int noOfPerturbations) {
        this.random = random;
        this.noOfPerturbations = noOfPerturbations;
    }

    public int getNoOfPerturbations() {
        return noOfPerturbations;
    }

    public Random getRandom() {
        return random;
    }
}
