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

package org.drools.planner.examples.nurserostering.competition;

import java.io.File;
import java.util.Arrays;

import org.drools.planner.config.XmlSolverConfigurer;
import org.drools.planner.config.localsearch.LocalSearchSolverConfig;
import org.drools.planner.core.Solver;
import org.drools.planner.examples.common.app.LoggingMain;
import org.drools.planner.examples.nurserostering.domain.NurseRoster;
import org.drools.planner.examples.nurserostering.persistence.NurseRosteringSolutionExporter;
import org.drools.planner.examples.nurserostering.persistence.NurseRosteringSolutionImporter;

public class NurseRosteringCompetitionSprintApp extends LoggingMain {

    public static final String SOLVER_CONFIG
            = "/org/drools/planner/examples/nurserostering/competition/nurseRosteringCompetitionSprintSolverConfig.xml";
    private static final int DEFAULT_TIME_SECONDS_SPEND = 10;
    private static final String INPUT_FILE_NAME_PREFIX = "sprint";

    public static void main(String[] args) {
        long maximumSecondsSpend;
        if (args.length > 1) {
            throw new IllegalArgumentException("Only one argument (maximumSecondsSpend) is allowed.");
        } else if (args.length == 1) {
            try {
                maximumSecondsSpend = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The argument maximumSecondsSpend (" + args[0]
                        + ") is not a number.");
            }
        } else {
            maximumSecondsSpend = DEFAULT_TIME_SECONDS_SPEND;
        }
        new NurseRosteringCompetitionSprintApp(maximumSecondsSpend).solve();
    }

    protected NurseRosteringSolutionImporter importer;
    protected NurseRosteringSolutionExporter exporter;
    protected Solver solver;
    protected File inputDir;
    protected File outputDir;

    public NurseRosteringCompetitionSprintApp(long maximumSecondsSpend) {
        super();
        inputDir = new File("input");
        if (!inputDir.exists()) {
            throw new IllegalArgumentException("The input directory  (" + inputDir + ") does not exist.");
        }
        outputDir = new File("output");
        if (!outputDir.exists()) {
            throw new IllegalArgumentException("The output directory (" + outputDir + ") does not exist.");
        }
        importer = new NurseRosteringSolutionImporter();
        exporter = new NurseRosteringSolutionExporter();
        
        XmlSolverConfigurer configurer = new XmlSolverConfigurer();
        configurer.configure(SOLVER_CONFIG);
        ((LocalSearchSolverConfig) configurer.getConfig()).getTerminationConfig()
                .setMaximumSecondsSpend(maximumSecondsSpend);
        solver = configurer.buildSolver();
    }

    private void solve() {
        File[] inputFiles = inputDir.listFiles();
        if (inputFiles == null) {
            throw new IllegalArgumentException("The input directory  (" + inputDir + ") does not exist.");
        }
        Arrays.sort(inputFiles);
        for (File inputFile : inputFiles) {
            String inputFileName = inputFile.getName();
            if (inputFileName.toLowerCase().endsWith(".xml") && inputFileName.toLowerCase().startsWith(INPUT_FILE_NAME_PREFIX)) {
                logger.info("Solving {}", inputFileName);
                NurseRoster startingSolution = (NurseRoster) importer.readSolution(inputFile);
                solver.setStartingSolution(startingSolution);
                solver.solve();
                NurseRoster bestSolution = (NurseRoster) solver.getBestSolution();
                File outputFile = new File(outputDir, inputFileName.substring(0, inputFileName.length() - 4)
                        + "_geoffrey_de_smet.xml");
                exporter.writeSolution(bestSolution, outputFile);
            }
        }
        logger.info("Done");
    }

}
