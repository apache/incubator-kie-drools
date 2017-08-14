/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import org.apache.commons.io.FilenameUtils;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.impl.solver.XStreamXmlSolverFactory;

/**
 * @see PlannerBenchmarkFactory
 */
public class AbstractPlannerBenchmarkFactory extends PlannerBenchmarkFactory {

    protected final SolverConfigContext solverConfigContext;

    protected PlannerBenchmarkConfig plannerBenchmarkConfig = null;

    public AbstractPlannerBenchmarkFactory() {
        this(new SolverConfigContext());
    }

    /**
     * @param solverConfigContext never null
     */
    public AbstractPlannerBenchmarkFactory(SolverConfigContext solverConfigContext) {
        this.solverConfigContext = solverConfigContext;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public PlannerBenchmarkConfig getPlannerBenchmarkConfig() {
        checkPlannerBenchmarkConfigNotNull();
        return plannerBenchmarkConfig;
    }

    @Override
    public PlannerBenchmark buildPlannerBenchmark() {
        checkPlannerBenchmarkConfigNotNull();
        return plannerBenchmarkConfig.buildPlannerBenchmark(solverConfigContext);
    }

    @Override
    @SafeVarargs
    public final <Solution_> PlannerBenchmark buildPlannerBenchmark(Solution_... problems) {
        checkPlannerBenchmarkConfigNotNull();
        return plannerBenchmarkConfig.buildPlannerBenchmark(solverConfigContext, problems);
    }

    @Override
    public <Solution_> PlannerBenchmark buildPlannerBenchmark(List<Solution_> problemList) {
        checkPlannerBenchmarkConfigNotNull();
        return plannerBenchmarkConfig.buildPlannerBenchmark(solverConfigContext, problemList.toArray());
    }

    public void checkPlannerBenchmarkConfigNotNull() {
        if (plannerBenchmarkConfig == null) {
            throw new IllegalStateException("The plannerBenchmarkConfig (" + plannerBenchmarkConfig + ") is null," +
                    " call configure(...) first.");
        }
    }

}
