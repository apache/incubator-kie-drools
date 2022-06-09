package org.optaplanner.core.config.localsearch.decider.acceptor;

import java.util.List;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingType;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "acceptorTypeList",
        "entityTabuSize",
        "entityTabuRatio",
        "fadingEntityTabuSize",
        "fadingEntityTabuRatio",
        "valueTabuSize",
        "valueTabuRatio",
        "fadingValueTabuSize",
        "fadingValueTabuRatio",
        "moveTabuSize",
        "fadingMoveTabuSize",
        "undoMoveTabuSize",
        "fadingUndoMoveTabuSize",
        "simulatedAnnealingStartingTemperature",
        "lateAcceptanceSize",
        "greatDelugeWaterLevelIncrementScore",
        "greatDelugeWaterLevelIncrementRatio",
        "stepCountingHillClimbingSize",
        "stepCountingHillClimbingType"
})
public class LocalSearchAcceptorConfig extends AbstractConfig<LocalSearchAcceptorConfig> {

    @XmlElement(name = "acceptorType")
    private List<AcceptorType> acceptorTypeList = null;

    protected Integer entityTabuSize = null;
    protected Double entityTabuRatio = null;
    protected Integer fadingEntityTabuSize = null;
    protected Double fadingEntityTabuRatio = null;
    protected Integer valueTabuSize = null;
    protected Double valueTabuRatio = null;
    protected Integer fadingValueTabuSize = null;
    protected Double fadingValueTabuRatio = null;
    protected Integer moveTabuSize = null;
    protected Integer fadingMoveTabuSize = null;
    protected Integer undoMoveTabuSize = null;
    protected Integer fadingUndoMoveTabuSize = null;

    protected String simulatedAnnealingStartingTemperature = null;

    protected Integer lateAcceptanceSize = null;

    protected String greatDelugeWaterLevelIncrementScore = null;
    protected Double greatDelugeWaterLevelIncrementRatio = null;

    protected Integer stepCountingHillClimbingSize = null;
    protected StepCountingHillClimbingType stepCountingHillClimbingType = null;

    public List<AcceptorType> getAcceptorTypeList() {
        return acceptorTypeList;
    }

    public void setAcceptorTypeList(List<AcceptorType> acceptorTypeList) {
        this.acceptorTypeList = acceptorTypeList;
    }

    public Integer getEntityTabuSize() {
        return entityTabuSize;
    }

    public void setEntityTabuSize(Integer entityTabuSize) {
        this.entityTabuSize = entityTabuSize;
    }

    public Double getEntityTabuRatio() {
        return entityTabuRatio;
    }

    public void setEntityTabuRatio(Double entityTabuRatio) {
        this.entityTabuRatio = entityTabuRatio;
    }

    public Integer getFadingEntityTabuSize() {
        return fadingEntityTabuSize;
    }

    public void setFadingEntityTabuSize(Integer fadingEntityTabuSize) {
        this.fadingEntityTabuSize = fadingEntityTabuSize;
    }

    public Double getFadingEntityTabuRatio() {
        return fadingEntityTabuRatio;
    }

    public void setFadingEntityTabuRatio(Double fadingEntityTabuRatio) {
        this.fadingEntityTabuRatio = fadingEntityTabuRatio;
    }

    public Integer getValueTabuSize() {
        return valueTabuSize;
    }

    public void setValueTabuSize(Integer valueTabuSize) {
        this.valueTabuSize = valueTabuSize;
    }

    public Double getValueTabuRatio() {
        return valueTabuRatio;
    }

    public void setValueTabuRatio(Double valueTabuRatio) {
        this.valueTabuRatio = valueTabuRatio;
    }

    public Integer getFadingValueTabuSize() {
        return fadingValueTabuSize;
    }

    public void setFadingValueTabuSize(Integer fadingValueTabuSize) {
        this.fadingValueTabuSize = fadingValueTabuSize;
    }

    public Double getFadingValueTabuRatio() {
        return fadingValueTabuRatio;
    }

    public void setFadingValueTabuRatio(Double fadingValueTabuRatio) {
        this.fadingValueTabuRatio = fadingValueTabuRatio;
    }

    public Integer getMoveTabuSize() {
        return moveTabuSize;
    }

    public void setMoveTabuSize(Integer moveTabuSize) {
        this.moveTabuSize = moveTabuSize;
    }

    public Integer getFadingMoveTabuSize() {
        return fadingMoveTabuSize;
    }

    public void setFadingMoveTabuSize(Integer fadingMoveTabuSize) {
        this.fadingMoveTabuSize = fadingMoveTabuSize;
    }

    public Integer getUndoMoveTabuSize() {
        return undoMoveTabuSize;
    }

    public void setUndoMoveTabuSize(Integer undoMoveTabuSize) {
        this.undoMoveTabuSize = undoMoveTabuSize;
    }

