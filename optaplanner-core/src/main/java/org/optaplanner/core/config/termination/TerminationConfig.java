/*
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

package org.optaplanner.core.config.termination;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.termination.AbstractCompositeTermination;
import org.optaplanner.core.impl.termination.AndCompositeTermination;
import org.optaplanner.core.impl.termination.OrCompositeTermination;
import org.optaplanner.core.impl.termination.ScoreAttainedTermination;
import org.optaplanner.core.impl.termination.StepCountTermination;
import org.optaplanner.core.impl.termination.Termination;
import org.optaplanner.core.impl.termination.TimeMillisSpendTermination;
import org.optaplanner.core.impl.termination.UnimprovedStepCountTermination;

@XStreamAlias("termination")
public class TerminationConfig implements Cloneable {

    private Class<? extends Termination> terminationClass = null;

    private TerminationCompositionStyle terminationCompositionStyle = null;

    private Long maximumTimeMillisSpend = null;
    private Long maximumSecondsSpend = null;
    private Long maximumMinutesSpend = null;
    private Long maximumHoursSpend = null;
    private String scoreAttained = null;
    private Integer maximumStepCount = null;
    private Integer maximumUnimprovedStepCount = null;

    @XStreamImplicit(itemFieldName = "termination")
    private List<TerminationConfig> terminationConfigList = null;

    public Class<? extends Termination> getTerminationClass() {
        return terminationClass;
    }

    public void setTerminationClass(Class<? extends Termination> terminationClass) {
        this.terminationClass = terminationClass;
    }

    public TerminationCompositionStyle getTerminationCompositionStyle() {
        return terminationCompositionStyle;
    }

    public void setTerminationCompositionStyle(TerminationCompositionStyle terminationCompositionStyle) {
        this.terminationCompositionStyle = terminationCompositionStyle;
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

    public Integer getMaximumStepCount() {
        return maximumStepCount;
    }

    public void setMaximumStepCount(Integer maximumStepCount) {
        this.maximumStepCount = maximumStepCount;
    }

    public Integer getMaximumUnimprovedStepCount() {
        return maximumUnimprovedStepCount;
    }

    public void setMaximumUnimprovedStepCount(Integer maximumUnimprovedStepCount) {
        this.maximumUnimprovedStepCount = maximumUnimprovedStepCount;
    }

    public List<TerminationConfig> getTerminationConfigList() {
        return terminationConfigList;
    }

    public void setTerminationConfigList(List<TerminationConfig> terminationConfigList) {
        this.terminationConfigList = terminationConfigList;
    }
    
    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Termination buildTermination(ScoreDefinition scoreDefinition, Termination chainedTermination) {
        Termination termination = buildTermination(scoreDefinition);
        if (termination == null) {
            return chainedTermination;
        }
        return new OrCompositeTermination(chainedTermination, termination);
    }

    public Termination buildTermination(ScoreDefinition scoreDefinition) {
        List<Termination> terminationList = new ArrayList<Termination>();
        if (terminationClass != null) {
            Termination termination  = ConfigUtils.newInstance(this, "terminationClass", terminationClass);
            terminationList.add(termination);
        }
        Long maximumTimeMillisSpendTotal = calculateMaximumTimeMillisSpendTotal();
        if (maximumTimeMillisSpendTotal != null) {
            TimeMillisSpendTermination termination = new TimeMillisSpendTermination();
            termination.setMaximumTimeMillisSpend(maximumTimeMillisSpendTotal);
            terminationList.add(termination);
        }
        if (scoreAttained != null) {
            ScoreAttainedTermination termination = new ScoreAttainedTermination();
            termination.setScoreAttained(scoreDefinition.parseScore(scoreAttained));
            terminationList.add(termination);
        }
        if (maximumStepCount != null) {
            StepCountTermination termination = new StepCountTermination();
            termination.setMaximumStepCount(maximumStepCount);
            terminationList.add(termination);
        }
        if (maximumUnimprovedStepCount != null) {
            UnimprovedStepCountTermination termination = new UnimprovedStepCountTermination();
            termination.setMaximumUnimprovedStepCount(maximumUnimprovedStepCount);
            terminationList.add(termination);
        }
        if (!CollectionUtils.isEmpty(terminationConfigList)) {
            for (TerminationConfig terminationConfig : terminationConfigList) {
                Termination termination = terminationConfig.buildTermination(scoreDefinition);
                if (termination != null) {
                    terminationList.add(termination);
                }
            }
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
                        + ") is not implemented.");
            }
            compositeTermination.setTerminationList(terminationList);
            return compositeTermination;
        } else {
            return null;
        }
    }

    public Long calculateMaximumTimeMillisSpendTotal() {
        if (maximumTimeMillisSpend == null && maximumSecondsSpend == null && maximumMinutesSpend == null
                && maximumHoursSpend == null) {
            return null;
        }
        long maximumTimeMillisSpendTotal = 0L;
        if (maximumTimeMillisSpend != null) {
            maximumTimeMillisSpendTotal += maximumTimeMillisSpend;
        }
        if (maximumSecondsSpend != null) {
            maximumTimeMillisSpendTotal += maximumSecondsSpend * 1000L;
        }
        if (maximumMinutesSpend != null) {
            maximumTimeMillisSpendTotal += maximumMinutesSpend * 60000L;
        }
        if (maximumHoursSpend != null) {
            maximumTimeMillisSpendTotal += maximumHoursSpend * 3600000L;
        }
        return maximumTimeMillisSpendTotal;
    }

    public void shortenMaximumTimeMillisSpendTotal(long maximumTimeMillisSpendTotal) {
        Long oldMaximumTimeMillisSpendTotal = calculateMaximumTimeMillisSpendTotal();
        if (oldMaximumTimeMillisSpendTotal == null || maximumTimeMillisSpendTotal < oldMaximumTimeMillisSpendTotal) {
            maximumTimeMillisSpend = maximumTimeMillisSpendTotal;
            maximumSecondsSpend = null;
            maximumMinutesSpend = null;
            maximumHoursSpend = null;
        }
    }

    public void inherit(TerminationConfig inheritedConfig) {
        if (terminationClass == null) {
            terminationClass = inheritedConfig.getTerminationClass();
        }
        if (terminationCompositionStyle == null) {
            terminationCompositionStyle = inheritedConfig.getTerminationCompositionStyle();
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
        if (maximumStepCount == null) {
            maximumStepCount = inheritedConfig.getMaximumStepCount();
        }
        if (maximumUnimprovedStepCount == null) {
            maximumUnimprovedStepCount = inheritedConfig.getMaximumUnimprovedStepCount();
        }
        terminationConfigList = ConfigUtils.inheritMergeableListProperty(
                terminationConfigList, inheritedConfig.getTerminationConfigList());
    }

    @Override
    public TerminationConfig clone() {
        TerminationConfig clone;
        try {
            clone = (TerminationConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Impossible exception because TerminationConfig implements Cloneable.", e);
        }
        // Deep clone terminationConfigList
        if (terminationConfigList != null) {
            List<TerminationConfig> clonedTerminationConfigList = new ArrayList<TerminationConfig>(
                    terminationConfigList.size());
            for (TerminationConfig terminationConfig : terminationConfigList) {
                TerminationConfig clonedTerminationConfig = terminationConfig.clone();
                clonedTerminationConfigList.add(clonedTerminationConfig);
            }
            clone.terminationConfigList = clonedTerminationConfigList;
        } else {
            clone.terminationConfigList = null;
        }
        return clone;
    }

    public enum TerminationCompositionStyle {
        AND,
        OR,
    }

}
