/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.tsp.persistence;

import java.io.File;

import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class TspFileIO implements SolutionFileIO<TspSolution> {

    private TspImporter importer = new TspImporter();
    private TspExporter exporter = new TspExporter();

    @Override
    public String getInputFileExtension() {
        return TspImporter.INPUT_FILE_SUFFIX;
    }

    @Override
    public String getOutputFileExtension() {
        return TspExporter.OUTPUT_FILE_SUFFIX;
    }

    @Override
    public TspSolution read(File inputSolutionFile) {
        return importer.readSolution(inputSolutionFile);
    }

    @Override
    public void write(TspSolution solution, File outputSolutionFile) {
        exporter.writeSolution(solution, outputSolutionFile);
    }

}
