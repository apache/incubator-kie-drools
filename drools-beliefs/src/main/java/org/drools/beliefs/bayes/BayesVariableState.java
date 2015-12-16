/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

public class BayesVariableState {
    private BayesVariable variable;
    private double[]      distribution;
    private Object[]      outcomes;

    public BayesVariableState(BayesVariable variable, double[] distribution) {
        this.variable = variable;
        this.distribution = distribution;
    }

    public BayesVariable getVariable() {
        return variable;
    }

    public double[] getDistribution() {
        return distribution;
    }

    public void setDistribution(double[] distribution) {
        this.distribution = distribution;
    }

    public Object[] getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(Object[] outcomes) {
        this.outcomes = outcomes;
    }
}
