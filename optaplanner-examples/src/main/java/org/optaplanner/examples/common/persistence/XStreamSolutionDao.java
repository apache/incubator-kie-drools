/*
 * Copyright 2010 JBoss Inc
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

import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.persistence.xstream.XStreamProblemIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XStreamSolutionDao implements SolutionDao {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected XStreamProblemIO xStreamProblemIO;
    protected String dirName;
    protected File dataDir;

    public XStreamSolutionDao(String dirName, Class... xstreamAnnotations) {
        this.dirName = dirName;
        dataDir = new File("data/" + dirName);
        xStreamProblemIO = new XStreamProblemIO(xstreamAnnotations);
        if (!dataDir.exists()) {
            throw new IllegalStateException("The directory dataDir (" + dataDir.getAbsolutePath()
                    + ") does not exist.\n" +
                    " The working directory should be set to the directory that contains the data directory" +
                    " (which is not the data directory itself).\n" +
                    " The working directory is different in a git clone (optaplanner/optaplanner-examples)" +
                    " and the release zip (examples).\n" +
                    " In an IDE (IntelliJ, Eclipse, NetBeans), open the \"Run configuration\""
                    + " to change the field \"Working directory\".");
        }
    }

    public String getDirName() {
        return dirName;
    }

    public File getDataDir() {
        return dataDir;
    }

    public String getFileExtension() {
        return "xml";
    }

    public Solution readSolution(File inputSolutionFile) {
        Solution solution = xStreamProblemIO.read(inputSolutionFile);
        logger.info("Opened: {}", inputSolutionFile);
        return solution;
    }

    public void writeSolution(Solution solution, File outputSolutionFile) {
        xStreamProblemIO.write(solution, outputSolutionFile);
        logger.info("Saved: {}", outputSolutionFile);
    }

}
