/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.solver.termination;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.io.jaxb.adapter.JaxbDurationAdapter;
import org.optaplanner.core.impl.solver.termination.Termination;

@XmlType(propOrder = {
        "terminationClass",
        "terminationCompositionStyle",
        "spentLimit",
        "millisecondsSpentLimit",
        "secondsSpentLimit",
        "minutesSpentLimit",
        "hoursSpentLimit",
        "daysSpentLimit",
        "unimprovedSpentLimit",
        "unimprovedMillisecondsSpentLimit",
        "unimprovedSecondsSpentLimit",
        "unimprovedMinutesSpentLimit",
        "unimprovedHoursSpentLimit",
        "unimprovedDaysSpentLimit",
        "unimprovedScoreDifferenceThreshold",
        "bestScoreLimit",
        "bestScoreFeasible",
        "stepCountLimit",
        "unimprovedStepCountLimit",
        "scoreCalculationCountLimit",
        "terminationConfigList"
})
public class TerminationConfig extends AbstractConfig<TerminationConfig> {

    private Class<? extends Termination> terminationClass = null;

    private TerminationCompositionStyle terminationCompositionStyle = null;

    @XmlJavaTypeAdapter(JaxbDurationAdapter.class)
    private Duration spentLimit = null;
    private Long millisecondsSpentLimit = null;
    private Long secondsSpentLimit = null;
    private Long minutesSpentLimit = null;
    private Long hoursSpentLimit = null;
    private Long daysSpentLimit = null;

    @XmlJavaTypeAdapter(JaxbDurationAdapter.class)
    private Duration unimprovedSpentLimit = null;
    private Long unimprovedMillisecondsSpentLimit = null;
    private Long unimprovedSecondsSpentLimit = null;
    private Long unimprovedMinutesSpentLimit = null;
    private Long unimprovedHoursSpentLimit = null;
    private Long unimprovedDaysSpentLimit = null;
    private String unimprovedScoreDifferenceThreshold = null;

    private String bestScoreLimit = null;
    private Boolean bestScoreFeasible = null;

    private Integer stepCountLimit = null;
    private Integer unimprovedStepCountLimit = null;

    private Long scoreCalculationCountLimit = null;

    @XmlElement(name = "termination")
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

    public Duration getSpentLimit() {
        return spentLimit;
    }

