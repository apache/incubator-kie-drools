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
package org.drools.examples.sudoku.swing;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SudokuGridSamples {
   private Map<String, Integer[][]> samples = new LinkedHashMap<String, Integer[][]>();
   private static SudokuGridSamples INSTANCE;
   
   private SudokuGridSamples() {
      samples.put
      (
        "Simple",
        new Integer[][] 
       {{null, 5, 6, 8, null, 1, 9, 4, null},
        {9, null, null, 6, null, 5, null, null, 3},
        {7, null, null, 4, 9, 3, null, null, 8},
        {8, 9, 7, null, 4, null, 6, 3, 5},
        {null, null, 3, 9, null, 6, 8, null, null},
        {4, 6, 5, null, 8, null, 2, 9, 1},
        {5, null, null, 2, 6, 9, null, null, 7},
        {6, null, null, 5, null, 4, null, null, 9},
        {null, 4, 9, 7, null, 8, 3, 5, null}}
      );
      
      samples.put
      (
        "Medium",
        new Integer[][] 
       {{8, 4, 7, null, null, null, 2, 5, 6},
               {5, null, null, null, 8, null, null, null, 4},
               {2, null, null, null, 7, null, null, null, 8},
               {null, null, null, 3, null, 8, null, null, null},
               {null, 5, 1, null, null, null, 8, 7, 2},
               {null, null, null, 5, null, 7, null, null, null},
               {4, null, null, null, 5, null, null, null, 7},
               {6, null, null, null, 3, null, null, null, 9},
               {1, 3, 2, null, null, null, 4, 8, 5}}
      );
      
      samples.put
      (
        "Hard 1",
        new Integer[][] 
       {{null, null, null, null, 5, 1, null, 8, null},
               {null, 8, null, null, 4, null, null, null, 5},
               {null, null, 3, null, null, null, 2, null, null},
               {null, null, null, null, 6, null, null, null, 9},
               {6, 7, null, 4, null, 9, null, 1, 3},
               {8, null, null, null, 3, null, null, null, null},
               {null, null, 2, null, null, null, 4, null, null},
               {5, null, null, null, 9, null, null, 2, null},
               {null, 9, null, 7, 1, null, null, null, null}}
      );
      
      samples.put
      (
        "Hard 2",
        new Integer[][] 
       {{null,null,null,6,null,null,1,null,null},
           {null,null,null,null,null,5,null,null,6},
           {5,null,7,null,null,null,2,3,null},
           {null,8,null,9,null,7,null,null,null},
           {9,3,null,null,null,null,null,6,7},
           {null,null,null,4,null,6,null,1,null},
           {null,7,4,null,null,null,9,null,1},
           {8,null,null,7,null,null,null,null,null},
           {null,null,3,null,null,8,null,null,null}}
      );

      samples.put
      (
        "Hard 3",
        new Integer[][] 
       {{null,8,null,null,null,6,null,null,5},
               {2,null,null,null,null,null,4,8,null},
               {null,null,9,null,null,8,null,1,null},
               {null,null,null,null,8,null,1,null,2},
               {null,null,null,3,null,1,null,null,null},
               {6,null,1,null,9,null,null,null,null},
               {null,9,null,4,null,null,8,null,null},
               {null,7,6,null,null,null,null,null,3},
               {1,null,null,7,null,null,null,5,null}}
      );
      
      samples.put
      (
        "Hard 4",
        new Integer[][] 
       {{null,null,null,null,null,4,null,9,5},
               {6,7,null,5,null,null,null,1,null},
               {null,null,null,6,null,9,null,null,null},
               {null,2,null,null,null,null,4,null,null},
               {8,1,null,null,null,null,null,7,2},
               {null,null,7,null,null,null,null,8,null},
               {null,null,null,3,null,5,null,null,null},
               {null,6,null,null,null,1,null,5,8},
               {7,3,null,9,null,null,null,null,null}}
      );    
      
      samples.put
      (
        "!DELIBERATELY BROKEN!",
        new Integer[][] 
       {{5,null,null,null,null,4,null,9,5},
               {6,7,null,5,null,null,null,1,null},
               {null,null,null,6,null,9,null,null,null},
               {null,2,null,null,null,null,4,null,null},
               {8,1,null,null,null,null,null,7,2},
               {null,null,7,null,null,null,null,8,null},
               {8,null,null,3,null,5,null,null,null},
               {null,6,null,null,null,1,null,5,8},
               {7,3,null,9,null,null,null,null,null}}
      );   }
   
   public static SudokuGridSamples getInstance(){
      if (INSTANCE == null){
         INSTANCE = new SudokuGridSamples();
      }
      return INSTANCE;
   }
   
   public Set<String> getSampleNames(){
      return samples.keySet();
   }
   
   public Integer[][] getSample(String name){
      return samples.get(name);
   }
}
