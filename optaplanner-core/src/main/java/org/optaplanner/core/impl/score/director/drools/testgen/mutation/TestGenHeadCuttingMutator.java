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
package org.optaplanner.core.impl.score.director.drools.testgen.mutation;

import java.util.List;

public class TestGenHeadCuttingMutator<T> {

    private final List<T> list;
    private double cutFactor = 0.8;
    private int totalCutSize = 0;
    private int revertIncrement = -1;
    private int cutIncrement;

    public TestGenHeadCuttingMutator(List<T> list) {
        this.list = list;
        updateIncrement(false);
    }

    public boolean canMutate() {
        return cutIncrement > 0 && totalCutSize + cutIncrement <= list.size();
    }

    public List<T> mutate() {
        totalCutSize += cutIncrement;
        revertIncrement = cutIncrement;
        updateIncrement(false);
        return getResult();
    }

    public void revert() {
        if (revertIncrement < 0) {
            throw new IllegalStateException("Can't revert without performing mutation first");
        }
        // revert cut size
        totalCutSize -= revertIncrement;
        revertIncrement = -1;
        // reduce cut factor
        cutFactor /= 2;
        updateIncrement(true);
    }

    private void updateIncrement(boolean revert) {
        cutIncrement = (int) ((list.size() - totalCutSize) * cutFactor);
        if (cutIncrement == 0 && !revert) {
            cutIncrement = 1;
        }
    }

    public List<T> getResult() {
        return list.subList(totalCutSize, list.size());
    }

}
