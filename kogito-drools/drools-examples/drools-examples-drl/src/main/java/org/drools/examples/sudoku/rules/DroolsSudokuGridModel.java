/**
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

/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.drools.examples.sudoku.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

/**
 * An implementation of the SudokuGridModel interface which is backed by the Drools engine.
 * <p>
 * Two rule bases are used in this implementation. The first, defined at SUDOKU_SOLVER_DRL 
 * provides a set of rules that attempt to solve a partially completed Sudoku grid. The
 * second, defined at SUDOKU_VALIDATOR_DRL provides a set of rules which can validate 
 * whether the current state of the model represents a valid solution.
 * <p>
 * 
 * @author <a href="pbennett@redhat.com">Pete Bennett</a>
 * @version $Revision: 1.1 $
 */
@SuppressWarnings("unchecked")
public class DroolsSudokuGridModel extends AbstractSudokuGridModel
    implements
    SudokuGridModel {
    /**
     * The location of the DRL file which defines the rule base for solving a Sudoku grid
     */
    public final static String          SUDOKU_SOLVER_DRL     = "../sudokuSolver.drl";

    /**
     * The location of the DRL file which defines the rule base for validating the content of a Sudoku grid
     */
    public final static String          SUDOKU_VALIDATOR_DRL  = "../sudokuValidator.drl";

    /** A set of AbtractCellValues capturing the current state of the grid */
    private Set<AbstractCellValue>      allCellValues         = new HashSet<AbstractCellValue>();

    /** A index into the AbstractCellValues based on row and column */
    private Set<Integer>[][]            cellValuesByRowAndCol = new HashSet[SudokuGridModel.NUM_ROWS][SudokuGridModel.NUM_COLS];

    /** The solver rule base */
    private KnowledgeBase               solverRuleBase;

    /** The stateful session working memory for the solver rule base */
    private StatefulKnowledgeSession    solverStatefulSession;

    /** An inner class implementation listening to working memory events */
    private SudokuWorkingMemoryListener workingMemoryListener = new SudokuWorkingMemoryListener();

    /**
     * Create a new DroolsSudokuGridModel with an empty grid.
     */
    public DroolsSudokuGridModel() {

    }

    /**
     * Create a new DroolsSudokuGridModel with the specified values.
     * 
     * @param cellValues a two dimensional grid of Integer values for cells, a null means the value is not yet resolved
     */
    public DroolsSudokuGridModel(Integer[][] cellValues) {
        this();
        setCellValues( cellValues );
    }

    /**
     * Set the state of the Grid based on a two dimensional array of Integers.
     * 
     * @param cellValues a two dimensional grid of Integer values for cells, a null means the value is not yet resolved
     */
    public void setCellValues(Integer[][] cellValues) {
        long startTime = System.currentTimeMillis();
        if ( solverRuleBase == null ) {
            try {
                solverRuleBase = DroolsUtil.getInstance().readRuleBase( SUDOKU_SOLVER_DRL );
            } catch ( Exception ex ) {
                ex.printStackTrace();
                throw new RuntimeException( "Error Reading RuleBase for Solver" );
            }
        }

        if ( solverStatefulSession != null ) {
            solverStatefulSession.removeEventListener( workingMemoryListener );
        }

        solverStatefulSession = solverRuleBase.newStatefulKnowledgeSession();
        solverStatefulSession.addEventListener( workingMemoryListener );

        for ( int row = 0; row < cellValues.length; row++ ) {
            for ( int col = 0; col < cellValues[row].length; col++ ) {
                cellValuesByRowAndCol[row][col] = new HashSet<Integer>();

                if ( cellValues[row][col] == null ) {
                    for ( int value = 1; value < 10; value++ ) {
                        PossibleCellValue cellValue = new PossibleCellValue( value,
                                                                             row,
                                                                             col );
                        addCellValue( cellValue );
                        allCellValues.add( cellValue );
                    }
                } else {
                    ResolvedCellValue cellValue = new ResolvedCellValue( cellValues[row][col],
                                                                         row,
                                                                         col );
                    addCellValue( cellValue );
                }
            }
        }

        insertAllCellValues( solverStatefulSession );
        System.out.println( "Setting up working memory and inserting all cell value POJOs took " + (System.currentTimeMillis() - startTime) + "ms." );
    }

    /**
     * Determines whether a given cell is editable from another class.
     * 
     * @param row the row in the grid for the cell
     * @param col the column in the grid for the cell
     * @return is the specified cell editable
     */
    public boolean isCellEditable(int row,
                                  int col) {
        return false;
    }

    /**
     * Determines whether a given cell has been solved.
     * 
     * @param row the row in the grid for the cell
     * @param col the column in the grid for the cell
     * @return is the specified cell solved
     */
    public boolean isCellResolved(int row,
                                  int col) {
        return getPossibleCellValues( row,
                                      col ).size() == 1;
    }

    /**
     * Evaluates the current state of the Grid against the 
     * validation rules determined in the SUDOKU_VALIDATOR_DRL
     * and indicates if the grid is currently solved or not.
     * 
     * @return true if the current state represents a completely filled out
     *              and valid Sudoku solution, false otherwise
     */
    public boolean isGridSolved() {
        boolean solved = true;

        // TODO: move this logic into SUDOKU_VALIDATOR_DRL and out of Java code
        for ( int row = 0; row < NUM_ROWS; row++ ) {
            for ( int col = 0; col < NUM_COLS; col++ ) {
                if ( !isCellResolved( row,
                                      col ) ) {
                    System.out.print( "(" + row + "," + col + ") has not been resolved but has been narrowed down to " );
                    for ( Integer possibleInt : getPossibleCellValues( row,
                                                                       col ) ) {
                        System.out.print( possibleInt + " " );
                    }
                    System.out.println();
                    solved = false;
                }
            }
        }

        if ( solved ) {
            try {
                KnowledgeBase kbase = DroolsUtil.getInstance().readRuleBase( SUDOKU_VALIDATOR_DRL );

                StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
                List issues = new ArrayList();
                ksession.setGlobal( "issues",
                                                    issues );
                insertAllCellValues( ksession );
                ksession.fireAllRules();

                if ( issues.isEmpty() ) {
                    System.out.println( "Sucessfully Validated Solution" );
                } else {
                    solved = false;
                    for ( Object issue : issues ) {
                        System.out.println( issue );
                    }
                }
            } catch ( Exception ex ) {
                ex.printStackTrace();
                throw new RuntimeException();
            }
        }

        return solved;
    }

    /**
     * Returns the possible values of the cell at a specific 
     * row and column in the Grid.
     * 
     * @param row the row in the Grid
     * @param col the column in the Grid
     * @return the Set of possible Integer values this cell can have, if 
     *         the Set is of size one then this is the value this cell 
     *         must have, otherwise it is a list of the possibilities
     */
    public Set<Integer> getPossibleCellValues(int row,
                                              int col) {
        return cellValuesByRowAndCol[row][col];
    }

    /**
     * Attempt to solve the Sudoku puzzle from its current state by 
     * firing all of the rules in SUDOKU_SOLVER_DRL against the 
     * current state of the Grid then validate if we have solved the 
     * Grid after this.
     * 
     * @return true if the state after firing all rules 
     *              represents a completely filled out
     *              and valid Sudoku solution, false otherwise 
     */
    public boolean solve() {
        solverStatefulSession.fireAllRules();

        return isGridSolved();
    }

    /**
     * Fire the next rule on the agenda and return
     * 
     * @return true if the state after firing the single rule 
     *              represents a completely filled out
     *              and valid Sudoku solution, false otherwise 
     */
    public boolean step() {
        // TODO: I am not sure where the fireAllRules(int) method has gone
        // should be solverStatefulSession.fireAllRules(1)
        solverStatefulSession.fireAllRules();

        return isGridSolved();
    }

    /**
     * Inserts all of the current state of the Grid as represented
     * by the set of AbstractCellValues this class is maintaining
     * into the specified StatefulSession working memory.
     * 
     * @param statefulSession the target StatefulSession
     */
    private void insertAllCellValues(StatefulKnowledgeSession statefulSession) {
        for ( AbstractCellValue cellValue : allCellValues ) {
            statefulSession.insert( cellValue );
        }
    }

    /**
     * Adds the specified AbstractCellValue into the set of 
     * AbstractCellValues that this class is maintaining.
     * 
     * @param cellValue the AbstractCellValue to add
     */
    private void addCellValue(AbstractCellValue cellValue) {
        allCellValues.add( cellValue );
        cellValuesByRowAndCol[cellValue.getRow()][cellValue.getCol()].add( cellValue.getValue() );
    }

    /**
     * Removes the specified AbstractCellValue from the set of 
     * AbstractCellValues that this class is maintaining.
     * 
     * @param cellValue the AbstractCellValue to remove
     */
    private void removeCellValue(AbstractCellValue cellValue) {
        allCellValues.remove( cellValue );
        cellValuesByRowAndCol[cellValue.getRow()][cellValue.getCol()].remove( cellValue.getValue() );
    }

    class SudokuWorkingMemoryListener
        implements
        WorkingMemoryEventListener {

        public void objectInserted(ObjectInsertedEvent ev) {
            if ( ev.getObject() instanceof AbstractCellValue ) {
                addCellValue( ((AbstractCellValue) ev.getObject()) );
            }

            if ( ev.getObject() instanceof ResolvedCellValue ) {
                ResolvedCellValue cellValue = (ResolvedCellValue) ev.getObject();
                fireCellResolvedEvent( new SudokuGridEvent( this,
                                                            cellValue.getRow(),
                                                            cellValue.getCol(),
                                                            cellValue.getValue() ) );
            }

            if ( ev.getObject() instanceof String ) {
                System.out.println( ev.getObject() );
            }
        }

        public void objectRetracted(ObjectRetractedEvent ev) {
            if ( ev.getOldObject() instanceof AbstractCellValue ) {
                AbstractCellValue cellValue = (AbstractCellValue) ev.getOldObject();

                removeCellValue( cellValue );
                fireCellUpdatedEvent( new SudokuGridEvent( this,
                                                           cellValue.getRow(),
                                                           cellValue.getCol(),
                                                           cellValue.getValue() ) );
            }
        }

        public void objectUpdated(ObjectUpdatedEvent ev) {
            if ( ev.getObject() instanceof ResolvedCellValue ) {
                ResolvedCellValue cellValue = (ResolvedCellValue) ev.getObject();
                fireCellUpdatedEvent( new SudokuGridEvent( this,
                                                           cellValue.getRow(),
                                                           cellValue.getCol(),
                                                           cellValue.getValue() ) );
            }
        }
    }
}
