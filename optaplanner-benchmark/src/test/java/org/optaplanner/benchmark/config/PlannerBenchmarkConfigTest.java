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

package org.optaplanner.benchmark.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.solver.io.XStreamConfigReader;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.junit.Assert.*;

public class PlannerBenchmarkConfigTest {

    @Test
    public void validNameWithUnderscoreAndSpace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Valid_name with space_and_underscore");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        config.validate();
    }

    @Test
    public void validNameWithJapanese() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Valid name (有効名 in Japanese)");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        config.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidNameWithSlash() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("slash/name");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        config.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidNameWithSuffixWhitespace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Suffixed with space ");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        config.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidNameWithPrefixWhitespace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName(" prefixed with space");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        config.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noSolverConfigs() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setSolverBenchmarkConfigList(null);
        config.setSolverBenchmarkBluePrintConfigList(null);
        config.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void nonUniqueSolverConfigName() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        final String sbcName = "x";
        SolverBenchmarkConfig sbc1 = new SolverBenchmarkConfig();
        sbc1.setName(sbcName);
        SolverBenchmarkConfig sbc2 = new SolverBenchmarkConfig();
        sbc2.setName(sbcName);
        config.setSolverBenchmarkConfigList(Arrays.asList(sbc1, sbc2));
        config.generateSolverBenchmarkConfigNames();
    }

    @Test
    public void uniqueNamesGenerated() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        SolverBenchmarkConfig sbc1 = new SolverBenchmarkConfig();
        SolverBenchmarkConfig sbc2 = new SolverBenchmarkConfig();
        SolverBenchmarkConfig sbc3 = new SolverBenchmarkConfig();
        sbc3.setName("Config_1");
        List<SolverBenchmarkConfig> configs = Arrays.asList(sbc1, sbc2, sbc3);
        config.setSolverBenchmarkConfigList(configs);
        config.generateSolverBenchmarkConfigNames();
        assertEquals("Config_1", sbc3.getName());
        TreeSet<String> names = new TreeSet<>();
        for (SolverBenchmarkConfig sc : configs) {
            names.add(sc.getName());
        }
        for (int i = 0; i < configs.size(); i++) {
            assertTrue(names.contains("Config_" + i));
        }
    }

    @Test
    public void resolveParallelBenchmarkCountAutomatically() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        assertEquals(1, config.resolveParallelBenchmarkCountAutomatically(-1));
        assertEquals(1, config.resolveParallelBenchmarkCountAutomatically(0));
        assertEquals(1, config.resolveParallelBenchmarkCountAutomatically(1));
        assertEquals(1, config.resolveParallelBenchmarkCountAutomatically(2));
        assertEquals(2, config.resolveParallelBenchmarkCountAutomatically(3));
        assertEquals(2, config.resolveParallelBenchmarkCountAutomatically(4));
        assertEquals(3, config.resolveParallelBenchmarkCountAutomatically(5));
        assertEquals(4, config.resolveParallelBenchmarkCountAutomatically(6));
        assertEquals(9, config.resolveParallelBenchmarkCountAutomatically(17));
    }

    @Test
    public void resolveParallelBenchmarkCountFromFormula() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setParallelBenchmarkCount(ConfigUtils.AVAILABLE_PROCESSOR_COUNT + "+1");
        // resolved benchmark count cannot be higher than available processors
        assertEquals(Runtime.getRuntime().availableProcessors(), config.resolveParallelBenchmarkCount());
    }

    @Test
    public void parallelBenchmarkDisabledByDefault() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        assertEquals(1, config.resolveParallelBenchmarkCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolvedParallelBenchmarkCountNegative() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setParallelBenchmarkCount("-1");
        config.resolveParallelBenchmarkCount();
    }

    @Test
    public void calculateWarmUpTimeMillisSpentLimit() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setWarmUpHoursSpentLimit(1L);
        config.setWarmUpMinutesSpentLimit(2L);
        config.setWarmUpSecondsSpentLimit(5L);
        config.setWarmUpMillisecondsSpentLimit(753L);
        assertEquals(3_725_753L, (long) config.calculateWarmUpTimeMillisSpentLimit());
    }

    @Test
    public void xmlConfigFileRemainsSameAfterReadWrite() throws IOException {
        String benchmarkConfigResource = "org/optaplanner/benchmark/config/testdataBenchmarkConfigNoInheritence.xml";
        String originalXml = IOUtils.toString(
                getClass().getClassLoader().getResourceAsStream(benchmarkConfigResource), StandardCharsets.UTF_8);
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(benchmarkConfigResource);
        assertNotNull(PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark(new TestdataSolution()));
        XStream xStream = XStreamConfigReader.buildXStreamPortable(getClass().getClassLoader(), PlannerBenchmarkConfig.class);
        xStream.setMode(XStream.NO_REFERENCES);
        String savedXml = xStream.toXML(benchmarkConfig);
        assertEquals(originalXml.trim(), savedXml.trim());
    }

}
