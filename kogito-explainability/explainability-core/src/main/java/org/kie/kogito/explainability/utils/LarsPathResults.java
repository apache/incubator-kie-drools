/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.utils;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class LarsPathResults {
    private final RealMatrix coefs;
    private final RealVector alphas;
    private final List<Integer> active;
    private final int nIter;

    public LarsPathResults(RealMatrix coefs, RealVector alphas, List<Integer> active, int nIter) {
        this.coefs = coefs;
        this.alphas = alphas;
        this.active = active;
        this.nIter = nIter;
    }

    public RealMatrix getCoefs() {
        return coefs;
    }

    public RealVector getAlphas() {
        return alphas;
    }

    public List<Integer> getActive() {
        return active;
    }

    public int getnIter() {
        return nIter;
    }
}
