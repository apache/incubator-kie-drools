/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.examples.nqueens.domain.NQueens;

public class NQueensSolutionInitializer {

    public static NQueens initialize(NQueens solution) {
        for (int i = 0; i < solution.getQueenList().size(); i++) {
            solution.getQueenList().get(i).setRow(solution.getRowList().get(0));
        }
        return solution;
    }

}
