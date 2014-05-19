package org.drools.beliefs.bayes;

public class Marginalizer {

    public Marginalizer(BayesVariable[]  srcVars, double[] srcPotentials, BayesVariable var,
                        double[] varDistribution) {
        BayesVariable[]  trgVars = new BayesVariable[] { var };

        int[] trgVarPos = PotentialMultiplier.createSubsetVarPos(srcVars, trgVars);
        int trgVarNumberOfStates = PotentialMultiplier.createNumberOfStates(trgVars);
        int[] trgVarMultipliers = PotentialMultiplier.createIndexMultipliers(trgVars, trgVarNumberOfStates);

        BayesProjection p = new BayesProjection(srcVars, srcPotentials, trgVarPos, trgVarMultipliers, varDistribution);
        p.project();
    }
}
