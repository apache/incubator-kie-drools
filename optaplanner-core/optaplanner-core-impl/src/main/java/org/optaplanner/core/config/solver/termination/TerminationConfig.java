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
        long timeMillisSpentLimit = 0L
                + requireNonNegative(millisecondsSpentLimit, "millisecondsSpentLimit")
                + requireNonNegative(secondsSpentLimit, "secondsSpentLimit") * 1_000L
                + requireNonNegative(minutesSpentLimit, "minutesSpentLimit") * 60_000L
                + requireNonNegative(hoursSpentLimit, "hoursSpentLimit") * 3_600_000L
                + requireNonNegative(daysSpentLimit, "daysSpentLimit") * 86_400_000L;
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
        long unimprovedTimeMillisSpentLimit = 0L
                + requireNonNegative(unimprovedMillisecondsSpentLimit, "unimprovedMillisecondsSpentLimit")
                + requireNonNegative(unimprovedSecondsSpentLimit, "unimprovedSecondsSpentLimit") * 1000L
                + requireNonNegative(unimprovedMinutesSpentLimit, "unimprovedMinutesSpentLimit") * 60_000L
                + requireNonNegative(unimprovedHoursSpentLimit, "unimprovedHoursSpentLimit") * 3_600_000L
                + requireNonNegative(unimprovedDaysSpentLimit, "unimprovedDaysSpentLimit") * 86_400_000L;
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
                timeSpentLimitIsSet() ||
                unimprovedTimeSpentLimitIsSet() ||
                bestScoreLimit != null ||
                bestScoreFeasible != null ||
                stepCountLimit != null ||
                unimprovedStepCountLimit != null ||
                scoreCalculationCountLimit != null ||
                isTerminationListConfigured();
    }

    private boolean isTerminationListConfigured() {
        if (terminationConfigList == null || terminationCompositionStyle == null) {
            return false;
        }

        switch (terminationCompositionStyle) {
            case AND:
                return terminationConfigList.stream().allMatch(TerminationConfig::isConfigured);
            case OR:
                return terminationConfigList.stream().anyMatch(TerminationConfig::isConfigured);
            default:
                throw new IllegalStateException("Unhandled case (" + terminationCompositionStyle + ").");
        }
    }

    @Override
    public TerminationConfig inherit(TerminationConfig inheritedConfig) {
        if (!timeSpentLimitIsSet()) {
            inheritTimeSpentLimit(inheritedConfig);
        }
        if (!unimprovedTimeSpentLimitIsSet()) {
            inheritUnimprovedTimeSpentLimit(inheritedConfig);
        }
        terminationClass = ConfigUtils.inheritOverwritableProperty(terminationClass,
                inheritedConfig.getTerminationClass());
        terminationCompositionStyle = ConfigUtils.inheritOverwritableProperty(terminationCompositionStyle,
                inheritedConfig.getTerminationCompositionStyle());
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

    private TerminationConfig inheritTimeSpentLimit(TerminationConfig parent) {
        spentLimit = ConfigUtils.inheritOverwritableProperty(spentLimit,
                parent.getSpentLimit());
        millisecondsSpentLimit = ConfigUtils.inheritOverwritableProperty(millisecondsSpentLimit,
                parent.getMillisecondsSpentLimit());
        secondsSpentLimit = ConfigUtils.inheritOverwritableProperty(secondsSpentLimit,
                parent.getSecondsSpentLimit());
        minutesSpentLimit = ConfigUtils.inheritOverwritableProperty(minutesSpentLimit,
                parent.getMinutesSpentLimit());
        hoursSpentLimit = ConfigUtils.inheritOverwritableProperty(hoursSpentLimit,
                parent.getHoursSpentLimit());
        daysSpentLimit = ConfigUtils.inheritOverwritableProperty(daysSpentLimit,
                parent.getDaysSpentLimit());
        return this;
    }

    private TerminationConfig inheritUnimprovedTimeSpentLimit(TerminationConfig parent) {
        unimprovedSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedSpentLimit,
                parent.getUnimprovedSpentLimit());
        unimprovedMillisecondsSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedMillisecondsSpentLimit,
                parent.getUnimprovedMillisecondsSpentLimit());
        unimprovedSecondsSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedSecondsSpentLimit,
                parent.getUnimprovedSecondsSpentLimit());
        unimprovedMinutesSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedMinutesSpentLimit,
                parent.getUnimprovedMinutesSpentLimit());
        unimprovedHoursSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedHoursSpentLimit,
                parent.getUnimprovedHoursSpentLimit());
        unimprovedDaysSpentLimit = ConfigUtils.inheritOverwritableProperty(unimprovedDaysSpentLimit,
                parent.getUnimprovedDaysSpentLimit());
        return this;
    }

    /**
     * Assert that the parameter is non-negative and return its value,
     * converting {@code null} to 0.
     * 
     * @param param the parameter to test/convert
     * @param name the name of the parameter, for use in the exception message
     * @throws IllegalArgumentException iff param is negative
     */
    private Long requireNonNegative(Long param, String name) {
        if (param == null) {
            return 0L; // Makes adding a null param a NOP.
        } else if (param < 0L) {
            String msg = String.format("The termination %s (%d) cannot be negative.", name, param);
            throw new IllegalArgumentException(msg);
        } else {
            return param;
        }
    }

    /** Check whether any ...SpentLimit is non-null. */
    private boolean timeSpentLimitIsSet() {
        return getDaysSpentLimit() != null
                || getHoursSpentLimit() != null
                || getMinutesSpentLimit() != null
                || getSecondsSpentLimit() != null
                || getMillisecondsSpentLimit() != null
                || getSpentLimit() != null;
    }

    /** Check whether any unimproved...SpentLimit is non-null. */
    private boolean unimprovedTimeSpentLimitIsSet() {
        return getUnimprovedDaysSpentLimit() != null
                || getUnimprovedHoursSpentLimit() != null
                || getUnimprovedMinutesSpentLimit() != null
                || getUnimprovedSecondsSpentLimit() != null
                || getUnimprovedMillisecondsSpentLimit() != null
                || getUnimprovedSpentLimit() != null;
    }

}
