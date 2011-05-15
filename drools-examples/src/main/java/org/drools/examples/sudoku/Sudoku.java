/*
 * Copyright 2011 JBoss Inc
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

import java.util.Formatter;
import java.util.HashSet;
import java.util.Set;

import org.drools.KnowledgeBase;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.examples.sudoku.swing.AbstractSudokuGridModel;
import org.drools.examples.sudoku.swing.SudokuGridEvent;
import org.drools.examples.sudoku.swing.SudokuGridModel;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

/**
 * An object of this class solves Sudoku problems.
 */
public class Sudoku extends AbstractSudokuGridModel implements SudokuGridModel {

    public static Sudoku sudoku;
    
    public  Cell[][]    cells;
    private CellSqr[][] sqrs = new CellSqr[][]{ new CellSqr[3], new CellSqr[3], new CellSqr[3] };
    private CellRow[]   rows = new CellRow[9];
    private CellCol[]   cols = new CellCol[9];
    
    private KnowledgeBase kBase;
    private StatefulKnowledgeSession session;
    private SudokuWorkingMemoryListener workingMemoryListener = new SudokuWorkingMemoryListener();
    private Counter counter;
    private Boolean explain = false;
    private FactHandle steppingFactHandle;
    private Stepping stepping;
    private boolean unsolvable = false;
    
    /**
     * Constructor.
     * @param kBase a Knowledge Base with rules for solving Sudoku problems.
     */
    public Sudoku(KnowledgeBase kBase) {
        this.kBase = kBase;
        sudoku = this;
    }
    
    /*
     * (non-Javadoc)
     * @see org.drools.examples.sudoku.swing.SudokuGridModel#getCellValue(int, int)
     */
    public String getCellValue(int iRow, int iCol) {
        if (cells == null) return " ";
        return cells[iRow][iCol].valueAsString();
    }
    
