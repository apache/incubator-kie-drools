/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.benchmark.impl.aggregator;

import java.io.File;
import java.util.Arrays;


import org.optaplanner.benchmark.impl.aggregator.swingui.BenchmarkAggregatorFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkAggregatorApp {

    public static void main(String[] args) {
        BenchmarkAggregatorApp app = new BenchmarkAggregatorApp();
        File defaultBenchmarkDirectory;
        if (args.length == 0) {
            defaultBenchmarkDirectory = null;
        } else {
            if (args.length > 1) {
                throw new IllegalStateException("The program arguments (" + Arrays.toString(args)
                        + ") are invalid: only 1 program argument is supported.");
            }
            defaultBenchmarkDirectory = new File(args[0]);
            if (!defaultBenchmarkDirectory.exists()) {
                throw new IllegalArgumentException("The defaultBenchmarkDirectory (" + defaultBenchmarkDirectory
                        + ") does not exist.");
            }
        }
        app.init(defaultBenchmarkDirectory);
    }

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public BenchmarkAggregatorApp() {
    }

    public void init(File defaultBenchmarkDirectory) {
        BenchmarkAggregatorFrame benchmarkAggregatorFrame = new BenchmarkAggregatorFrame(defaultBenchmarkDirectory);
        benchmarkAggregatorFrame.init();
        benchmarkAggregatorFrame.setVisible(true);
    }

}