    public void setSpentLimit(Duration spentLimit) {
        this.spentLimit = spentLimit;
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

    public Long getDaysSpentLimit() {
        return daysSpentLimit;
    }

    public void setDaysSpentLimit(Long daysSpentLimit) {
        this.daysSpentLimit = daysSpentLimit;
    }

    public Duration getUnimprovedSpentLimit() {
        return unimprovedSpentLimit;
    }

    public void setUnimprovedSpentLimit(Duration unimprovedSpentLimit) {
        this.unimprovedSpentLimit = unimprovedSpentLimit;
    }

    public Long getUnimprovedMillisecondsSpentLimit() {
        return unimprovedMillisecondsSpentLimit;
    }

    public void setUnimprovedMillisecondsSpentLimit(Long unimprovedMillisecondsSpentLimit) {
        this.unimprovedMillisecondsSpentLimit = unimprovedMillisecondsSpentLimit;
    }

    public Long getUnimprovedSecondsSpentLimit() {
        return unimprovedSecondsSpentLimit;
    }

    public void setUnimprovedSecondsSpentLimit(Long unimprovedSecondsSpentLimit) {
        this.unimprovedSecondsSpentLimit = unimprovedSecondsSpentLimit;
    }

    public Long getUnimprovedMinutesSpentLimit() {
        return unimprovedMinutesSpentLimit;
    }

    public void setUnimprovedMinutesSpentLimit(Long unimprovedMinutesSpentLimit) {
        this.unimprovedMinutesSpentLimit = unimprovedMinutesSpentLimit;
    }

    public Long getUnimprovedHoursSpentLimit() {
        return unimprovedHoursSpentLimit;
    }

    public void setUnimprovedHoursSpentLimit(Long unimprovedHoursSpentLimit) {
        this.unimprovedHoursSpentLimit = unimprovedHoursSpentLimit;
    }

    public Long getUnimprovedDaysSpentLimit() {
        return unimprovedDaysSpentLimit;
    }

    public void setUnimprovedDaysSpentLimit(Long unimprovedDaysSpentLimit) {
        this.unimprovedDaysSpentLimit = unimprovedDaysSpentLimit;
    }

    public String getUnimprovedScoreDifferenceThreshold() {
        return unimprovedScoreDifferenceThreshold;
    }

    public void setUnimprovedScoreDifferenceThreshold(String unimprovedScoreDifferenceThreshold) {
        this.unimprovedScoreDifferenceThreshold = unimprovedScoreDifferenceThreshold;
    }

    public String getBestScoreLimit() {
        return bestScoreLimit;
    }

    public void setBestScoreLimit(String bestScoreLimit) {
        this.bestScoreLimit = bestScoreLimit;
    }

    public Boolean getBestScoreFeasible() {
        return bestScoreFeasible;
    }

    public void setBestScoreFeasible(Boolean bestScoreFeasible) {
        this.bestScoreFeasible = bestScoreFeasible;
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

    public Long getScoreCalculationCountLimit() {
        return scoreCalculationCountLimit;
    }

    public void setScoreCalculationCountLimit(Long scoreCalculationCountLimit) {
        this.scoreCalculationCountLimit = scoreCalculationCountLimit;
    }

    public List<TerminationConfig> getTerminationConfigList() {
        return terminationConfigList;
    }

    public void setTerminationConfigList(List<TerminationConfig> terminationConfigList) {
        this.terminationConfigList = terminationConfigList;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public TerminationConfig withTerminationClass(Class<? extends Termination> terminationClass) {
        this.terminationClass = terminationClass;
        return this;
    }

    public TerminationConfig withTerminationCompositionStyle(TerminationCompositionStyle terminationCompositionStyle) {
        this.terminationCompositionStyle = terminationCompositionStyle;
        return this;
    }

    public TerminationConfig withSpentLimit(Duration spentLimit) {
        this.spentLimit = spentLimit;
        return this;
    }

    public TerminationConfig withMillisecondsSpentLimit(Long millisecondsSpentLimit) {
        this.millisecondsSpentLimit = millisecondsSpentLimit;
        return this;
    }

    public TerminationConfig withSecondsSpentLimit(Long secondsSpentLimit) {
        this.secondsSpentLimit = secondsSpentLimit;
        return this;
    }

    public TerminationConfig withMinutesSpentLimit(Long minutesSpentLimit) {
        this.minutesSpentLimit = minutesSpentLimit;
        return this;
    }

    public TerminationConfig withHoursSpentLimit(Long hoursSpentLimit) {
        this.hoursSpentLimit = hoursSpentLimit;
        return this;
    }

    public TerminationConfig withDaysSpentLimit(Long daysSpentLimit) {
        this.daysSpentLimit = daysSpentLimit;
        return this;
    }

    public TerminationConfig withUnimprovedSpentLimit(Duration unimprovedSpentLimit) {
        this.unimprovedSpentLimit = unimprovedSpentLimit;
        return this;
    }

    public TerminationConfig withUnimprovedMillisecondsSpentLimit(Long unimprovedMillisecondsSpentLimit) {
        this.unimprovedMillisecondsSpentLimit = unimprovedMillisecondsSpentLimit;
        return this;
    }

    public TerminationConfig withUnimprovedSecondsSpentLimit(Long unimprovedSecondsSpentLimit) {
        this.unimprovedSecondsSpentLimit = unimprovedSecondsSpentLimit;
        return this;
    }

    public TerminationConfig withUnimprovedMinutesSpentLimit(Long unimprovedMinutesSpentLimit) {
        this.unimprovedMinutesSpentLimit = unimprovedMinutesSpentLimit;
        return this;
    }

    public TerminationConfig withUnimprovedHoursSpentLimit(Long unimprovedHoursSpentLimit) {
        this.unimprovedHoursSpentLimit = unimprovedHoursSpentLimit;
        return this;
    }

    public TerminationConfig withUnimprovedDaysSpentLimit(Long unimprovedDaysSpentLimit) {
        this.unimprovedDaysSpentLimit = unimprovedDaysSpentLimit;
        return this;
    }

    public TerminationConfig withUnimprovedScoreDifferenceThreshold(String unimprovedScoreDifferenceThreshold) {
        this.unimprovedScoreDifferenceThreshold = unimprovedScoreDifferenceThreshold;
        return this;
    }

    public TerminationConfig withBestScoreLimit(String bestScoreLimit) {
        this.bestScoreLimit = bestScoreLimit;
        return this;
    }

    public TerminationConfig withBestScoreFeasible(Boolean bestScoreFeasible) {
        this.bestScoreFeasible = bestScoreFeasible;
        return this;
    }

    public TerminationConfig withStepCountLimit(Integer stepCountLimit) {
        this.stepCountLimit = stepCountLimit;
        return this;
    }

    public TerminationConfig withUnimprovedStepCountLimit(Integer unimprovedStepCountLimit) {
        this.unimprovedStepCountLimit = unimprovedStepCountLimit;
        return this;
    }

    public TerminationConfig withScoreCalculationCountLimit(Long scoreCalculationCountLimit) {
        this.scoreCalculationCountLimit = scoreCalculationCountLimit;
        return this;
    }

    public TerminationConfig withTerminationConfigList(List<TerminationConfig> terminationConfigList) {
        this.terminationConfigList = terminationConfigList;
        return this;
    }

    public void overwriteSpentLimit(Duration spentLimit) {
        setSpentLimit(spentLimit);
        setMillisecondsSpentLimit(null);
        setSecondsSpentLimit(null);
        setMinutesSpentLimit(null);
        setHoursSpentLimit(null);
        setDaysSpentLimit(null);
    }

    public Long calculateTimeMillisSpentLimit() {
        if (millisecondsSpentLimit == null && secondsSpentLimit == null
                && minutesSpentLimit == null && hoursSpentLimit == null && daysSpentLimit == null) {
            if (spentLimit != null) {
                if (spentLimit.getNano() % 1000 != 0) {
                    throw new IllegalArgumentException("The termination spentLimit (" + spentLimit
                            + ") cannot use nanoseconds.");
                }
                return spentLimit.toMillis();
            }
            return null;
        }
        if (spentLimit != null) {
            throw new IllegalArgumentException("The termination spentLimit (" + spentLimit
                    + ") cannot be combined with millisecondsSpentLimit (" + millisecondsSpentLimit
                    + "), secondsSpentLimit (" + secondsSpentLimit
                    + "), minutesSpentLimit (" + minutesSpentLimit
                    + "), hoursSpentLimit (" + hoursSpentLimit
                    + ") or daysSpentLimit (" + daysSpentLimit + ").");
        }
        long timeMillisSpentLimit = 0L;
        if (millisecondsSpentLimit != null) {
            if (millisecondsSpentLimit < 0L) {
                throw new IllegalArgumentException("The termination millisecondsSpentLimit (" + millisecondsSpentLimit
                        + ") cannot be negative.");
            }
            timeMillisSpentLimit += millisecondsSpentLimit;
        }
        if (secondsSpentLimit != null) {
            if (secondsSpentLimit < 0L) {
                throw new IllegalArgumentException("The termination secondsSpentLimit (" + secondsSpentLimit
                        + ") cannot be negative.");
            }
            timeMillisSpentLimit += secondsSpentLimit * 1_000L;
        }
        if (minutesSpentLimit != null) {
            if (minutesSpentLimit < 0L) {
                throw new IllegalArgumentException("The termination minutesSpentLimit (" + minutesSpentLimit
                        + ") cannot be negative.");
            }
            timeMillisSpentLimit += minutesSpentLimit * 60_000L;
        }
        if (hoursSpentLimit != null) {
            if (hoursSpentLimit < 0L) {
                throw new IllegalArgumentException("The termination hoursSpentLimit (" + hoursSpentLimit
                        + ") cannot be negative.");
            }
            timeMillisSpentLimit += hoursSpentLimit * 3_600_000L;
        }
        if (daysSpentLimit != null) {
            if (daysSpentLimit < 0L) {
                throw new IllegalArgumentException("The termination daysSpentLimit (" + daysSpentLimit
                        + ") cannot be negative.");
            }
            timeMillisSpentLimit += daysSpentLimit * 86_400_000L;
        }
        return timeMillisSpentLimit;
    }

    public void shortenTimeMillisSpentLimit(long timeMillisSpentLimit) {
        Long oldLimit = calculateTimeMillisSpentLimit();
        if (oldLimit == null || timeMillisSpentLimit < oldLimit) {
            spentLimit = null;
            millisecondsSpentLimit = timeMillisSpentLimit;
            secondsSpentLimit = null;
            minutesSpentLimit = null;
            hoursSpentLimit = null;
            daysSpentLimit = null;
        }
    }

    public void overwriteUnimprovedSpentLimit(Duration unimprovedSpentLimit) {
        setUnimprovedSpentLimit(unimprovedSpentLimit);
        setUnimprovedMillisecondsSpentLimit(null);
        setUnimprovedSecondsSpentLimit(null);
        setUnimprovedMinutesSpentLimit(null);
        setUnimprovedHoursSpentLimit(null);
        setUnimprovedDaysSpentLimit(null);
    }

    public Long calculateUnimprovedTimeMillisSpentLimit() {
        if (unimprovedMillisecondsSpentLimit == null && unimprovedSecondsSpentLimit == null
                && unimprovedMinutesSpentLimit == null && unimprovedHoursSpentLimit == null) {
            if (unimprovedSpentLimit != null) {
                if (unimprovedSpentLimit.getNano() % 1000 != 0) {
                    throw new IllegalArgumentException("The termination unimprovedSpentLimit (" + unimprovedSpentLimit
                            + ") cannot use nanoseconds.");
                }
                return unimprovedSpentLimit.toMillis();
            }
            return null;
        }
        if (unimprovedSpentLimit != null) {
            throw new IllegalArgumentException("The termination unimprovedSpentLimit (" + unimprovedSpentLimit
                    + ") cannot be combined with unimprovedMillisecondsSpentLimit (" + unimprovedMillisecondsSpentLimit
                    + "), unimprovedSecondsSpentLimit (" + unimprovedSecondsSpentLimit
                    + "), unimprovedMinutesSpentLimit (" + unimprovedMinutesSpentLimit
                    + "), unimprovedHoursSpentLimit (" + unimprovedHoursSpentLimit + ").");
        }
        long unimprovedTimeMillisSpentLimit = 0L;
        if (unimprovedMillisecondsSpentLimit != null) {
            if (unimprovedMillisecondsSpentLimit < 0L) {
                throw new IllegalArgumentException(
                        "The termination unimprovedMillisecondsSpentLimit (" + unimprovedMillisecondsSpentLimit
                                + ") cannot be negative.");
            }
            unimprovedTimeMillisSpentLimit += unimprovedMillisecondsSpentLimit;
        }
        if (unimprovedSecondsSpentLimit != null) {
            if (unimprovedSecondsSpentLimit < 0L) {
                throw new IllegalArgumentException("The termination unimprovedSecondsSpentLimit (" + unimprovedSecondsSpentLimit
                        + ") cannot be negative.");
            }
            unimprovedTimeMillisSpentLimit += unimprovedSecondsSpentLimit * 1000L;
        }
        if (unimprovedMinutesSpentLimit != null) {
            if (unimprovedMinutesSpentLimit < 0L) {
                throw new IllegalArgumentException("The termination unimprovedMinutesSpentLimit (" + unimprovedMinutesSpentLimit
                        + ") cannot be negative.");
            }
            unimprovedTimeMillisSpentLimit += unimprovedMinutesSpentLimit * 60000L;
        }
        if (unimprovedHoursSpentLimit != null) {
            if (unimprovedHoursSpentLimit < 0L) {
                throw new IllegalArgumentException("The termination unimprovedHoursSpentLimit (" + unimprovedHoursSpentLimit
                        + ") cannot be negative.");
            }
            unimprovedTimeMillisSpentLimit += unimprovedHoursSpentLimit * 3600000L;
        }
        if (unimprovedDaysSpentLimit != null) {
            if (unimprovedDaysSpentLimit < 0L) {
                throw new IllegalArgumentException("The termination unimprovedDaysSpentLimit (" + unimprovedDaysSpentLimit
                        + ") cannot be negative.");
            }
            unimprovedTimeMillisSpentLimit += unimprovedDaysSpentLimit * 86400000L;
        }
        return unimprovedTimeMillisSpentLimit;
    }

    /**
     * Return true if this TerminationConfig configures a termination condition.
     * Note: this does not mean it will always terminate: ex: bestScoreLimit configured,
     * but it is impossible to reach the bestScoreLimit.
     */
    @XmlTransient
    public boolean isConfigured() {
        return terminationClass != null ||
                spentLimit != null ||
                millisecondsSpentLimit != null ||
                secondsSpentLimit != null ||
                minutesSpentLimit != null ||
                hoursSpentLimit != null ||
                daysSpentLimit != null ||
                bestScoreLimit != null ||
                unimprovedSpentLimit != null ||
                unimprovedMillisecondsSpentLimit != null ||
                unimprovedSecondsSpentLimit != null ||
                unimprovedMinutesSpentLimit != null ||
                unimprovedHoursSpentLimit != null ||
                unimprovedDaysSpentLimit != null ||
                stepCountLimit != null ||
                terminationConfigList != null;
    }

    @Override
    public TerminationConfig inherit(TerminationConfig inheritedConfig) {
        terminationClass = ConfigUtils.inheritOverwritableProperty(terminationClass,
                inheritedConfig.getTerminationClass());
        terminationCompositionStyle = ConfigUtils.inheritOverwritableProperty(terminationCompositionStyle,
                inheritedConfig.getTerminationCompositionStyle());
        spentLimit = ConfigUtils.inheritOverwritableProperty(spentLimit,
                inheritedConfig.getSpentLimit());
        millisecondsSpentLimit = ConfigUtils.inheritOverwritableProperty(millisecondsSpentLimit,
                inheritedConfig.getMillisecondsSpentLimit());
        secondsSpentLimit = ConfigUtils.inheritOverwritableProperty(secondsSpentLimit,
                inheritedConfig.getSecondsSpentLimit());
        minutesSpentLimit = ConfigUtils.inheritOverwritableProperty(minutesSpentLimit,
                inheritedConfig.getMinutesSpentLimit());
        hoursSpentLimit = ConfigUtils.inheritOverwritableProperty(hoursSpentLimit,
                inheritedConfig.getHoursSpentLimit());
        daysSpentLimit = ConfigUtils.inheritOverwritableProperty(daysSpentLimit,
                inheritedConfig.getDaysSpentLimit());
        unimprovedSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedSpentLimit,
                inheritedConfig.getUnimprovedSpentLimit());
        unimprovedMillisecondsSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedMillisecondsSpentLimit,
                inheritedConfig.getUnimprovedMillisecondsSpentLimit());
        unimprovedSecondsSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedSecondsSpentLimit,
                inheritedConfig.getUnimprovedSecondsSpentLimit());
        unimprovedMinutesSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedMinutesSpentLimit,
                inheritedConfig.getUnimprovedMinutesSpentLimit());
        unimprovedHoursSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedHoursSpentLimit,
                inheritedConfig.getUnimprovedHoursSpentLimit());
        unimprovedDaysSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedDaysSpentLimit,
                inheritedConfig.getUnimprovedDaysSpentLimit());
        unimprovedScoreDifferenceThreshold = ConfigUtils.inheritOverwritableProperty(unimprovedScoreDifferenceThreshold,
                inheritedConfig.getUnimprovedScoreDifferenceThreshold());
        bestScoreLimit = ConfigUtils.inheritOverwritableProperty(bestScoreLimit,
                inheritedConfig.getBestScoreLimit());
        bestScoreFeasible = ConfigUtils.inheritOverwritableProperty(bestScoreFeasible,
                inheritedConfig.getBestScoreFeasible());
        stepCountLimit = ConfigUtils.inheritOverwritableProperty(stepCountLimit,
                inheritedConfig.getStepCountLimit());
        unimprovedStepCountLimit = ConfigUtils.inheritOverwritableProperty(unimprovedStepCountLimit,
                inheritedConfig.getUnimprovedStepCountLimit());
        scoreCalculationCountLimit = ConfigUtils.inheritOverwritableProperty(scoreCalculationCountLimit,
                inheritedConfig.getScoreCalculationCountLimit());
        terminationConfigList = ConfigUtils.inheritMergeableListConfig(
                terminationConfigList, inheritedConfig.getTerminationConfigList());
        return this;
    }

    @Override
    public TerminationConfig copyConfig() {
        return new TerminationConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        classVisitor.accept(terminationClass);
        if (terminationConfigList != null) {
            terminationConfigList.forEach(tc -> tc.visitReferencedClasses(classVisitor));
        }
    }

}
