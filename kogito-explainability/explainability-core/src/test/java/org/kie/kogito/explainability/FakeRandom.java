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
package org.kie.kogito.explainability;

import java.util.Random;

public class FakeRandom extends Random {

    @Override
    protected int next(int bits) {
        return 1;
    }

    @Override
    public int nextInt() {
        return 1;
    }

    @Override
    public int nextInt(int bound) {
        return 1;
    }

    @Override
    public long nextLong() {
        return 1;
    }

    @Override
    public boolean nextBoolean() {
        return true;
    }

    @Override
    public float nextFloat() {
        return 1;
    }

    @Override
    public double nextDouble() {
        return 1;
    }

    @Override
    public synchronized double nextGaussian() {
        return 1;
    }
}