    public Integer getFadingUndoMoveTabuSize() {
        return fadingUndoMoveTabuSize;
    }

    public void setFadingUndoMoveTabuSize(Integer fadingUndoMoveTabuSize) {
        this.fadingUndoMoveTabuSize = fadingUndoMoveTabuSize;
    }

    public String getSimulatedAnnealingStartingTemperature() {
        return simulatedAnnealingStartingTemperature;
    }

    public void setSimulatedAnnealingStartingTemperature(String simulatedAnnealingStartingTemperature) {
        this.simulatedAnnealingStartingTemperature = simulatedAnnealingStartingTemperature;
    }

    public Integer getLateAcceptanceSize() {
        return lateAcceptanceSize;
    }

    public void setLateAcceptanceSize(Integer lateAcceptanceSize) {
        this.lateAcceptanceSize = lateAcceptanceSize;
    }

    public String getGreatDelugeWaterLevelIncrementScore() {
        return greatDelugeWaterLevelIncrementScore;
    }

    public void setGreatDelugeWaterLevelIncrementScore(String greatDelugeWaterLevelIncrementScore) {
        this.greatDelugeWaterLevelIncrementScore = greatDelugeWaterLevelIncrementScore;
    }

    public Double getGreatDelugeWaterLevelIncrementRatio() {
        return greatDelugeWaterLevelIncrementRatio;
    }

    public void setGreatDelugeWaterLevelIncrementRatio(Double greatDelugeWaterLevelIncrementRatio) {
        this.greatDelugeWaterLevelIncrementRatio = greatDelugeWaterLevelIncrementRatio;
    }

    public Integer getStepCountingHillClimbingSize() {
        return stepCountingHillClimbingSize;
    }

    public void setStepCountingHillClimbingSize(Integer stepCountingHillClimbingSize) {
        this.stepCountingHillClimbingSize = stepCountingHillClimbingSize;
    }

    public StepCountingHillClimbingType getStepCountingHillClimbingType() {
        return stepCountingHillClimbingType;
    }

