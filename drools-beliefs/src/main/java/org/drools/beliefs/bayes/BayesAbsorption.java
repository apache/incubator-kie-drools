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

public class BayesAbsorption {
    int[]           srcVarPos;
    int[]           srcVarPosMultipliers;
    double[]        srcPotentials;
    double[]        oldSrcPotentials;
    int             srcPotentialIndex;

    BayesVariable[] trgVars;
    double[]        trgPotentials;
    int             trgPotentialIndex;

    int[]           path;


    public BayesAbsorption(int[] srcVarPos, double[] oldSrcPotentials, double[] srcPotentials, int[] srcVarPosMultipliers, BayesVariable[] trgVars, double[] trgPotentials) {
        this.srcVarPos = srcVarPos;
        this.srcVarPosMultipliers = srcVarPosMultipliers;
        this.srcPotentials = srcPotentials;
        this.oldSrcPotentials = oldSrcPotentials;

        this.trgVars = trgVars;
        this.trgPotentials = trgPotentials;
        this.path = new int[trgVars.length];
    }

    public void absorb() {
        srcPotentials = dividePotentials(srcPotentials, oldSrcPotentials);

        trgPotentialIndex = 0;
        srcPotentialIndex = 0;
        recurse(0, 0);
        normalize(trgPotentials);
    }

    public static void normalize(double[] array) {
        double sum = 0.0;
        for ( int i = 0, length = array.length; i < length; i++ ) {
            sum += array[i];
        }

        for ( int i = 0, length = array.length; i < length; i++ ) {
            array[i] = array[i]/sum;
        }
    }

    public static double[] dividePotentials(double[] potentials, double[] oldPotentials) {
        double[] absorbedPotentials = Arrays.copyOf( potentials, potentials.length);
        for ( int i = 0; i < potentials.length; i++ ) {
            double oldP = oldPotentials[i];
            if (oldP == 0.0) {
                absorbedPotentials[i] = 0.0;
            //} else if (Math.abs(Yvalue) < 1e-20) { // is this necessary?
            } else {
                absorbedPotentials[i] /= oldP;
            }
        }
        return absorbedPotentials;
    }


    public void recurse(int currentVar, int targetVarPos) {
        int numberOfOutcomes = trgVars[currentVar].getOutcomes().length;

        boolean isSepVar = false;
        int nextParentKeyPos = targetVarPos;
        if (this.srcVarPos.length > 0 && targetVarPos < this.srcVarPos.length &&  this.srcVarPos[targetVarPos] == currentVar) {
            nextParentKeyPos++;
            isSepVar = true;
        }

        for (int j = 0; j < numberOfOutcomes; j++) {
            path[currentVar] = j;

            if (currentVar < trgVars.length - 1) {
                recurse(currentVar + 1, nextParentKeyPos);
            } else {
                //sum += srcPotentials[srcPotentialIndex];
                trgPotentials[trgPotentialIndex++] *= srcPotentials[srcPotentialIndex];
            }
            if ( isSepVar ) {
                srcPotentialIndex += srcVarPosMultipliers[targetVarPos];
            }
        }
        if ( isSepVar ) {
            srcPotentialIndex -= (srcVarPosMultipliers[targetVarPos] * numberOfOutcomes );
        }
    }
}
