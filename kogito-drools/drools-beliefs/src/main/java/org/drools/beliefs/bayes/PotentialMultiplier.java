/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.beliefs.bayes;

import java.util.Arrays;
import java.util.List;

public class PotentialMultiplier {
    //    BayesVariable   var;
    int             varPos;
    int[]           parentVarPos;
    double[][]      varPotential;
    int[]           parentIndexMultipliers;
    int             varProbabilityTableRow;
    BayesVariable[] vars;
    int[]           multipliers;
    int[]           path;

    double[] trgPotentials;
    int      trgPotentialIndex;


    public PotentialMultiplier(double[][] varPotential, int varPos, int[] parentVarPos, int[] parentIndexMultipliers,
                               BayesVariable[] vars, int[] multipliers, double[] trgPotentials) {
        this.varPotential = varPotential;
        this.varPos = varPos;
        this.parentVarPos = parentVarPos;
        this.parentIndexMultipliers = parentIndexMultipliers;
        this.vars = vars;
        this.multipliers = multipliers;
        this.path = new int[vars.length];;
        this.trgPotentials = trgPotentials;
    }

    public static int createNumberOfStates(List<BayesVariable> vars) {
        int numberOfStates = 1;
        for (int i = 0; i < vars.size(); i++) {
            BayesVariable var = vars.get(i);
            numberOfStates *= var.getOutcomes().length;
        }
        return numberOfStates;
    }

    public static int createNumberOfStates(BayesVariable[] vars) {
        return createNumberOfStates(Arrays.asList(vars));
    }

    public static int[] createIndexMultipliers(BayesVariable[] vars, int numberOfStates) {
        if ( vars.length == 0 ) {
            // length only == 0 during unit testing
            return new int[0];
        }

        int[] indexMultipliers = new int[vars.length];
        indexMultipliers[0] = numberOfStates / vars[0].getOutcomes().length;
        for (int i = 1; i < vars.length; i++) {
            indexMultipliers[i] = indexMultipliers[i - 1] / vars[i].getOutcomes().length;
        }
        return indexMultipliers;
    }

    public static int[] createSubsetVarPos(BayesVariable[] vars, BayesVariable[] subset) {
        int[] parentVarPos = new int[subset.length];
        int currentVar = 0;
        for ( int i = 0; i < vars.length && currentVar < subset.length; i++ ) {
            if ( vars[i] == subset[currentVar] ) {
                parentVarPos[currentVar++] = i;
            }
        }
        return parentVarPos;
    }

    public void multiple() {
        varProbabilityTableRow = 0;
        trgPotentialIndex = 0;
        multiple(0, 0);
    }

    public void multiple(int currentVar, int parentKeyPos) {
        // This performs a depth first recursion of the clique's variables.
        // It uses the intrinsic ordering between data structures.
        // The iteration maps the var ptable value(double[][]) to the clique's potential value(double[])
        // The current var state is tracked in a path, and the parent's too (if parents exist).
        // The ptable row is mapped using the parentIndexMultiplier, which is updated each time a parent is entered exited

        int numberOfOutcomes = vars[currentVar].getOutcomes().length;

        boolean isParent = false;
        int nextParentKeyPos = parentKeyPos;
        if (parentVarPos.length > 0 && parentKeyPos < parentVarPos.length &&  parentVarPos[parentKeyPos] == currentVar) {
            nextParentKeyPos++;
            isParent = true;
        }

        for (int j = 0; j < numberOfOutcomes; j++) {
            path[currentVar] = j;

            if (currentVar < vars.length - 1) {
                multiple(currentVar + 1, nextParentKeyPos);
            } else {
                trgPotentials[trgPotentialIndex++] *= varPotential[varProbabilityTableRow][path[varPos]];
            }
            if ( isParent ) {
                varProbabilityTableRow += parentIndexMultipliers[parentKeyPos];
            }
        }
        if ( isParent ) {
            varProbabilityTableRow -= (parentIndexMultipliers[parentKeyPos] * numberOfOutcomes );
        }
    }

    public static int[] indexToKey(int index, int[] indexMultipliers) {
        int[] stateIndex = new int[indexMultipliers.length + 1];

        int offset = 0;
        for (int i = 0; i < indexMultipliers.length; i++) {
            int multiplier = indexMultipliers[i];
            stateIndex[i] = Math.abs((index - offset) / multiplier);
            offset += multiplier * stateIndex[i];
        }
        stateIndex[indexMultipliers.length] = index - offset;

        return stateIndex;
    }

    public static int keyToIndex(int[] key, int[] indexMultipliers) {
        int index = 0;
        for (int i = 0; i < indexMultipliers.length; i++) {
            int value = key[i];
            index += value * indexMultipliers[i];
        }
        index += key[key.length - 1];
        return index;
    }
}
