/*
 * Copyright 2010 JBoss Inc
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

import org.drools.KnowledgeBase;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.examples.sudoku.swing.AbstractSudokuGridModel;
import org.drools.examples.sudoku.swing.SudokuGridEvent;
import org.drools.examples.sudoku.swing.SudokuGridModel;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * An object of this class solves Sudoku problems.
 */
public class Sudoku extends AbstractSudokuGridModel implements SudokuGridModel {

    private Cell[][]    cells;
    private CellSqr[][] sqrs = new CellSqr[][]{ new CellSqr[3], new CellSqr[3], new CellSqr[3] };
    private CellRow[]   rows   = new CellRow[9];
    private CellCol[]   cols   = new CellCol[9];
    
    private KnowledgeBase kBase;
    private StatefulKnowledgeSession session;
    private SudokuWorkingMemoryListener workingMemoryListener = new SudokuWorkingMemoryListener();
    private Counter counter;
    
    /**
     * Constructor.
     * @param kBase a Knowledge Base with rules for solving Sudoku problems.
     */
    public Sudoku( KnowledgeBase kBase ){
        this.kBase = kBase;
    }
    
    /*
     * (non-Javadoc)
     * @see org.drools.examples.sudoku.swing.SudokuGridModel#getCellValue(int, int)
     */
    public String getCellValue( int iRow, int iCol ){
        if( cells == null ) return " ";
        return cells[iRow][iCol].valueAsString();
    }
    
    /*
     * (non-Javadoc)
     * @see org.drools.examples.sudoku.swing.SudokuGridModel#solve()
     */
    public void solve(){
        if( this.isSolved() ) return;
        this.session.fireAllRules();
    }
    
    /*
     * (non-Javadoc)
     * @see org.drools.examples.sudoku.swing.SudokuGridModel#step()
     */
    public void step(){
        if( this.isSolved() ) return;
        this.counter.setCount( 1 );
        session.update( session.getFactHandle( this.counter ), this.counter );
        this.session.fireUntilHalt();
    }

    private boolean isSolved(){
        for( int iRow = 0; iRow < 9; iRow++ ){
            for( int iCol = 0; iCol < 9; iCol++ ){
                if( cells[iRow][iCol].getValue() == null ) return false;
            }
        }
        return true;
    }
    
    private void create(){
        for( int i = 0; i < 9; i++ ){
            session.insert( Integer.valueOf( i+1 ) );
            rows[i] = new CellRow( i );
            cols[i] = new CellCol( i );
        }
        
        cells = new Cell[9][];
        for( int iRow = 0; iRow < 9; iRow++ ){
            cells[iRow] = new Cell[9];
            for( int iCol = 0; iCol < 9; iCol++ ){
                Cell cell = cells[iRow][iCol] = new Cell();
                rows[iRow].addCell( cell );
                cols[iCol].addCell( cell );
            }
        }

        for( int i = 0; i < 3; i++ ){
            for( int j = 0; j < 3; j++ ){
                sqrs[i][j] = new CellSqr( rows[i*3], rows[i*3+1], rows[i*3+2], cols[j*3], cols[j*3+1], cols[j*3+2] );
            }
        }

        for( int iRow = 0; iRow < 9; iRow++ ){
            for( int iCol = 0; iCol < 9; iCol++ ){
                cells[iRow][iCol].makeReferences( rows[iRow], cols[iCol], sqrs[iRow/3][iCol/3] );
                session.insert( cells[iRow][iCol] );
            }
            session.insert( rows[iRow] );
            session.insert( cols[iRow] );
            session.insert( sqrs[iRow/3][iRow%3] );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.drools.examples.sudoku.swing.SudokuGridModel#setCellValues(java.lang.Integer[][])
     */
    public void setCellValues( Integer[][] cellValues ){
        if( session != null ){
            session.removeEventListener( workingMemoryListener );
            session.dispose();
        }
        
        System.out.println( "setCellValues" );
        
        this.session = kBase.newStatefulKnowledgeSession();
        session.addEventListener( workingMemoryListener );
        this.create();

        int initial = 0;
        for( int iRow = 0; iRow < 9; iRow++ ){
            for( int iCol = 0; iCol < 9; iCol++ ){
                Integer value = cellValues[iRow][iCol];
                if( value != null ){
                    session.insert( new Setting( iRow, iCol, value ) );
                    initial++;
                }
            }
        }
        this.counter = new Counter( initial );
        this.session.insert( counter );
        this.session.fireUntilHalt();
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append( "Sudoku:" ).append( '\n' );
        for( int iRow = 0; iRow < 9;  iRow++ ){
            sb.append( "  " ).append( rows[iRow].toString() ).append( '\n' );
        }
        
        return sb.toString();
    }
    
    class SudokuWorkingMemoryListener implements WorkingMemoryEventListener {

        public void objectInserted( ObjectInsertedEvent ev ) {
            if( ev.getObject() instanceof Counter ){
                fireRestartEvent(null);
            }
        }

        public void objectRetracted( ObjectRetractedEvent ev ) {
        }

        public void objectUpdated( ObjectUpdatedEvent ev ) {
            if( ev.getObject() instanceof Cell ) {
                Cell cell = (Cell) ev.getObject();
                if( cell.getValue() != null ){
                    fireCellUpdatedEvent( new SudokuGridEvent( this,
                            cell.getRowNo(),
                            cell.getColNo(),
                            cell.getValue() ) );
                }
            }
        }
    }
}
