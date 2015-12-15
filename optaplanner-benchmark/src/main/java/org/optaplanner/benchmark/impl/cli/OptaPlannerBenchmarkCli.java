/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.cli;

import java.io.File;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;

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
        PlannerBenchmarkFactory plannerBenchmarkFactory;
        if (benchmarkConfigFile.getName().endsWith(".ftl")) {
            plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlFile(benchmarkConfigFile);
        } else {
            plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlFile(benchmarkConfigFile);
        }
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().setBenchmarkDirectory(benchmarkDirectory);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        plannerBenchmark.benchmark();
    }

}
