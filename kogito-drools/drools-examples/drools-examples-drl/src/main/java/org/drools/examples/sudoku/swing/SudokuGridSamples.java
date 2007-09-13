/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.drools.examples.sudoku.swing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SudokuGridSamples
{
   private Map samples = new HashMap();
   private static SudokuGridSamples INSTANCE;
   
   private SudokuGridSamples()
   {
      samples.put
      (
        "Simple",
        new int[][] 
       {{0, 5, 6, 8, 0, 1, 9, 4, 0},
        {9, 0, 0, 6, 0, 5, 0, 0, 3},
        {7, 0, 0, 4, 9, 3, 0, 0, 8},
        {8, 9, 7, 0, 4, 0, 6, 3, 5},
        {0, 0, 3, 9, 0, 6, 8, 0, 0},
        {4, 6, 5, 0, 8, 0, 2, 9, 1},
        {5, 0, 0, 2, 6, 9, 0, 0, 7},
        {6, 0, 0, 5, 0, 4, 0, 0, 9},
        {0, 4, 9, 7, 0, 8, 3, 5, 0}}
      );
      
      samples.put
      (
        "Medium",
        new int[][] 
       {{8, 4, 7, 0, 0, 0, 2, 5, 6},
               {5, 0, 0, 0, 8, 0, 0, 0, 4},
               {2, 0, 0, 0, 7, 0, 0, 0, 8},
               {0, 0, 0, 3, 0, 8, 0, 0, 0},
               {0, 5, 1, 0, 0, 0, 8, 7, 2},
               {0, 0, 0, 5, 0, 7, 0, 0, 0},
               {4, 0, 0, 0, 5, 0, 0, 0, 7},
               {6, 0, 0, 0, 3, 0, 0, 0, 9},
               {1, 3, 2, 0, 0, 0, 4, 8, 5}}
      );
      
      samples.put
      (
        "Hard 1",
        new int[][] 
       {{0, 0, 0, 0, 5, 1, 0, 8, 0},
               {0, 8, 0, 0, 4, 0, 0, 0, 5},
               {0, 0, 3, 0, 0, 0, 2, 0, 0},
               {0, 0, 0, 0, 6, 0, 0, 0, 9},
               {6, 7, 0, 4, 0, 9, 0, 1, 3},
               {8, 0, 0, 0, 3, 0, 0, 0, 0},
               {0, 0, 2, 0, 0, 0, 4, 0, 0},
               {5, 0, 0, 0, 9, 0, 0, 2, 0},
               {0, 9, 0, 7, 1, 0, 0, 0, 0}}
      );
      
      samples.put
      (
        "Hard 2",
        new int[][] 
       {{0,0,0,6,0,0,1,0,0},
           {0,0,0,0,0,5,0,0,6},
           {5,0,7,0,0,0,2,3,0},
           {0,8,0,9,0,7,0,0,0},
           {9,3,0,0,0,0,0,6,7},
           {0,0,0,4,0,6,0,1,0},
           {0,7,4,0,0,0,9,0,1},
           {8,0,0,7,0,0,0,0,0},
           {0,0,3,0,0,8,0,0,0}}
      );

      samples.put
      (
        "Hard 3",
        new int[][] 
       {{0,8,0,0,0,6,0,0,5},
               {2,0,0,0,0,0,4,8,0},
               {0,0,9,0,0,8,0,1,0},
               {0,0,0,0,8,0,1,0,2},
               {0,0,0,3,0,1,0,0,0},
               {6,0,1,0,9,0,0,0,0},
               {0,9,0,4,0,0,8,0,0},
               {0,7,6,0,0,0,0,0,3},
               {1,0,0,7,0,0,0,5,0}}
      );
      
      samples.put
      (
        "Hard 4",
        new int[][] 
       {{0,0,0,0,0,4,0,9,5},
               {6,7,0,5,0,0,0,1,0},
               {0,0,0,6,0,9,0,0,0},
               {0,2,0,0,0,0,4,0,0},
               {8,1,0,0,0,0,0,7,2},
               {0,0,7,0,0,0,0,8,0},
               {0,0,0,3,0,5,0,0,0},
               {0,6,0,0,0,1,0,5,8},
               {7,3,0,9,0,0,0,0,0}}
      );    
      
      samples.put
      (
        "!DELIBERATELY BROKEN!",
        new int[][] 
       {{5,0,0,0,0,4,0,9,5},
               {6,7,0,5,0,0,0,1,0},
               {0,0,0,6,0,9,0,0,0},
               {0,2,0,0,0,0,4,0,0},
               {8,1,0,0,0,0,0,7,2},
               {0,0,7,0,0,0,0,8,0},
               {8,0,0,3,0,5,0,0,0},
               {0,6,0,0,0,1,0,5,8},
               {7,3,0,9,0,0,0,0,0}}
      );   }
   
   public static SudokuGridSamples getInstance()
   {
      if (INSTANCE == null)
      {
         INSTANCE = new SudokuGridSamples();
      }
      
      return INSTANCE;
   }
   
   public Set getSampleNames()
   {
      return samples.keySet();
   }
   
   public int[][] getSample(String name)
   {
      return (int[][]) samples.get(name);
   }
}
