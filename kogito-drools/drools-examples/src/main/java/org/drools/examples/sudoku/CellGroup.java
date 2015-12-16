/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.examples.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * Abstract base class for all kinds of groups of related cells.
 */
public abstract class CellGroup extends SetOfNine {

    public static final Set<Integer> ALL_NINE = new CopyOnWriteArraySet<Integer>();
    static {
        for (int i = 1; i <= 9; i++) ALL_NINE.add(i);
    }

    private List<Cell> cells = new ArrayList<Cell>();
    
    /**
     * Constructor.
     */
    protected CellGroup() {
        super();
    }

    /**
     * Add another Cell object to the cells of this group.
     * @param cell a Cell object.
     */
    public void addCell(Cell cell) {
        cells.add(cell);
    }

    /**
     * Returns the Cell objects in this group.
     * @return a List of Cell objects.
     */
    public List<Cell> getCells() {
        return cells;
    }
}
