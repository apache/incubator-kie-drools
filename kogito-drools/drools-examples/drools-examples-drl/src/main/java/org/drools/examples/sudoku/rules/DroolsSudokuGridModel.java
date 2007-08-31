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
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.event.ObjectInsertedEvent;
import org.drools.event.ObjectRetractedEvent;
import org.drools.event.ObjectUpdatedEvent;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.examples.sudoku.swing.AbstractSudokuGridModel;
import org.drools.examples.sudoku.swing.SudokuGridEvent;
import org.drools.examples.sudoku.swing.SudokuGridModel;

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
public class DroolsSudokuGridModel
   extends AbstractSudokuGridModel
   implements SudokuGridModel
{
   /**
    * The location of the DRL file which defines the rule base for solving a Sudoku grid
    */
   public final static String SUDOKU_SOLVER_DRL = "../sudokuSolver.drl";
   
   /**
    * The location of the DRL file which defines the rule base for validating the content of a Sudoku grid
    */
   public final static String SUDOKU_VALIDATOR_DRL = "../sudokuValidator.drl";
   
   /** A set of AbtractCellValues capturing the current state of the grid */
   private Set<AbstractCellValue> allCellValues = new HashSet<AbstractCellValue>();

   /** The solver rule base */
   private RuleBase solverRuleBase;
   
   /** The stateful session working memory for the solver rule base */
   private StatefulSession solverStatefulSession;

   /** An inner class implementation listening to working memory events */
   private SudokuWorkingMemoryListener workingMemoryListener = new SudokuWorkingMemoryListener();
   
   /**
    * Create a new DroolsSudokuGridModel with an empty grid.
    */
   public DroolsSudokuGridModel()
   {
   
   }

   /**
    * Create a new DroolsSudokuGridModel with the specified values.
    * 
    * @param cellValues a two dimensional grid of Integer values for cells, a null means the value is not yet resolved
    */
   public DroolsSudokuGridModel(Integer[][] cellValues)
   {  
      this();
      setCellValues(cellValues);
   }
   
   public void setCellValues(Integer[][] cellValues)
   {
      if (solverRuleBase == null)
      {
         try
         {
            solverRuleBase = DroolsUtil.getInstance().readRuleBase(SUDOKU_SOLVER_DRL);
         }
         catch(Exception ex)
         {
            ex.printStackTrace();
            throw new RuntimeException("Error Reading RuleBase for Solver");
         }         
      }
      
      if (solverStatefulSession != null)
      {
         solverStatefulSession.removeEventListener(workingMemoryListener);
      }
      
      solverStatefulSession = solverRuleBase.newStatefulSession();
      solverStatefulSession.addEventListener(workingMemoryListener);
      
      for(int row=0; row<cellValues.length; row++)
      {
         for (int col=0; col<cellValues[row].length; col++)
         {
            if(cellValues[row][col] == null)
            {
               for(int value=1; value<10; value++)
               {
                  PossibleCellValue cellValue = new PossibleCellValue(value, row, col);
                  allCellValues.add(cellValue);
               }
            }
            else
            {
               ResolvedCellValue cellValue = new ResolvedCellValue(cellValues[row][col], row, col);
               allCellValues.add(cellValue);
            }
         }
      }
      
      insertAllCellValues(solverStatefulSession);
   }
   
   public boolean isCellEditable(int row, int col)
   {
      return false;
   }
   
   public boolean isCellResolved(int row, int col)
   {
      return getPossibleCellValues(row, col).size() == 1;
   }
   
   public boolean isGridSolved()
   {
      boolean solved = true;
      
      for(int row=0; row<NUM_ROWS; row++)
      {
         for (int col=0; col<NUM_COLS; col++)
         {
            if(!isCellResolved(row, col))
            {
               System.out.println("("+row+","+col+") has not been resolved");
               solved=false;
            }
         }
      }
      
      if (solved)
      {
         try
         {
            RuleBase validatorRuleBase = DroolsUtil.getInstance().readRuleBase(SUDOKU_VALIDATOR_DRL);
            
            StatefulSession validatorStatefulSession = validatorRuleBase.newStatefulSession();
            List issues = new ArrayList();
            validatorStatefulSession.setGlobal("issues", issues);
            insertAllCellValues(validatorStatefulSession);
            validatorStatefulSession.fireAllRules();
            
            if (issues.isEmpty())
            {
               System.out.println("Sucessfully Validated Solution");
            }
            else
            {
               solved = false;
               for (Object issue : issues)
               {
                  System.out.println(issue);
               }
            }
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
            throw new RuntimeException();
         }
      }
      
      return solved;
   }
   
   public List<Integer> getPossibleCellValues(int row, int col)
   {
      List<Integer> possibleCellValues = new ArrayList<Integer>();

      for (AbstractCellValue cellValue : allCellValues)
      {
         if (cellValue.getRow() == row && cellValue.getCol() == col)
         {
            possibleCellValues.add(cellValue.getValue());
         }
      }
      
      return possibleCellValues;
   }
   
   public boolean solve()
   {
      solverStatefulSession.fireAllRules();
      
      return isGridSolved();
   }
   
   private void insertAllCellValues(StatefulSession statefulSession)
   {
      for (AbstractCellValue cellValue : allCellValues)
      {
         statefulSession.insert(cellValue);
      }
   }

   class SudokuWorkingMemoryListener
      implements WorkingMemoryEventListener
   {
      public void objectInserted(ObjectInsertedEvent ev)
      {
         if (ev.getObject() instanceof AbstractCellValue)
         {
            allCellValues.add(((AbstractCellValue) ev.getObject()));
         }
         
         if (ev.getObject() instanceof ResolvedCellValue)
         {
            ResolvedCellValue cellValue = (ResolvedCellValue) ev.getObject();
            fireCellResolvedEvent(new SudokuGridEvent(this, cellValue.getRow(), cellValue.getCol(), cellValue.getValue()));
         }
      }
   
      public void objectRetracted(ObjectRetractedEvent ev)
      {
         if (ev.getOldObject() instanceof AbstractCellValue)
         {
            allCellValues.remove(((AbstractCellValue) ev.getOldObject()));
         }     
      }
   
      public void objectUpdated(ObjectUpdatedEvent ev)
      {
         if (ev.getObject() instanceof ResolvedCellValue)
         {
            ResolvedCellValue cellValue = (ResolvedCellValue) ev.getObject();
            fireCellUpdatedEvent(new SudokuGridEvent(this, cellValue.getRow(), cellValue.getCol(), cellValue.getValue()));
   
         }     
      }
   }
}
