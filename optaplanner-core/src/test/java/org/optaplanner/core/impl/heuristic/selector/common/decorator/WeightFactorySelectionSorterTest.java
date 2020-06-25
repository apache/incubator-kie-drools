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

package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfIterator;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public class WeightFactorySelectionSorterTest {

    @Test
    public void sortAscending() {
        SelectionSorterWeightFactory<TestdataSolution, TestdataEntity> weightFactory = (solution, selection) -> Integer
                .valueOf(selection.getCode().charAt(0));
        WeightFactorySelectionSorter<TestdataSolution, TestdataEntity> selectionSorter = new WeightFactorySelectionSorter<>(
                weightFactory, SelectionSorterOrder.ASCENDING);
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        List<TestdataEntity> selectionList = new ArrayList<>();
        selectionList.add(new TestdataEntity("C"));
        selectionList.add(new TestdataEntity("A"));
        selectionList.add(new TestdataEntity("D"));
        selectionList.add(new TestdataEntity("B"));
        selectionSorter.sort(scoreDirector, selectionList);
        assertCodesOfIterator(selectionList.iterator(), "A", "B", "C", "D");
    }

    @Test
    public void sortDescending() {
        SelectionSorterWeightFactory<TestdataSolution, TestdataEntity> weightFactory = (solution, selection) -> Integer
                .valueOf(selection.getCode().charAt(0));
        WeightFactorySelectionSorter<TestdataSolution, TestdataEntity> selectionSorter = new WeightFactorySelectionSorter<>(
                weightFactory, SelectionSorterOrder.DESCENDING);
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        List<TestdataEntity> selectionList = new ArrayList<>();
        selectionList.add(new TestdataEntity("C"));
        selectionList.add(new TestdataEntity("A"));
        selectionList.add(new TestdataEntity("D"));
        selectionList.add(new TestdataEntity("B"));
        selectionSorter.sort(scoreDirector, selectionList);
        assertCodesOfIterator(selectionList.iterator(), "D", "C", "B", "A");
    }

}
