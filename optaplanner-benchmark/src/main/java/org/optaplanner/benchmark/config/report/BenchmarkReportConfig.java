package org.optaplanner.benchmark.config.report;

import java.util.Comparator;
import java.util.Locale;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.optaplanner.benchmark.config.ranking.SolverRankingType;
import org.optaplanner.benchmark.impl.ranking.SolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.io.jaxb.adapter.JaxbLocaleAdapter;

@XmlType(propOrder = {
        "locale",
        "solverRankingType",
        "solverRankingComparatorClass",
        "solverRankingWeightFactoryClass"
})
public class BenchmarkReportConfig extends AbstractConfig<BenchmarkReportConfig> {

    @XmlJavaTypeAdapter(JaxbLocaleAdapter.class)
    private Locale locale = null;
    private SolverRankingType solverRankingType = null;
    private Class<? extends Comparator<SolverBenchmarkResult>> solverRankingComparatorClass = null;
    private Class<? extends SolverRankingWeightFactory> solverRankingWeightFactoryClass = null;

    public BenchmarkReportConfig() {
    }

    public BenchmarkReportConfig(BenchmarkReportConfig inheritedConfig) {
        inherit(inheritedConfig);
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public SolverRankingType getSolverRankingType() {
        return solverRankingType;
    }

    public void setSolverRankingType(SolverRankingType solverRankingType) {
        this.solverRankingType = solverRankingType;
    }

    public Class<? extends Comparator<SolverBenchmarkResult>> getSolverRankingComparatorClass() {
        return solverRankingComparatorClass;
    }

    public void setSolverRankingComparatorClass(
            Class<? extends Comparator<SolverBenchmarkResult>> solverRankingComparatorClass) {
        this.solverRankingComparatorClass = solverRankingComparatorClass;
    }

    public Class<? extends SolverRankingWeightFactory> getSolverRankingWeightFactoryClass() {
        return solverRankingWeightFactoryClass;
    }

    public void setSolverRankingWeightFactoryClass(
            Class<? extends SolverRankingWeightFactory> solverRankingWeightFactoryClass) {
        this.solverRankingWeightFactoryClass = solverRankingWeightFactoryClass;
    }

    public Locale determineLocale() {
        return getLocale() == null ? Locale.getDefault() : getLocale();
    }

    @Override
    public BenchmarkReportConfig inherit(BenchmarkReportConfig inheritedConfig) {
        locale = ConfigUtils.inheritOverwritableProperty(locale, inheritedConfig.getLocale());
        solverRankingType = ConfigUtils.inheritOverwritableProperty(solverRankingType,
                inheritedConfig.getSolverRankingType());
        solverRankingComparatorClass = ConfigUtils.inheritOverwritableProperty(solverRankingComparatorClass,
                inheritedConfig.getSolverRankingComparatorClass());
        solverRankingWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(solverRankingWeightFactoryClass,
                inheritedConfig.getSolverRankingWeightFactoryClass());
        return this;
    }

    @Override
    public BenchmarkReportConfig copyConfig() {
        return new BenchmarkReportConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        classVisitor.accept(solverRankingComparatorClass);
        classVisitor.accept(solverRankingWeightFactoryClass);
    }

}