    public void setStepCountingHillClimbingType(StepCountingHillClimbingType stepCountingHillClimbingType) {
        this.stepCountingHillClimbingType = stepCountingHillClimbingType;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public LocalSearchAcceptorConfig withAcceptorTypeList(List<AcceptorType> acceptorTypeList) {
        this.acceptorTypeList = acceptorTypeList;
        return this;
    }

    public LocalSearchAcceptorConfig withEntityTabuSize(Integer entityTabuSize) {
        this.entityTabuSize = entityTabuSize;
        return this;
    }

    public LocalSearchAcceptorConfig withEntityTabuRatio(Double entityTabuRatio) {
        this.entityTabuRatio = entityTabuRatio;
        return this;
    }

    public LocalSearchAcceptorConfig withFadingEntityTabuSize(Integer fadingEntityTabuSize) {
        this.fadingEntityTabuSize = fadingEntityTabuSize;
        return this;
    }

    public LocalSearchAcceptorConfig withFadingEntityTabuRatio(Double fadingEntityTabuRatio) {
        this.fadingEntityTabuRatio = fadingEntityTabuRatio;
        return this;
    }

    public LocalSearchAcceptorConfig withValueTabuSize(Integer valueTabuSize) {
        this.valueTabuSize = valueTabuSize;
        return this;
    }

    public LocalSearchAcceptorConfig withValueTabuRatio(Double valueTabuRatio) {
        this.valueTabuRatio = valueTabuRatio;
        return this;
    }

    public LocalSearchAcceptorConfig withFadingValueTabuSize(Integer fadingValueTabuSize) {
        this.fadingValueTabuSize = fadingValueTabuSize;
        return this;
    }

    public LocalSearchAcceptorConfig withFadingValueTabuRatio(Double fadingValueTabuRatio) {
        this.fadingValueTabuRatio = fadingValueTabuRatio;
        return this;
    }

    public LocalSearchAcceptorConfig withMoveTabuSize(Integer moveTabuSize) {
        this.moveTabuSize = moveTabuSize;
        return this;
    }

    public LocalSearchAcceptorConfig withFadingMoveTabuSize(Integer fadingMoveTabuSize) {
        this.fadingMoveTabuSize = fadingMoveTabuSize;
        return this;
    }

    public LocalSearchAcceptorConfig withUndoMoveTabuSize(Integer undoMoveTabuSize) {
        this.undoMoveTabuSize = undoMoveTabuSize;
        return this;
    }

    public LocalSearchAcceptorConfig withFadingUndoMoveTabuSize(Integer fadingUndoMoveTabuSize) {
        this.fadingUndoMoveTabuSize = fadingUndoMoveTabuSize;
        return this;
    }

    public LocalSearchAcceptorConfig withSimulatedAnnealingStartingTemperature(String simulatedAnnealingStartingTemperature) {
        this.simulatedAnnealingStartingTemperature = simulatedAnnealingStartingTemperature;
        return this;
    }

    public LocalSearchAcceptorConfig withLateAcceptanceSize(Integer lateAcceptanceSize) {
        this.lateAcceptanceSize = lateAcceptanceSize;
        return this;
    }

    public LocalSearchAcceptorConfig withStepCountingHillClimbingSize(Integer stepCountingHillClimbingSize) {
        this.stepCountingHillClimbingSize = stepCountingHillClimbingSize;
        return this;
    }

    public LocalSearchAcceptorConfig
            withStepCountingHillClimbingType(StepCountingHillClimbingType stepCountingHillClimbingType) {
        this.stepCountingHillClimbingType = stepCountingHillClimbingType;
        return this;
    }

    @Override
    public LocalSearchAcceptorConfig inherit(LocalSearchAcceptorConfig inheritedConfig) {
        if (acceptorTypeList == null) {
            acceptorTypeList = inheritedConfig.getAcceptorTypeList();
        } else {
            List<AcceptorType> inheritedAcceptorTypeList = inheritedConfig.getAcceptorTypeList();
            if (inheritedAcceptorTypeList != null) {
                for (AcceptorType acceptorType : inheritedAcceptorTypeList) {
                    if (!acceptorTypeList.contains(acceptorType)) {
                        acceptorTypeList.add(acceptorType);
                    }
                }
            }
        }
        entityTabuSize = ConfigUtils.inheritOverwritableProperty(entityTabuSize, inheritedConfig.getEntityTabuSize());
        entityTabuRatio = ConfigUtils.inheritOverwritableProperty(entityTabuRatio, inheritedConfig.getEntityTabuRatio());
        fadingEntityTabuSize = ConfigUtils.inheritOverwritableProperty(fadingEntityTabuSize,
                inheritedConfig.getFadingEntityTabuSize());
        fadingEntityTabuRatio = ConfigUtils.inheritOverwritableProperty(fadingEntityTabuRatio,
                inheritedConfig.getFadingEntityTabuRatio());
        valueTabuSize = ConfigUtils.inheritOverwritableProperty(valueTabuSize, inheritedConfig.getValueTabuSize());
        valueTabuRatio = ConfigUtils.inheritOverwritableProperty(valueTabuRatio, inheritedConfig.getValueTabuRatio());
        fadingValueTabuSize = ConfigUtils.inheritOverwritableProperty(fadingValueTabuSize,
                inheritedConfig.getFadingValueTabuSize());
        fadingValueTabuRatio = ConfigUtils.inheritOverwritableProperty(fadingValueTabuRatio,
                inheritedConfig.getFadingValueTabuRatio());
        moveTabuSize = ConfigUtils.inheritOverwritableProperty(moveTabuSize, inheritedConfig.getMoveTabuSize());
        fadingMoveTabuSize = ConfigUtils.inheritOverwritableProperty(fadingMoveTabuSize,
                inheritedConfig.getFadingMoveTabuSize());
        undoMoveTabuSize = ConfigUtils.inheritOverwritableProperty(undoMoveTabuSize,
                inheritedConfig.getUndoMoveTabuSize());
        fadingUndoMoveTabuSize = ConfigUtils.inheritOverwritableProperty(fadingUndoMoveTabuSize,
                inheritedConfig.getFadingUndoMoveTabuSize());
        simulatedAnnealingStartingTemperature = ConfigUtils.inheritOverwritableProperty(
                simulatedAnnealingStartingTemperature, inheritedConfig.getSimulatedAnnealingStartingTemperature());
        lateAcceptanceSize = ConfigUtils.inheritOverwritableProperty(lateAcceptanceSize,
                inheritedConfig.getLateAcceptanceSize());
        greatDelugeWaterLevelIncrementScore = ConfigUtils.inheritOverwritableProperty(greatDelugeWaterLevelIncrementScore,
                inheritedConfig.getGreatDelugeWaterLevelIncrementScore());
        greatDelugeWaterLevelIncrementRatio = ConfigUtils.inheritOverwritableProperty(greatDelugeWaterLevelIncrementRatio,
                inheritedConfig.getGreatDelugeWaterLevelIncrementRatio());
        stepCountingHillClimbingSize = ConfigUtils.inheritOverwritableProperty(stepCountingHillClimbingSize,
                inheritedConfig.getStepCountingHillClimbingSize());
        stepCountingHillClimbingType = ConfigUtils.inheritOverwritableProperty(stepCountingHillClimbingType,
                inheritedConfig.getStepCountingHillClimbingType());
        return this;
    }

    @Override
    public LocalSearchAcceptorConfig copyConfig() {
        return new LocalSearchAcceptorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        // No referenced classes
    }

}
