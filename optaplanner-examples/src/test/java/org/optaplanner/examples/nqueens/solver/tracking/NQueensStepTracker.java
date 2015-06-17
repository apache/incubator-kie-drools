/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.examples.nqueens.solver.tracking;


import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.testdata.util.listeners.StepTestListener;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;

import java.util.ArrayList;
import java.util.List;

public class NQueensStepTracker extends StepTestListener {

    private List<Integer> filledColumns = new ArrayList<Integer>();
    private List<NQueensStepTracking> trackingList = new ArrayList<NQueensStepTracking>();

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        NQueens queens = (NQueens) stepScope.getWorkingSolution();

        for (Queen queen : queens.getQueenList()) {
            if (queen.getRow() != null && !filledColumns.contains(queen.getColumn().getIndex())) {
                filledColumns.add(queen.getColumn().getIndex());
                trackingList.add(new NQueensStepTracking(queen.getColumnIndex(), queen.getRowIndex()));
            }
        }
    }

    public List<NQueensStepTracking> getTrackingList() {
        return trackingList;
    }

}
