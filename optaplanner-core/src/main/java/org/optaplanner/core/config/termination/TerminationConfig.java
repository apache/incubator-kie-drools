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
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.termination.AbstractCompositeTermination;
import org.optaplanner.core.impl.termination.AndCompositeTermination;
import org.optaplanner.core.impl.termination.BestScoreTermination;
import org.optaplanner.core.impl.termination.OrCompositeTermination;
import org.optaplanner.core.impl.termination.StepCountTermination;
import org.optaplanner.core.impl.termination.Termination;
import org.optaplanner.core.impl.termination.TimeMillisSpentTermination;
import org.optaplanner.core.impl.termination.UnimprovedStepCountTermination;

@XStreamAlias("termination")
public class TerminationConfig implements Cloneable {

    private Class<? extends Termination> terminationClass = null;

    private TerminationCompositionStyle terminationCompositionStyle = null;

    private Long millisecondsSpentLimit = null;
    private Long secondsSpentLimit = null;
    private Long minutesSpentLimit = null;
    private Long hoursSpentLimit = null;
    private String bestScoreLimit = null;
    private Integer stepCountLimit = null;
    private Integer unimprovedStepCountLimit = null;

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

    public Long getMillisecondsSpentLimit() {
        return millisecondsSpentLimit;
    }

    public void setMillisecondsSpentLimit(Long millisecondsSpentLimit) {
        this.millisecondsSpentLimit = millisecondsSpentLimit;
    }

    public Long getSecondsSpentLimit() {
        return secondsSpentLimit;
    }

    public void setSecondsSpentLimit(Long secondsSpentLimit) {
        this.secondsSpentLimit = secondsSpentLimit;
    }

    public Long getMinutesSpentLimit() {
        return minutesSpentLimit;
    }

    public void setMinutesSpentLimit(Long minutesSpentLimit) {
        this.minutesSpentLimit = minutesSpentLimit;
    }

    public Long getHoursSpentLimit() {
        return hoursSpentLimit;
    }

    public void setHoursSpentLimit(Long hoursSpentLimit) {
        this.hoursSpentLimit = hoursSpentLimit;
    }

    public String getBestScoreLimit() {
        return bestScoreLimit;
    }

    public void setBestScoreLimit(String bestScoreLimit) {
        this.bestScoreLimit = bestScoreLimit;
    }

    public Integer getStepCountLimit() {
        return stepCountLimit;
    }

    public void setStepCountLimit(Integer stepCountLimit) {
        this.stepCountLimit = stepCountLimit;
    }

    public Integer getUnimprovedStepCountLimit() {
        return unimprovedStepCountLimit;
    }

    public void setUnimprovedStepCountLimit(Integer unimprovedStepCountLimit) {
        this.unimprovedStepCountLimit = unimprovedStepCountLimit;
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

    public Termination buildTermination(HeuristicConfigPolicy configPolicy, Termination chainedTermination) {
        Termination termination = buildTermination(configPolicy);
        if (termination == null) {
            return chainedTermination;
        }
        return new OrCompositeTermination(chainedTermination, termination);
    }

    public Termination buildTermination(HeuristicConfigPolicy configPolicy) {
        List<Termination> terminationList = new ArrayList<Termination>();
        if (terminationClass != null) {
            Termination termination  = ConfigUtils.newInstance(this, "terminationClass", terminationClass);
            terminationList.add(termination);
        }
        Long timeMillisSpentLimit = calculateTimeMillisSpentLimit();
        if (timeMillisSpentLimit != null) {
            TimeMillisSpentTermination termination = new TimeMillisSpentTermination(timeMillisSpentLimit);
            terminationList.add(termination);
        }
        if (bestScoreLimit != null) {
            Score bestScoreLimit_ = configPolicy.getScoreDefinition().parseScore(bestScoreLimit);
            BestScoreTermination termination = new BestScoreTermination(bestScoreLimit_);
            terminationList.add(termination);
        }
        if (stepCountLimit != null) {
            StepCountTermination termination = new StepCountTermination(stepCountLimit);
            terminationList.add(termination);
        }
        if (unimprovedStepCountLimit != null) {
            UnimprovedStepCountTermination termination = new UnimprovedStepCountTermination(unimprovedStepCountLimit);
            terminationList.add(termination);
        }
        if (!CollectionUtils.isEmpty(terminationConfigList)) {
            for (TerminationConfig terminationConfig : terminationConfigList) {
                Termination termination = terminationConfig.buildTermination(configPolicy);
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
                compositeTermination = new OrCompositeTermination(terminationList);
            } else if (terminationCompositionStyle == TerminationCompositionStyle.AND) {
                compositeTermination = new AndCompositeTermination(terminationList);
            } else {
                throw new IllegalStateException("The terminationCompositionStyle (" + terminationCompositionStyle
                        + ") is not implemented.");
            }
            return compositeTermination;
        } else {
            return null;
        }
    }

    public Long calculateTimeMillisSpentLimit() {
        if (millisecondsSpentLimit == null && secondsSpentLimit == null && minutesSpentLimit == null
                && hoursSpentLimit == null) {
            return null;
        }
        long timeMillisSpentLimit = 0L;
        if (millisecondsSpentLimit != null) {
            timeMillisSpentLimit += millisecondsSpentLimit;
        }
        if (secondsSpentLimit != null) {
            timeMillisSpentLimit += secondsSpentLimit * 1000L;
        }
        if (minutesSpentLimit != null) {
            timeMillisSpentLimit += minutesSpentLimit * 60000L;
        }
        if (hoursSpentLimit != null) {
            timeMillisSpentLimit += hoursSpentLimit * 3600000L;
        }
        return timeMillisSpentLimit;
    }

    public void shortenTimeMillisSpentLimit(long timeMillisSpentLimit) {
        Long oldLimit = calculateTimeMillisSpentLimit();
        if (oldLimit == null || timeMillisSpentLimit < oldLimit) {
            millisecondsSpentLimit = timeMillisSpentLimit;
            secondsSpentLimit = null;
            minutesSpentLimit = null;
            hoursSpentLimit = null;
        }
    }

    public void inherit(TerminationConfig inheritedConfig) {
        if (terminationClass == null) {
            terminationClass = inheritedConfig.getTerminationClass();
        }
        if (terminationCompositionStyle == null) {
            terminationCompositionStyle = inheritedConfig.getTerminationCompositionStyle();
        }
        if (millisecondsSpentLimit == null) {
            millisecondsSpentLimit = inheritedConfig.getMillisecondsSpentLimit();
        }
        if (secondsSpentLimit == null) {
            secondsSpentLimit = inheritedConfig.getSecondsSpentLimit();
        }
        if (minutesSpentLimit == null) {
            minutesSpentLimit = inheritedConfig.getMinutesSpentLimit();
        }
        if (hoursSpentLimit == null) {
            hoursSpentLimit = inheritedConfig.getHoursSpentLimit();
        }
        if (bestScoreLimit == null) {
            bestScoreLimit = inheritedConfig.getBestScoreLimit();
        }
        if (stepCountLimit == null) {
            stepCountLimit = inheritedConfig.getStepCountLimit();
        }
        if (unimprovedStepCountLimit == null) {
            unimprovedStepCountLimit = inheritedConfig.getUnimprovedStepCountLimit();
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
            throw new IllegalStateException("Impossible state because TerminationConfig implements Cloneable.", e);
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
