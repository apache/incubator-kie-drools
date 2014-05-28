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

package org.optaplanner.examples.common.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.examples.common.business.ProblemFileComparator;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.vehiclerouting.app.VehicleRoutingBenchmarkApp;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public abstract class PlannerBenchmarkConfigTest {

    protected static Collection<Object[]> getArgOptionsAsParameters(CommonBenchmarkApp benchmarkApp) {
        List<Object[]> filesAsParameters = new ArrayList<Object[]>();
        for (CommonBenchmarkApp.ArgOption argOption : benchmarkApp.getArgOptions()) {
            filesAsParameters.add(new Object[]{argOption});
        }
        return filesAsParameters;
    }

    protected CommonBenchmarkApp.ArgOption argOption;

    protected PlannerBenchmarkConfigTest(CommonBenchmarkApp.ArgOption argOption) {
        this.argOption = argOption;
    }

    @Test
    public void buildPlannerBenchmark() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = argOption.buildPlannerBenchmarkFactory();
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
    }

}
