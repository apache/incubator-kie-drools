package org.optaplanner.benchmark.impl.cli;

import java.io.File;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;

/**
 * Run this class from the command line interface
 * to run a benchmarkConfigFile directly (using the normal classpath from the JVM).
 */
public class OptaPlannerBenchmarkCli {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: OptaPlannerBenchmarkCli benchmarkConfigFile benchmarkDirectory");
            System.exit(1);
        }
        File benchmarkConfigFile = new File(args[0]);
        if (!benchmarkConfigFile.exists()) {
            System.err.println("The benchmarkConfigFile (" + benchmarkConfigFile + ") does not exist.");
            System.exit(1);
        }
        File benchmarkDirectory = new File(args[1]);
        PlannerBenchmarkConfig benchmarkConfig;
        if (benchmarkConfigFile.getName().endsWith(".ftl")) {
            benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlFile(benchmarkConfigFile);
        } else {
            benchmarkConfig = PlannerBenchmarkConfig.createFromXmlFile(benchmarkConfigFile);
        }
        benchmarkConfig.setBenchmarkDirectory(benchmarkDirectory);
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(benchmarkConfig);
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
        benchmark.benchmark();
    }

}
