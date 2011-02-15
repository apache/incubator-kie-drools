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

package org.drools.planner.config.score.definition;

import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.score.definition.SimpleScoreDefinition;
import org.drools.planner.core.score.definition.HardAndSoftScoreDefinition;
import org.drools.planner.core.score.calculator.ScoreCalculator;
import org.drools.planner.core.score.calculator.SimpleScoreCalculator;
import org.drools.planner.core.score.calculator.DefaultHardAndSoftConstraintScoreCalculator;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Geoffrey De Smet
 */
@XStreamAlias("scoreDefinition")
public class ScoreDefinitionConfig {

    private ScoreDefinition scoreDefinition = null;
    private Class<ScoreDefinition> scoreDefinitionClass = null;
    private ScoreDefinitionType scoreDefinitionType = null;

    public ScoreDefinition getScoreDefinition() {
        return scoreDefinition;
    }

    public void setScoreDefinition(ScoreDefinition scoreDefinition) {
        this.scoreDefinition = scoreDefinition;
    }

    public Class<ScoreDefinition> getScoreDefinitionClass() {
        return scoreDefinitionClass;
    }

    public void setScoreDefinitionClass(Class<ScoreDefinition> scoreDefinitionClass) {
        this.scoreDefinitionClass = scoreDefinitionClass;
    }

    public ScoreDefinitionType getScoreDefinitionType() {
        return scoreDefinitionType;
    }

    public void setScoreDefinitionType(ScoreDefinitionType scoreDefinitionType) {
        this.scoreDefinitionType = scoreDefinitionType;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public ScoreDefinition buildScoreDefinition() {
        if (scoreDefinition != null) {
            return scoreDefinition;
        } else if (scoreDefinitionClass != null) {
            try {
                return scoreDefinitionClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("scoreDefinitionClass (" + scoreDefinitionClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("scoreDefinitionClass (" + scoreDefinitionClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
        } else if (scoreDefinitionType != null) {
            switch (scoreDefinitionType) {
                case SIMPLE:
                    return new SimpleScoreDefinition();
                case HARD_AND_SOFT:
                    return new HardAndSoftScoreDefinition();
                default:
                    throw new IllegalStateException("The scoreDefinitionType (" + scoreDefinitionType
                            + ") is not implemented");
            }
        } else {
            return new SimpleScoreDefinition();
        }
    }

    /**
     * @TODO score-in-solution refactor
     */
    public ScoreCalculator buildScoreCalculator() {
        if (scoreDefinitionType != null) {
            switch (scoreDefinitionType) {
                case SIMPLE:
                    return new SimpleScoreCalculator();
                case HARD_AND_SOFT:
                    return new DefaultHardAndSoftConstraintScoreCalculator();
                default:
                    throw new IllegalStateException("The scoreDefinitionType (" + scoreDefinitionType
                            + ") is not implemented");
            }
        } else {
            return new SimpleScoreCalculator();
        }
    }

    public void inherit(ScoreDefinitionConfig inheritedConfig) {
        if (scoreDefinition == null && scoreDefinitionClass == null && scoreDefinitionType == null) {
            scoreDefinition = inheritedConfig.getScoreDefinition();
            scoreDefinitionClass = inheritedConfig.getScoreDefinitionClass();
            scoreDefinitionType = inheritedConfig.getScoreDefinitionType();
        }
    }

    public static enum ScoreDefinitionType {
        SIMPLE,
        HARD_AND_SOFT,
    }

}
