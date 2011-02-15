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

package org.drools.planner.config.localsearch.termination;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.core.localsearch.termination.AbstractCompositeTermination;
import org.drools.planner.core.localsearch.termination.AndCompositeTermination;
import org.drools.planner.core.localsearch.termination.ScoreAttainedTermination;
import org.drools.planner.core.localsearch.termination.Termination;
import org.drools.planner.core.localsearch.termination.OrCompositeTermination;
import org.drools.planner.core.localsearch.termination.StepCountTermination;
import org.drools.planner.core.localsearch.termination.TimeMillisSpendTermination;
import org.drools.planner.core.localsearch.termination.UnimprovedStepCountTermination;
import org.drools.planner.core.score.definition.ScoreDefinition;

/**
 * @author Geoffrey De Smet
 */
@XStreamAlias("termination")
public class TerminationConfig {

    private Termination termination = null; // TODO make into a list
    private Class<Termination> terminationClass = null;

    private TerminationCompositionStyle terminationCompositionStyle = null;

    private Integer maximumStepCount = null;
    private Long maximumTimeMillisSpend = null;
    private Long maximumSecondsSpend = null;
    private Long maximumMinutesSpend = null;
    private Long maximumHoursSpend = null;
    private String scoreAttained = null;
    private Integer maximumUnimprovedStepCount = null;

