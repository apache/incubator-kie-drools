/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.io;

import java.io.Reader;
import java.io.Writer;

import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.io.jaxb.ElementNamespaceOverride;
import org.optaplanner.core.impl.io.jaxb.GenericJaxbIO;
import org.optaplanner.core.impl.io.jaxb.JaxbIO;

public class PlannerBenchmarkConfigIO implements JaxbIO<PlannerBenchmarkConfig> {

    private final GenericJaxbIO<PlannerBenchmarkConfig> genericJaxbIO = new GenericJaxbIO<>(PlannerBenchmarkConfig.class);

    @Override
    public PlannerBenchmarkConfig read(Reader reader) {
        return genericJaxbIO.readOverridingNamespace(reader,
                ElementNamespaceOverride.of(PlannerBenchmarkConfig.XML_ELEMENT_NAME, SolverConfig.XML_NAMESPACE));
    }

    @Override
    public void write(PlannerBenchmarkConfig plannerBenchmarkConfig, Writer writer) {
        genericJaxbIO.writeWithoutNamespaces(plannerBenchmarkConfig, writer);
    }
}
