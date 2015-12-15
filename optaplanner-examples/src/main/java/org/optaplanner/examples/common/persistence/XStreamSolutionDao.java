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

package org.optaplanner.examples.common.persistence;

import java.io.File;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public abstract class XStreamSolutionDao extends AbstractSolutionDao {

    protected XStreamSolutionFileIO xStreamSolutionFileIO;

    public XStreamSolutionDao(String dirName, Class... xStreamAnnotations) {
        super(dirName);
        xStreamSolutionFileIO = new XStreamSolutionFileIO(xStreamAnnotations);
    }

    public String getFileExtension() {
        return xStreamSolutionFileIO.getOutputFileExtension();
    }

    public Solution readSolution(File inputSolutionFile) {
        Solution solution = xStreamSolutionFileIO.read(inputSolutionFile);
        logger.info("Opened: {}", inputSolutionFile);
        return solution;
    }

    public void writeSolution(Solution solution, File outputSolutionFile) {
        xStreamSolutionFileIO.write(solution, outputSolutionFile);
        logger.info("Saved: {}", outputSolutionFile);
    }

}
