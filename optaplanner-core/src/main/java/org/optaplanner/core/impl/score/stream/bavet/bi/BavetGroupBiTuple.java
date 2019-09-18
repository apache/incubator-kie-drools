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

package org.optaplanner.core.impl.score.stream.bavet.bi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.optaplanner.core.impl.score.stream.bavet.common.BavetGroupTuple;

public final class BavetGroupBiTuple<GroupKey_, ResultContainer_, Result_> extends BavetAbstractBiTuple<GroupKey_, Result_>
        implements BavetGroupTuple {

    private final BavetGroupBiNode<GroupKey_, ResultContainer_, Result_> node;

    private GroupKey_ groupKey;
    private int parentCount;
    private ResultContainer_ resultContainer;
    private Result_ result;

    protected List<BavetAbstractBiTuple<GroupKey_, Result_>> childTupleList;

    public BavetGroupBiTuple(BavetGroupBiNode<GroupKey_, ResultContainer_, Result_> node,
            GroupKey_ groupKey, ResultContainer_ resultContainer) {
        this.node = node;
        this.groupKey = groupKey;
        parentCount = 0;
        this.resultContainer = resultContainer;
        result = null;
        childTupleList = new ArrayList<>();
    }

    @Override
    public void refresh() {
        node.refresh(this);
    }

    public int increaseParentCount() {
        parentCount++;
        return parentCount;
    }

    public int decreaseParentCount() {
        parentCount--;
        if (parentCount < 0) {
            throw new IllegalStateException("The parentCount (" + parentCount + ") for groupKey (" + groupKey
                    + ") must not be negative.");
        }
        return parentCount;
    }

    public void clearResult() {
        result = null;
    }

    public void updateResult(Function<ResultContainer_, Result_> finisher) {
        result = finisher.apply(resultContainer);
    }

    @Override
    public String toString() {
        return "Group(" + getFactsString() + ")";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetGroupBiNode<GroupKey_, ResultContainer_, Result_> getNode() {
        return node;
    }

    @Override
    public GroupKey_ getFactA() {
        return groupKey;
    }

    @Override
    public Result_ getFactB() {
        return result;
    }

    public GroupKey_ getGroupKey() {
        return groupKey;
    }

    public ResultContainer_ getResultContainer() {
        return resultContainer;
    }

    public List<BavetAbstractBiTuple<GroupKey_, Result_>> getChildTupleList() {
        return childTupleList;
    }

}
