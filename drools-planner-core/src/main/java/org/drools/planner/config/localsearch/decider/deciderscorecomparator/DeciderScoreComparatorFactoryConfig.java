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

package org.drools.planner.config.localsearch.decider.deciderscorecomparator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.core.localsearch.decider.deciderscorecomparator.DeciderScoreComparatorFactory;
import org.drools.planner.core.localsearch.decider.deciderscorecomparator.NaturalDeciderScoreComparatorFactory;
import org.drools.planner.core.localsearch.decider.deciderscorecomparator.ShiftingHardPenaltyDeciderScoreComparatorFactory;

/**
 * @author Geoffrey De Smet
 */
@XStreamAlias("deciderScoreComparatorFactory")
public class DeciderScoreComparatorFactoryConfig {

    private Class<DeciderScoreComparatorFactory> deciderScoreComparatorClass = null;
    private DeciderScoreComparatorFactoryType deciderScoreComparatorFactoryType = null;

    private Integer hardScoreActivationThreshold = null;
    private Integer successiveNoHardChangeMinimum = null;
    private Integer successiveNoHardChangeMaximum = null;
    private Double successiveNoHardChangeRepetitionMultiplicand = null;
    private Double hardWeightSurvivalRatio = null;
    private Integer startingHardWeight = null; // TODO determine dynamically

    public Class<DeciderScoreComparatorFactory> getDeciderScoreComparatorClass() {
        return deciderScoreComparatorClass;
    }

    public void setDeciderScoreComparatorClass(Class<DeciderScoreComparatorFactory> deciderScoreComparatorClass) {
        this.deciderScoreComparatorClass = deciderScoreComparatorClass;
    }

    public DeciderScoreComparatorFactoryType getDeciderScoreComparatorFactoryType() {
        return deciderScoreComparatorFactoryType;
    }

    public void setDeciderScoreComparatorFactoryType(DeciderScoreComparatorFactoryType deciderScoreComparatorFactoryType) {
        this.deciderScoreComparatorFactoryType = deciderScoreComparatorFactoryType;
    }

    public Integer getHardScoreActivationThreshold() {
        return hardScoreActivationThreshold;
    }

    public void setHardScoreActivationThreshold(Integer hardScoreActivationThreshold) {
        this.hardScoreActivationThreshold = hardScoreActivationThreshold;
    }

    public Integer getSuccessiveNoHardChangeMinimum() {
        return successiveNoHardChangeMinimum;
    }

    public void setSuccessiveNoHardChangeMinimum(Integer successiveNoHardChangeMinimum) {
        this.successiveNoHardChangeMinimum = successiveNoHardChangeMinimum;
    }

    public Integer getSuccessiveNoHardChangeMaximum() {
        return successiveNoHardChangeMaximum;
    }

    public void setSuccessiveNoHardChangeMaximum(Integer successiveNoHardChangeMaximum) {
        this.successiveNoHardChangeMaximum = successiveNoHardChangeMaximum;
    }

    public Double getSuccessiveNoHardChangeRepetitionMultiplicand() {
        return successiveNoHardChangeRepetitionMultiplicand;
    }

    public void setSuccessiveNoHardChangeRepetitionMultiplicand(Double successiveNoHardChangeRepetitionMultiplicand) {
        this.successiveNoHardChangeRepetitionMultiplicand = successiveNoHardChangeRepetitionMultiplicand;
    }

    public Double getHardWeightSurvivalRatio() {
        return hardWeightSurvivalRatio;
    }

    public void setHardWeightSurvivalRatio(Double hardWeightSurvivalRatio) {
        this.hardWeightSurvivalRatio = hardWeightSurvivalRatio;
    }

    public Integer getStartingHardWeight() {
        return startingHardWeight;
    }

    public void setStartingHardWeight(Integer startingHardWeight) {
        this.startingHardWeight = startingHardWeight;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public DeciderScoreComparatorFactory buildDeciderScoreComparatorFactory() {
        if (deciderScoreComparatorClass != null) {
            try {
                return deciderScoreComparatorClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("deciderScoreComparatorClass ("
                        + deciderScoreComparatorClass.getName()  + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("deciderScoreComparatorClass ("
                        + deciderScoreComparatorClass.getName() + ") does not have a public no-arg constructor", e);
            }
        } else if (deciderScoreComparatorFactoryType != null) {
            switch (deciderScoreComparatorFactoryType) {
                case NATURAL:
                    return new NaturalDeciderScoreComparatorFactory();
                case SHIFTING_HARD_PENALTY:
                    ShiftingHardPenaltyDeciderScoreComparatorFactory deciderScoreComparator
                            = new ShiftingHardPenaltyDeciderScoreComparatorFactory();
                    if (hardScoreActivationThreshold != null) {
                        deciderScoreComparator.setHardScoreActivationThreshold(hardScoreActivationThreshold);
                    }
                    if (successiveNoHardChangeMinimum != null) {
                        deciderScoreComparator.setSuccessiveNoHardChangeMinimum(successiveNoHardChangeMinimum);
                    }
                    if (successiveNoHardChangeMaximum != null) {
                        deciderScoreComparator.setSuccessiveNoHardChangeMaximum(successiveNoHardChangeMaximum);
                    }
                    if (successiveNoHardChangeRepetitionMultiplicand != null) {
                        deciderScoreComparator.setSuccessiveNoHardChangeRepetitionMultiplicand(
                                successiveNoHardChangeRepetitionMultiplicand);
                    }
                    if (hardWeightSurvivalRatio != null) {
                        deciderScoreComparator.setHardWeightSurvivalRatio(hardWeightSurvivalRatio);
                    }
                    if (startingHardWeight != null) {
                        deciderScoreComparator.setStartingHardWeight(startingHardWeight);
                    }
                    return deciderScoreComparator;
                default:
                    throw new IllegalStateException("The deciderScoreComparatorFactoryType ("
                            + deciderScoreComparatorFactoryType + ") is not implemented");
            }
        } else {
            return new NaturalDeciderScoreComparatorFactory();
        }
    }

    public void inherit(DeciderScoreComparatorFactoryConfig inheritedConfig) {
        if (deciderScoreComparatorClass == null && deciderScoreComparatorFactoryType == null) {
            deciderScoreComparatorClass = inheritedConfig.getDeciderScoreComparatorClass();
            deciderScoreComparatorFactoryType = inheritedConfig.getDeciderScoreComparatorFactoryType();
        }
        if (hardScoreActivationThreshold == null) {
            hardScoreActivationThreshold = inheritedConfig.getHardScoreActivationThreshold();
        }
        if (successiveNoHardChangeMinimum == null) {
            successiveNoHardChangeMinimum = inheritedConfig.getSuccessiveNoHardChangeMinimum();
        }
        if (successiveNoHardChangeMaximum == null) {
            successiveNoHardChangeMaximum = inheritedConfig.getSuccessiveNoHardChangeMaximum();
        }
        if (successiveNoHardChangeRepetitionMultiplicand == null) {
            successiveNoHardChangeRepetitionMultiplicand = inheritedConfig
                    .getSuccessiveNoHardChangeRepetitionMultiplicand();
        }
        if (hardWeightSurvivalRatio == null) {
            hardWeightSurvivalRatio = inheritedConfig.getHardWeightSurvivalRatio();
        }
        if (startingHardWeight == null) {
            startingHardWeight = inheritedConfig.getStartingHardWeight();
        }
    }

    public static enum DeciderScoreComparatorFactoryType {
        NATURAL,
        SHIFTING_HARD_PENALTY
    }

}
