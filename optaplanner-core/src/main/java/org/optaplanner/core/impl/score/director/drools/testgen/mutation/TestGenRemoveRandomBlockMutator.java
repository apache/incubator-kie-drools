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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestGenRemoveRandomBlockMutator<T> {

    private final List<T> list;
    private final Random random = new Random(0);
    private final List<Integer> indexBlacklist = new ArrayList<>();
    private int blockPortion = 10;
    private int removedIndex = -1;
    private List<T> removedBlock;

    public TestGenRemoveRandomBlockMutator(List<T> list) {
        this.list = new ArrayList<>(list);
    }

    public boolean canMutate() {
        return !list.isEmpty() && list.size() > indexBlacklist.size();
    }

    public List<T> mutate() {
        if (!canMutate()) {
            throw new IllegalStateException("No more mutations possible.");
        }

        if (removedIndex >= 0) {
            // last mutation was successful => clear the blacklist
            indexBlacklist.clear();
        }

        int blockSize = Math.max(list.size() / blockPortion, 1);
        if (indexBlacklist.size() == list.size() / blockSize && list.size() / blockPortion > 1) {
            // we've tried all blocks without success => try smaller blocks and clear the blacklist
            blockPortion *= 2;
            indexBlacklist.clear();
        }

        blockSize = Math.max(list.size() / blockPortion, 1);

        do {
            removedIndex = random.nextInt(list.size() / blockSize) * blockSize;
        } while (indexBlacklist.contains(removedIndex));

        removedBlock = new ArrayList<>(list.subList(removedIndex, removedIndex + blockSize));
        list.removeAll(removedBlock);
        return list;
    }

    public void revert() {
        // return the item
        list.addAll(removedIndex, removedBlock);
        // don't try this index on next mutation
        indexBlacklist.add(removedIndex);
        // last mutation wasn't successful
        removedIndex = -1;
    }

    public List<T> getResult() {
        return list;
    }

    public List<T> getRemovedBlock() {
        return removedBlock;
    }

}
