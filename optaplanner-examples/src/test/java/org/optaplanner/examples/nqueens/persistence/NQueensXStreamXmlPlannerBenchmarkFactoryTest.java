/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nqueens.persistence;

import java.io.IOException;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.benchmark.impl.XStreamXmlPlannerBenchmarkFactory;

import static org.junit.Assert.*;

public class NQueensXStreamXmlPlannerBenchmarkFactoryTest {

    @Test
    public void configFileRemainsSameAfterReadWrite() throws IOException {
        readWriteTest("nqueensSimpleBenchmarkConfig.xml");
    }

    @Test
    public void configFileRemainsSameAfterReadWriteBuild() throws IOException {
        readBuildWriteTest("nqueensSimpleBenchmarkConfig.xml");
    }

    @Test
    public void configFileRemainsSameAfterReadWriteWithInherited() throws IOException {
        readWriteTest("nqueensSimpleBenchmarkConfigInherited.xml");
    }

    @Test
    @Ignore("Config shouldn't actually remain the same: we inherited the subSingleCount, problemBenchmarks and more")
    public void configFileRemainsSameAfterReadWriteBuildWithInherited() throws IOException {
        readBuildWriteTest("nqueensSimpleBenchmarkConfigInherited.xml");
    }

    private XStreamXmlPlannerBenchmarkFactory createXStreamXmlPlannerBenchmarkFactory(String plannerBenchmarkConfigResource) throws IOException {
        InputStream originalConfigInputStream = getClass().getResourceAsStream(plannerBenchmarkConfigResource);
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XStreamXmlPlannerBenchmarkFactory().configure(originalConfigInputStream);
        plannerBenchmarkFactory.getXStream().setMode(XStream.NO_REFERENCES);
        return plannerBenchmarkFactory;
    }

    private void compareOutputToOriginal(XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory, String plannerBenchmarkConfigResource) throws IOException {
        String originalXml = IOUtils.toString(getClass().getResourceAsStream(plannerBenchmarkConfigResource), "UTF-8");
        String savedXml = plannerBenchmarkFactory.getXStream().toXML(plannerBenchmarkFactory.getPlannerBenchmarkConfig());
        assertEquals(originalXml, savedXml);
    }

    private void readWriteTest(String plannerBenchmarkConfigResource) throws IOException {
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = createXStreamXmlPlannerBenchmarkFactory(plannerBenchmarkConfigResource);
        compareOutputToOriginal(plannerBenchmarkFactory, plannerBenchmarkConfigResource);
    }

    private void readBuildWriteTest(String plannerBenchmarkConfigResource) throws IOException {
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = createXStreamXmlPlannerBenchmarkFactory(plannerBenchmarkConfigResource);
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().buildPlannerBenchmark();
        compareOutputToOriginal(plannerBenchmarkFactory, plannerBenchmarkConfigResource);
    }
}