    public Termination getTermination() {
        return termination;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    public Class<Termination> getTerminationClass() {
        return terminationClass;
    }

    public void setTerminationClass(Class<Termination> terminationClass) {
        this.terminationClass = terminationClass;
    }

    public TerminationCompositionStyle getTerminationCompositionStyle() {
        return terminationCompositionStyle;
    }

    public void setTerminationCompositionStyle(TerminationCompositionStyle terminationCompositionStyle) {
        this.terminationCompositionStyle = terminationCompositionStyle;
    }

    public Integer getMaximumStepCount() {
        return maximumStepCount;
    }

    public void setMaximumStepCount(Integer maximumStepCount) {
        this.maximumStepCount = maximumStepCount;
    }

    public Long getMaximumTimeMillisSpend() {
        return maximumTimeMillisSpend;
    }

    public void setMaximumTimeMillisSpend(Long maximumTimeMillisSpend) {
        this.maximumTimeMillisSpend = maximumTimeMillisSpend;
    }

    public Long getMaximumSecondsSpend() {
        return maximumSecondsSpend;
    }

    public void setMaximumSecondsSpend(Long maximumSecondsSpend) {
        this.maximumSecondsSpend = maximumSecondsSpend;
    }

    public Long getMaximumMinutesSpend() {
        return maximumMinutesSpend;
    }

    public void setMaximumMinutesSpend(Long maximumMinutesSpend) {
        this.maximumMinutesSpend = maximumMinutesSpend;
    }

    public Long getMaximumHoursSpend() {
        return maximumHoursSpend;
    }

    public void setMaximumHoursSpend(Long maximumHoursSpend) {
        this.maximumHoursSpend = maximumHoursSpend;
    }

    public String getScoreAttained() {
        return scoreAttained;
    }

    public void setScoreAttained(String scoreAttained) {
        this.scoreAttained = scoreAttained;
    }

    public Integer getMaximumUnimprovedStepCount() {
        return maximumUnimprovedStepCount;
    }

    public void setMaximumUnimprovedStepCount(Integer maximumUnimprovedStepCount) {
        this.maximumUnimprovedStepCount = maximumUnimprovedStepCount;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Termination buildTermination(ScoreDefinition scoreDefinition) {
        List<Termination> terminationList = new ArrayList<Termination>();
        if (termination != null) {
            terminationList.add(termination);
        }
        if (terminationClass != null) {
            try {
                terminationList.add(terminationClass.newInstance());
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("terminationClass (" + terminationClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("terminationClass (" + terminationClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
        }
        if (maximumStepCount != null) {
            StepCountTermination termination = new StepCountTermination();
            termination.setMaximumStepCount(maximumStepCount);
            terminationList.add(termination);
        }
        if (maximumTimeMillisSpend != null) {
            TimeMillisSpendTermination termination = new TimeMillisSpendTermination();
            termination.setMaximumTimeMillisSpend(maximumTimeMillisSpend);
            terminationList.add(termination);
        }
        if (maximumSecondsSpend != null) {
            TimeMillisSpendTermination termination = new TimeMillisSpendTermination();
            termination.setMaximumTimeMillisSpend(maximumSecondsSpend * 1000L);
            terminationList.add(termination);
        }
        if (maximumMinutesSpend != null) {
            TimeMillisSpendTermination termination = new TimeMillisSpendTermination();
            termination.setMaximumTimeMillisSpend(maximumMinutesSpend * 60000L);
            terminationList.add(termination);
        }
        if (maximumHoursSpend != null) {
            TimeMillisSpendTermination termination = new TimeMillisSpendTermination();
            termination.setMaximumTimeMillisSpend(maximumHoursSpend * 3600000L);
            terminationList.add(termination);
        }
        if (scoreAttained != null) {
            ScoreAttainedTermination termination = new ScoreAttainedTermination();
            termination.setScoreAttained(scoreDefinition.parseScore(scoreAttained));
            terminationList.add(termination);
        }
        if (maximumUnimprovedStepCount != null) {
            UnimprovedStepCountTermination termination = new UnimprovedStepCountTermination();
            termination.setMaximumUnimprovedStepCount(maximumUnimprovedStepCount);
            terminationList.add(termination);
        }
        if (terminationList.size() == 1) {
            return terminationList.get(0);
        } else if (terminationList.size() > 1) {
            AbstractCompositeTermination compositeTermination;
            if (terminationCompositionStyle == null || terminationCompositionStyle == TerminationCompositionStyle.OR) {
                compositeTermination = new OrCompositeTermination();
            } else if (terminationCompositionStyle == TerminationCompositionStyle.AND) {
                compositeTermination = new AndCompositeTermination();
            } else {
                throw new IllegalStateException("The terminationCompositionStyle (" + terminationCompositionStyle
                        + ") is not implemented");
            }
            compositeTermination.setTerminationList(terminationList);
            return compositeTermination;
        } else {
            TimeMillisSpendTermination termination = new TimeMillisSpendTermination();
            termination.setMaximumTimeMillisSpend(60000);
            return termination;
        }
    }

    public void inherit(TerminationConfig inheritedConfig) {
        // inherited terminations get compositely added
        if (termination == null) {
            termination = inheritedConfig.getTermination();
        }
        if (terminationClass == null) {
            terminationClass = inheritedConfig.getTerminationClass();
        }
        if (terminationCompositionStyle == null) {
            terminationCompositionStyle = inheritedConfig.getTerminationCompositionStyle();
        }
        if (maximumStepCount == null) {
            maximumStepCount = inheritedConfig.getMaximumStepCount();
        }
        if (maximumTimeMillisSpend == null) {
            maximumTimeMillisSpend = inheritedConfig.getMaximumTimeMillisSpend();
        }
        if (maximumSecondsSpend == null) {
            maximumSecondsSpend = inheritedConfig.getMaximumSecondsSpend();
        }
        if (maximumMinutesSpend == null) {
            maximumMinutesSpend = inheritedConfig.getMaximumMinutesSpend();
        }
        if (maximumHoursSpend == null) {
            maximumHoursSpend = inheritedConfig.getMaximumHoursSpend();
        }
        if (scoreAttained == null) {
            scoreAttained = inheritedConfig.getScoreAttained();
        }
        if (maximumUnimprovedStepCount == null) {
            maximumUnimprovedStepCount = inheritedConfig.getMaximumUnimprovedStepCount();
        }
    }

    public enum TerminationCompositionStyle {
        AND,
        OR,
    }
    
}