    /**
     * Nice printout of the grid.
     */
    public void dumpGrid() {
        Formatter fmt = new Formatter(System.out);
        fmt.format("       ");
        for (int iCol = 0; iCol < 9; iCol++) {
            fmt.format("Col: %d     ", iCol);
        }
        System.out.println();
        for (int iRow = 0; iRow < 9; iRow++) {
            System.out.print("Row " + iRow + ": ");
            for (int iCol = 0; iCol < 9; iCol++) {
                if (cells[iRow][iCol].getValue() != null) {
                    fmt.format(" --- %d --- ", cells[iRow][iCol].getValue());
                } else {
                    StringBuilder sb = new StringBuilder();
                    Set<Integer> perms = cells[iRow][iCol].getFree();
                    for (int i = 1; i <= 9; i++) {
                        if (perms.contains(i)) {
                            sb.append(i);
                        } else {
                            sb.append(' ');
                        }
                    }
                    fmt.format(" %-10s", sb.toString());
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Checks that everything is still according to the sudoku rules.
     */
    public void consistencyCheck() {
        for (int iRow = 0; iRow < 9; iRow++) {
            for (int iCol = 0; iCol < 9; iCol++) {
                Cell cell = cells[iRow][iCol];
                Integer value = cell.getValue();
                if (value != null) {
                    if (! cell.getFree().isEmpty()) {
                        throw new IllegalStateException("free not empty");
                    }
                    // any containing group
                    for (Cell other: cell.getExCells()) {
                        // must not occur in any of the other cells
                        if (value.equals(other.getValue())) {
                            throw new IllegalStateException("duplicate");
                        }
                        // must not occur in the permissibles of any of the other cells
                        if (other.getFree().contains(value)) {
                            throw new IllegalStateException("not eliminated");
                        }
                    }
                }
            }
        }
        
        for (int i = 0; i < rows.length; i++) {
            Set<Integer> aSet = new HashSet<Integer>();
            for (int j = 0; j < rows[i].getCells().size(); j++) {
                Cell cell = rows[i].getCells().get(j);
                Integer value = cell.getValue();
                if (value != null) {
                    aSet.add(value);
                } else {
                    aSet.addAll(cell.getFree());
                }
            }
            if (! aSet.equals(CellGroup.allNine)) {
                throw new IllegalStateException("deficit in row");
            }
        }
        
        for (int i = 0; i < cols.length; i++) {
            Set<Integer> aSet = new HashSet<Integer>();
            for (int j = 0; j < cols[i].getCells().size(); j++) {
                Cell cell = cols[i].getCells().get(j);
                Integer value = cell.getValue();
                if (value != null) {
                    aSet.add(value);
                } else {
                    aSet.addAll(cell.getFree());
                }
            }
            if (! aSet.equals(CellGroup.allNine)) {
                throw new IllegalStateException("deficit in column");
            }
        }

        for (int ir = 0; ir < sqrs.length; ir++) {
            for (int ic = 0; ic < sqrs[ir] .length; ic++) {
                Set<Integer> aSet = new HashSet<Integer>();
                for (int j = 0; j < sqrs[ir][ic].getCells().size(); j++) {
                    Cell cell = sqrs[ir][ic].getCells().get(j);
                    Integer value = cell.getValue();
                    if (value != null) {
                        aSet.add(value);
                    } else {
                        aSet.addAll(cell.getFree());
                    }
                }
                if (! aSet.equals(CellGroup.allNine)) {
                    throw new IllegalStateException("deficit in square");
                }
            }
        }
        System.out.println("+++ check OK +++");
    }
    
    /*
     * (non-Javadoc)
     * @see org.drools.examples.sudoku.swing.SudokuGridModel#solve()
     */
    public void solve() {
        if (this.isSolved()) return;
        explain = false;
        session.setGlobal("explain", explain);
        if( steppingFactHandle != null ){
            session.retract( steppingFactHandle );
            steppingFactHandle = null;
            stepping = null;
        }
        this.session.fireAllRules();
//        dumpGrid();
    }
    
    /*
     * (non-Javadoc)
     * @see org.drools.examples.sudoku.swing.SudokuGridModel#step()
     */
    public void step() {
        if (this.isSolved()) return;
        explain = true;
        session.setGlobal("explain", explain);
        this.counter.setCount(1);
        session.update(session.getFactHandle(this.counter), this.counter);
        if( steppingFactHandle == null ){
            steppingFactHandle = session.insert( stepping = new Stepping() );
        }
        this.session.fireUntilHalt();
        if( stepping.isEmergency() ){
            this.unsolvable = true;
        }
//        dumpGrid();
    }

    public boolean isSolved() {
        for (int iRow = 0; iRow < 9; iRow++) {
            for (int iCol = 0; iCol < 9; iCol++) {
                if (cells[iRow][iCol].getValue() == null) return false;
            }
        }
        return true;
    }
    
    public boolean isUnsolvable(){
        return unsolvable;
    }
    
    private void create() {
        for (int i = 0; i < 9; i++) {
            session.insert(Integer.valueOf(i+1));
            rows[i] = new CellRow(i);
            cols[i] = new CellCol(i);
        }
        
        cells = new Cell[9][];
        for (int iRow = 0; iRow < 9; iRow++) {
            cells[iRow] = new Cell[9];
            for (int iCol = 0; iCol < 9; iCol++) {
                Cell cell = cells[iRow][iCol] = new Cell();
                rows[iRow].addCell(cell);
                cols[iCol].addCell(cell);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sqrs[i][j] = new CellSqr(rows[i*3], rows[i*3+1], rows[i*3+2],
                                         cols[j*3], cols[j*3+1], cols[j*3+2]);
            }
        }

        for (int iRow = 0; iRow < 9; iRow++) {
            for (int iCol = 0; iCol < 9; iCol++) {
                cells[iRow][iCol].makeReferences(rows[iRow], cols[iCol], sqrs[iRow/3][iCol/3]);
                session.insert(cells[iRow][iCol]);
            }
            session.insert(rows[iRow]);
            session.insert(cols[iRow]);
            session.insert(sqrs[iRow/3][iRow%3]);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.drools.examples.sudoku.swing.SudokuGridModel#setCellValues(java.lang.Integer[][])
     */
    public void setCellValues(Integer[][] cellValues) {
        if (session != null) {
            session.removeEventListener(workingMemoryListener);
            session.dispose();
        }
        
        this.session = kBase.newStatefulKnowledgeSession();
        session.setGlobal("explain", explain);
        session.addEventListener(workingMemoryListener);

        Setting s000 = new Setting(0, 0, 0);
        FactHandle fh000 = this.session.insert(s000);
        this.create();

        int initial = 0;
        for (int iRow = 0; iRow < 9; iRow++) {
            for (int iCol = 0; iCol < 9; iCol++) {
                Integer value = cellValues[iRow][iCol];
                if (value != null) {
                    session.insert(new Setting(iRow, iCol, value));
                    initial++;
                }
            }
        }
        this.counter = new Counter(initial);
        this.session.insert(counter);
        this.session.retract(fh000);
        this.session.fireUntilHalt();
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Sudoku:").append('\n');
        for (int iRow = 0; iRow < 9;  iRow++) {
            sb.append("  ").append(rows[iRow].toString()).append('\n');
        }
        
        return sb.toString();
    }
    
    class SudokuWorkingMemoryListener implements WorkingMemoryEventListener {

        public void objectInserted(ObjectInsertedEvent ev) {
            if (ev.getObject() instanceof Counter) {
                fireRestartEvent(null);
            }
        }

        public void objectRetracted(ObjectRetractedEvent ev) {
        }

        public void objectUpdated(ObjectUpdatedEvent ev) {
            if (ev.getObject() instanceof Cell) {
                Cell cell = (Cell) ev.getObject();
                if (cell.getValue() != null) {
                    fireCellUpdatedEvent(new SudokuGridEvent(this,
                            cell.getRowNo(),
                            cell.getColNo(),
                            cell.getValue()));
                }
            }
        }
    }
    
    public void validate(){
        session.getAgenda().getAgendaGroup( "validate" ).setFocus();
        session.fireUntilHalt();
    }
}
