/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.app;

import org.drools.planner.examples.common.app.CommonBenchmarkApp;

public class GeneralDroolsPlannerBenchmarkApp extends CommonBenchmarkApp {

    public static final String DEFAULT_BENCHMARK_CONFIG
            = "/org/drools/planner/examples/app/benchmark/generalDroolsPlannerBenchmarkConfig.xml";

    public static void main(String[] args) {
        String benchmarkConfig = DEFAULT_BENCHMARK_CONFIG;
        new GeneralDroolsPlannerBenchmarkApp().buildAndBenchmark(benchmarkConfig);
    }

}
