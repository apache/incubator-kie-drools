package org.optaplanner.benchmark.config;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

@XmlType(propOrder = {
        "solutionFileIOClass",
        "writeOutputSolutionEnabled",
        "inputSolutionFileList",
        "problemStatisticEnabled",
        "problemStatisticTypeList",
        "singleStatisticTypeList"
})
public class ProblemBenchmarksConfig extends AbstractConfig<ProblemBenchmarksConfig> {

    private Class<SolutionFileIO<?>> solutionFileIOClass = null;

    private Boolean writeOutputSolutionEnabled = null;

    @XmlElement(name = "inputSolutionFile")
    private List<File> inputSolutionFileList = null;

    private Boolean problemStatisticEnabled = null;

    @XmlElement(name = "problemStatisticType")
    private List<ProblemStatisticType> problemStatisticTypeList = null;

    @XmlElement(name = "singleStatisticType")
    private List<SingleStatisticType> singleStatisticTypeList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public Class<SolutionFileIO<?>> getSolutionFileIOClass() {
        return solutionFileIOClass;
    }

    public void setSolutionFileIOClass(Class<SolutionFileIO<?>> solutionFileIOClass) {
        this.solutionFileIOClass = solutionFileIOClass;
    }

    public Boolean getWriteOutputSolutionEnabled() {
        return writeOutputSolutionEnabled;
    }

    public void setWriteOutputSolutionEnabled(Boolean writeOutputSolutionEnabled) {
        this.writeOutputSolutionEnabled = writeOutputSolutionEnabled;
    }

    public List<File> getInputSolutionFileList() {
        return inputSolutionFileList;
    }

    public void setInputSolutionFileList(List<File> inputSolutionFileList) {
        this.inputSolutionFileList = inputSolutionFileList;
    }

    public Boolean getProblemStatisticEnabled() {
        return problemStatisticEnabled;
    }

    public void setProblemStatisticEnabled(Boolean problemStatisticEnabled) {
        this.problemStatisticEnabled = problemStatisticEnabled;
    }

    public List<ProblemStatisticType> getProblemStatisticTypeList() {
        return problemStatisticTypeList;
    }

    public void setProblemStatisticTypeList(List<ProblemStatisticType> problemStatisticTypeList) {
        this.problemStatisticTypeList = problemStatisticTypeList;
    }

    public List<SingleStatisticType> getSingleStatisticTypeList() {
        return singleStatisticTypeList;
    }

    public void setSingleStatisticTypeList(List<SingleStatisticType> singleStatisticTypeList) {
        this.singleStatisticTypeList = singleStatisticTypeList;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * Return the problem statistic type list, or a list containing default metrics if problemStatisticEnabled
     * is not false. If problemStatisticEnabled is false, an empty list is returned.
     *
     * @return never null
     */
    public List<ProblemStatisticType> determineProblemStatisticTypeList() {
        if (problemStatisticEnabled != null && !problemStatisticEnabled) {
            return Collections.emptyList();
        }

        if (problemStatisticTypeList == null || problemStatisticTypeList.isEmpty()) {
            return ProblemStatisticType.defaultList();
        }

        return problemStatisticTypeList;
    }

    /**
     * Return the single statistic type list, or an empty list if it is null
     *
     * @return never null
     */
    public List<SingleStatisticType> determineSingleStatisticTypeList() {
        return Objects.requireNonNullElse(singleStatisticTypeList, Collections.emptyList());
    }

    @Override
    public ProblemBenchmarksConfig inherit(ProblemBenchmarksConfig inheritedConfig) {
        solutionFileIOClass = ConfigUtils.inheritOverwritableProperty(solutionFileIOClass,
                inheritedConfig.getSolutionFileIOClass());
        writeOutputSolutionEnabled = ConfigUtils.inheritOverwritableProperty(writeOutputSolutionEnabled,
                inheritedConfig.getWriteOutputSolutionEnabled());
        inputSolutionFileList = ConfigUtils.inheritMergeableListProperty(inputSolutionFileList,
                inheritedConfig.getInputSolutionFileList());
        problemStatisticEnabled = ConfigUtils.inheritOverwritableProperty(problemStatisticEnabled,
                inheritedConfig.getProblemStatisticEnabled());
        problemStatisticTypeList = ConfigUtils.inheritMergeableListProperty(problemStatisticTypeList,
                inheritedConfig.getProblemStatisticTypeList());
        singleStatisticTypeList = ConfigUtils.inheritMergeableListProperty(singleStatisticTypeList,
                inheritedConfig.getSingleStatisticTypeList());
        return this;
    }

    @Override
    public ProblemBenchmarksConfig copyConfig() {
        return new ProblemBenchmarksConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        classVisitor.accept(solutionFileIOClass);
    }

}
