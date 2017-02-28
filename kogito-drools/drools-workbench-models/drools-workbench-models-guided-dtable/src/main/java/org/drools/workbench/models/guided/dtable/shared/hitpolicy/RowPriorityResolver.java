/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.hitpolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Resolves priorities for Resolved Hit Policy.
 * Helper classes Salience, RowNumber and Over are used since
 * map.get(int) is different from map.get(object).
 */
public class RowPriorityResolver {

    private List<RowNumber> rowOrder = new ArrayList<>();

    private Map<RowNumber, Over> overs = new TreeMap<>();

    public void set(final int rowNumber,
                    final int priorityOver) {

        final RowNumber rowNumberObject = new RowNumber(rowNumber);
        rowOrder.add(rowNumberObject);

        if (rowNumber < priorityOver) {
            throw new IllegalArgumentException("Priority over lower priority rows is not supported.");
        }

        if (priorityOver != 0) {
            overs.put(rowNumberObject,
                      new Over(priorityOver));
        }
    }

    public RowPriorities getPriorityRelations() {

        sortRowsByNumber();

        moveRowsBasedOnPriority();

        RowPriorities rowPriorities = new RowPriorities();

        // Set salience from top to bottom
        Collections.reverse(rowOrder);

        int salience = 0;
        for (RowNumber rowNumber : rowOrder) {
            rowPriorities.put(rowNumber,
                              new Salience(salience++));
        }

        return rowPriorities;
    }

    /**
     * Move rows on top of the row it has priority over.
     */
    private void moveRowsBasedOnPriority() {
        for (RowNumber myNumber : overs.keySet()) {
            Over over = overs.get(myNumber);

            int newIndex = rowOrder.indexOf(new RowNumber(over.getOver()));

            rowOrder.remove(myNumber);
            rowOrder.add(newIndex,
                         myNumber);
        }
    }

    private void sortRowsByNumber() {
        final Comparator<RowNumber> comparator = (me, other) -> me.getRowNumber().compareTo(other.getRowNumber());

        Collections.sort(rowOrder,
                         comparator);
    }
}
