/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.impl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.XStreamXmlSolverFactory;

import static org.junit.Assert.*;

public class XStreamXmlPlannerBenchmarkFactoryTest {

    @Test
    public void configFileRemainsSameAfterReadWrite() throws Exception {
        File originalConfigFile = new File("src/test/resources/org/optaplanner/benchmark/impl/testdataPlannerBenchmarkConfigXStream.xml");
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XStreamXmlPlannerBenchmarkFactory().configure(originalConfigFile);
        PlannerBenchmarkConfig benchmarkConfig = plannerBenchmarkFactory.getPlannerBenchmarkConfig();
        String savedXml = plannerBenchmarkFactory.getXStream().toXML(benchmarkConfig);
        String originalXml = FileUtils.readFileToString(originalConfigFile);
        assertEquals(originalXml, savedXml);
    }

    @Test
    public void configFileRemainsSameAfterReadWriteBuild() throws Exception {
        File originalConfigFile = new File("src/test/resources/org/optaplanner/benchmark/impl/testdataPlannerBenchmarkConfigXStream.xml");
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XStreamXmlPlannerBenchmarkFactory().configure(originalConfigFile);
        PlannerBenchmarkConfig benchmarkConfig = plannerBenchmarkFactory.getPlannerBenchmarkConfig();
        benchmarkConfig.buildPlannerBenchmark();
        String savedXml = plannerBenchmarkFactory.getXStream().toXML(benchmarkConfig);
        String originalXml = FileUtils.readFileToString(originalConfigFile);
        assertEquals(originalXml, savedXml);
    }

    @Test
    public void configFileRemainsSameAfterReadWriteWithInherited() throws Exception {
        File originalConfigFile = new File("src/test/resources/org/optaplanner/benchmark/impl/testdataPlannerBenchmarkConfigXStreamInherited.xml");
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XStreamXmlPlannerBenchmarkFactory().configure(originalConfigFile);
        PlannerBenchmarkConfig benchmarkConfig = plannerBenchmarkFactory.getPlannerBenchmarkConfig();
        String savedXml = plannerBenchmarkFactory.getXStream().toXML(benchmarkConfig);
        String originalXml = FileUtils.readFileToString(originalConfigFile);
        assertEquals(originalXml, savedXml);
    }

    @Test
    @Ignore("Config shouldn't actually remain the same: we inherited the subSingleCount")
    public void configFileRemainsSameAfterReadWriteBuildWithInherited() throws Exception {
        File originalConfigFile = new File("src/test/resources/org/optaplanner/benchmark/impl/testdataPlannerBenchmarkConfigXStreamInherited.xml");
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XStreamXmlPlannerBenchmarkFactory().configure(originalConfigFile);
        PlannerBenchmarkConfig benchmarkConfig = plannerBenchmarkFactory.getPlannerBenchmarkConfig();
        benchmarkConfig.buildPlannerBenchmark();
        String savedXml = plannerBenchmarkFactory.getXStream().toXML(benchmarkConfig);
        String originalXml = FileUtils.readFileToString(originalConfigFile);
        assertEquals(originalXml, savedXml);
    }
}
