package org.drools.beliefs.bayes;

import java.util.Arrays;

public class BayesProjection {
    int[]    trgVarPos;
    int[]    trgVarPosMultipliers;
    double[] trgPotentials;
    int      trgPotentialIndex;


    BayesVariable[] srcVars;
    double[]        srcPotentials;
    int             srcPotentialIndex;

    int[]           path;

    public BayesProjection(BayesVariable[] srcVars, double[] srcPotentials, int[] trgVarPos, int[] trgVarPosMultipliers, double[] trgPotentials) {
        this.srcVars = srcVars;
        this.srcPotentials = srcPotentials;

        this.trgVarPos = trgVarPos;
        this.trgVarPosMultipliers = trgVarPosMultipliers;
        Arrays.fill(trgPotentials, 0); // reset the contents, as it will be re-assigned as part of the recurse
        this.trgPotentials = trgPotentials;



        this.path = new int[srcVars.length];
    }

    public void project() {
        srcPotentialIndex = 0;
        trgPotentialIndex = 0;
        recurse(0, 0);
        BayesAbsorption.normalize(trgPotentials);

    }

    public static void normalize(double[] darray, double sum) {
        for ( int i = 0; i < darray.length; i++ ) {
            darray[i] /= sum;
        }
    }

    public void recurse(int currentVar, int targetVarPos) {
        int numberOfOutcomes = srcVars[currentVar].getOutcomes().length;

        boolean isTrgVar = false;
        int nextParentKeyPos = targetVarPos;
        if (this.trgVarPos.length > 0 && targetVarPos < this.trgVarPos.length &&  this.trgVarPos[targetVarPos] == currentVar) {
            nextParentKeyPos++;
            isTrgVar = true;
        }

        for (int j = 0; j < numberOfOutcomes; j++) {
            path[currentVar] = j;

            if (currentVar < srcVars.length - 1) {
                recurse(currentVar + 1, nextParentKeyPos);
            } else {
                //sum += srcPotentials[srcPotentialIndex];
                trgPotentials[trgPotentialIndex] += srcPotentials[srcPotentialIndex++];
            }
            if ( isTrgVar ) {
                trgPotentialIndex += trgVarPosMultipliers[targetVarPos];
            }
        }
        if ( isTrgVar ) {
            trgPotentialIndex -= (trgVarPosMultipliers[targetVarPos] * numberOfOutcomes );
        }
    }
}
