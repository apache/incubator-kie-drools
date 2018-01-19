/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.app;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.aggregator.swingui.BenchmarkAggregatorFrame;

public abstract class CommonBenchmarkApp extends LoggingMain {

    public static final String AGGREGATOR_ARG = "--aggregator";

    private final Map<String, ArgOption> benchmarkArgumentMap;

    public CommonBenchmarkApp(ArgOption... argOptions) {
        benchmarkArgumentMap = new LinkedHashMap<>(argOptions.length);
        for (ArgOption argOption : argOptions) {
            benchmarkArgumentMap.put(argOption.getName(), argOption);
        }
    }

    public Collection<ArgOption> getArgOptions() {
        return benchmarkArgumentMap.values();
    }

    public void buildAndBenchmark(String[] args) {
        boolean aggregator = false;
        ArgOption argOption = null;
        for (String arg : args) {
            if (arg.equalsIgnoreCase(AGGREGATOR_ARG)) {
                aggregator = true;
            } else if (benchmarkArgumentMap.containsKey(arg)) {
                if (argOption != null) {
                    throw new IllegalArgumentException("The args (" + Arrays.toString(args)
                            + ") contains arg name (" + argOption.getName() + ") and arg name (" + arg + ").");
                }
                argOption = benchmarkArgumentMap.get(arg);
            } else {
                throw new IllegalArgumentException("The args (" + Arrays.toString(args)
                        + ") contains an arg (" + arg + ") which is not part of the recognized args ("
                        + benchmarkArgumentMap.keySet() + " or " + AGGREGATOR_ARG + ").");
            }
        }
        if (argOption == null) {
            argOption = benchmarkArgumentMap.values().iterator().next();
        }
        PlannerBenchmarkFactory plannerBenchmarkFactory = argOption.buildPlannerBenchmarkFactory();
        if (!aggregator) {
            PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
            plannerBenchmark.benchmarkAndShowReportInBrowser();
        } else {
            BenchmarkAggregatorFrame.createAndDisplay(plannerBenchmarkFactory);
        }
    }

    public static class ArgOption {

        private String name;
        private String benchmarkConfig;
        private boolean template;

        public ArgOption(String name, String benchmarkConfig) {
            this(name, benchmarkConfig, false);
        }

        public ArgOption(String name, String benchmarkConfig, boolean template) {
            this.name = name;
            this.benchmarkConfig = benchmarkConfig;
            this.template = template;
        }

        public String getName() {
            return name;
        }

        public String getBenchmarkConfig() {
            return benchmarkConfig;
        }

        public boolean isTemplate() {
            return template;
        }

        public PlannerBenchmarkFactory buildPlannerBenchmarkFactory() {
            if (!template) {
                return PlannerBenchmarkFactory.createFromXmlResource(benchmarkConfig);
            } else {
                return PlannerBenchmarkFactory.createFromFreemarkerXmlResource(benchmarkConfig);
            }
        }

        @Override
        public String toString() {
            return name + " (" + benchmarkConfig + ")";
        }

    }

}
