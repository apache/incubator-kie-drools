/**
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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.drools.planner.core.score.HardAndSoftScore;
import org.drools.planner.examples.common.app.LoggingMain;
import org.drools.planner.examples.common.business.SolutionBusiness;
import org.drools.planner.examples.nurserostering.app.NurseRosteringApp;

/**
 * @author Geoffrey De Smet
 */
public class NurseRosteringEvaluatorHelper extends LoggingMain {

    private static final boolean ALL_INPUT_FILES = true;
    private static final String INPUT_FILE_PREFIX = "long_late04";
    private static final String OUTPUT_FILE_SUFFIX = "_geoffrey_de_smet";
    private static final String DEFAULT_LINE_CONTAINS_FILTER = null;

    public static void main(String[] args) {
        String lineContainsFilter;
        if (args.length > 0) {
            lineContainsFilter = args[0];
        } else {
            lineContainsFilter = DEFAULT_LINE_CONTAINS_FILTER;
        }
        NurseRosteringEvaluatorHelper helper = new NurseRosteringEvaluatorHelper();
        if (!ALL_INPUT_FILES) {
            helper.evaluate(INPUT_FILE_PREFIX, OUTPUT_FILE_SUFFIX, lineContainsFilter);
        } else {
            File inputDir = helper.getImportDir();
            File[] inputFiles = inputDir.listFiles();
            if (inputFiles == null) {
                throw new IllegalArgumentException(
                        "Your working dir should be drools-planner-examples and contain: " + inputDir);
            }
            Arrays.sort(inputFiles);
            for (File inputFile : inputFiles) {
                String inputFileName = inputFile.getName();
                if (inputFileName.endsWith(".xml")) {
                    String filePrefix = inputFileName.substring(0, inputFileName.lastIndexOf(".xml"));
                    helper.evaluate(filePrefix, OUTPUT_FILE_SUFFIX, lineContainsFilter);
                }
            }
        }
    }
    
    protected NurseRosteringApp nurseRosteringApp;
    protected SolutionBusiness solutionBusiness;

    public NurseRosteringEvaluatorHelper() {
        nurseRosteringApp = new NurseRosteringApp();
        solutionBusiness = nurseRosteringApp.createSolutionBusiness();
    }

    public File getImportDir() {
        return solutionBusiness.getImportDataDir();
    }

    public void evaluate(String filePrefix, String fileSuffix, String lineContainsFilter) {
        Process process = null;
        try {
            File inputFile = new File(solutionBusiness.getImportDataDir(),
                    filePrefix + ".xml").getCanonicalFile();
            File solvedFile = new File(solutionBusiness.getSolvedDataDir(),
                    filePrefix + fileSuffix + ".xml").getCanonicalFile();
            if (!solvedFile.exists()) {
                logger.info("Skipping inputFile ({}) because no solvedFile found.", inputFile);
                return;
            }
            solutionBusiness.openSolution(solvedFile);
            HardAndSoftScore score = (HardAndSoftScore) solutionBusiness.getScore();
            File outputFile = new File(solutionBusiness.getExportDataDir(),
                    filePrefix + fileSuffix + ".xml").getCanonicalFile();
            solutionBusiness.exportSolution(outputFile);
            File evaluatorDir = new File("local/competition/nurserostering/");
            String command = "java -jar evaluator.jar " + inputFile.getAbsolutePath()
                    + " " + outputFile.getAbsolutePath();
            process = Runtime.getRuntime().exec(command, null, evaluatorDir);
            EvaluatorSummaryFilterOutputStream out = new EvaluatorSummaryFilterOutputStream(outputFile.getName(), lineContainsFilter);
            IOUtils.copy(process.getInputStream(), out);
            IOUtils.copy(process.getErrorStream(), System.err);
            out.writeResults();
            int penaltyTotal = out.getPenaltyTotal();
            if (score.getHardScore() == 0) {
                if (score.getSoftScore() == (-penaltyTotal)) {
                    System.out.println("The calculated soft score (" + score.getSoftScore()
                            + ") is the same as the evaluator penalty total (" + penaltyTotal + ").");
                } else {
                    throw new IllegalStateException("The calculated soft score (" + score.getSoftScore()
                            + ") is not the same as the evaluator penalty total (" + penaltyTotal + ").");
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static class EvaluatorSummaryFilterOutputStream extends OutputStream {

        private String name;
        private String lineContainsFilter;

        private StringBuilder lineBuffer = new StringBuilder(120);
        private Map<String, int[]> costMap = new TreeMap<String, int[]>();
        private String lastEmployeeCode = null;

        private int penaltyTotal;

        private EvaluatorSummaryFilterOutputStream(String name, String lineContainsFilter) {
            super();
            this.name = name;
            this.lineContainsFilter = lineContainsFilter;
        }

        public int getPenaltyTotal() {
            return penaltyTotal;
        }

        public void write(int c) throws IOException {
            if (c == '\n') {
                String line = lineBuffer.toString();
                lineBuffer.delete(0, lineBuffer.length());
                processLine(line);
            } else {
                lineBuffer.append((char) c);
            }
        }

        private void processLine(String line) {
            int employeeIndex = line.indexOf("Employee: ");
            if (employeeIndex >= 0) {
                lastEmployeeCode = line.substring(employeeIndex).replaceAll("Employee: (.+)", "$1");
            } else if (line.contains("Penalty:")) {
                lastEmployeeCode = null;
            }
            if (lineContainsFilter == null || line.contains(lineContainsFilter)) {
                int excessIndex = line.indexOf("excess = ");
                if (excessIndex >= 0) {
                    String key = line.substring(0, excessIndex);
                    int costIndex = line.indexOf("cost = ");
                    int value = Integer.parseInt((line.substring(costIndex) + " ").replaceAll("cost = (\\d+) .*", "$1"));
                    int[] cost = costMap.get(key);
                    if (cost == null) {
                        cost = new int[]{1, value};
                        costMap.put(key, cost);
                    } else {
                        cost[0]++;
                        cost[1] += value;
                    }
                }
                if (lastEmployeeCode != null) {
                    System.out.print("E(" + lastEmployeeCode + ")  ");
                }
                System.out.println(line);
            }
        }

        public void writeResults() {
            System.out.println("EvaluatorHelper results for " + name);
            penaltyTotal = 0;
            if (lineContainsFilter != null) {
                System.out.println("with lineContainsFilter (" + lineContainsFilter + ")");
            }
            for (Map.Entry<String, int[]> entry : costMap.entrySet()) {
                int[] cost = entry.getValue();
                penaltyTotal += cost[1];
                System.out.println(entry.getKey() + " count = " + cost[0] + " total = " + cost[1]);
            }
            System.out.println("The penaltyTotal: " + penaltyTotal);
        }
    }

}
