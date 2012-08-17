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

package org.drools.planner.examples.common.business;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.drools.ClassObjectFilter;
import org.drools.WorkingMemory;
import org.drools.planner.core.Solver;
import org.drools.planner.core.event.BestSolutionChangedEvent;
import org.drools.planner.core.event.SolverEventListener;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.constraint.ConstraintOccurrence;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.score.director.ScoreDirectorFactory;
import org.drools.planner.core.score.director.drools.DroolsScoreDirector;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolver;
import org.drools.planner.core.solver.ProblemFactChange;
import org.drools.planner.examples.common.persistence.AbstractSolutionExporter;
import org.drools.planner.examples.common.persistence.AbstractSolutionImporter;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.common.swingui.SolverAndPersistenceFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolutionBusiness {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private SolutionDao solutionDao;

    private AbstractSolutionImporter importer;
    private AbstractSolutionExporter exporter;

    private File importDataDir;
    private File unsolvedDataDir;
    private File solvedDataDir;
    private File exportDataDir;

    // volatile because the solve method doesn't come from the event thread (like every other method call)
    private volatile Solver solver;
    private ScoreDirector guiScoreDirector;

    public void setSolutionDao(SolutionDao solutionDao) {
        this.solutionDao = solutionDao;
    }

    public void setImporter(AbstractSolutionImporter importer) {
        this.importer = importer;
    }

    public void setExporter(AbstractSolutionExporter exporter) {
        this.exporter = exporter;
    }

    public File getDataDir() {
        return solutionDao.getDataDir();
    }

    public String getDirName() {
        return solutionDao.getDirName();
    }

    public boolean hasImporter() {
        return importer != null;
    }

    public boolean hasExporter() {
        return exporter != null;
    }

    public void updateDataDirs() {
        File dataDir = getDataDir();
        if (!dataDir.exists()) {
            throw new IllegalStateException("The directory dataDir (" + dataDir.getAbsolutePath()
                    + ") does not exist." +
                    " The working directory should be set to the directory that contains the data directory." +
                    " This is different in a git clone (drools-planner/drools-planner-examples)" +
                    " and the release zip (examples).");
        }
        if (hasImporter()) {
            importDataDir = new File(dataDir, "input");
            if (!importDataDir.exists()) {
                throw new IllegalStateException("The directory importDataDir (" + importDataDir.getAbsolutePath()
                        + ") does not exist.");
            }
        }
        unsolvedDataDir = new File(dataDir, "unsolved");
        if (!unsolvedDataDir.exists()) {
            throw new IllegalStateException("The directory unsolvedDataDir (" + unsolvedDataDir.getAbsolutePath()
                    + ") does not exist.");
        }
        solvedDataDir = new File(dataDir, "solved");
        if (!solvedDataDir.exists() && !solvedDataDir.mkdir()) {
            throw new IllegalStateException("The directory solvedDataDir (" + solvedDataDir.getAbsolutePath()
                    + ") does not exist and could not be created.");
        }
        if (hasExporter()) {
            exportDataDir = new File(dataDir, "output");
            if (!exportDataDir.exists() && !exportDataDir.mkdir()) {
                throw new IllegalStateException("The directory exportDataDir (" + exportDataDir.getAbsolutePath()
                        + ") does not exist and could not be created.");
            }
        }
    }

    public File getImportDataDir() {
        return importDataDir;
    }

    public boolean acceptImportFile(File file) {
        return importer.acceptInputFile(file);
    }

    public String getImportFileSuffix() {
        return importer.getInputFileSuffix();
    }

    public File getUnsolvedDataDir() {
        return unsolvedDataDir;
    }

    public File getSolvedDataDir() {
        return solvedDataDir;
    }

    public File getExportDataDir() {
        return exportDataDir;
    }

    public void setSolver(Solver solver) {
        this.solver = solver;
        ScoreDirectorFactory scoreDirectorFactory = solver.getScoreDirectorFactory();
        guiScoreDirector = scoreDirectorFactory.buildScoreDirector();
    }

    public List<File> getUnsolvedFileList() {
        List<File> unsolvedFileList = Arrays.asList(unsolvedDataDir.listFiles(new SolverExampleFileFilter()));
        Collections.sort(unsolvedFileList);
        return unsolvedFileList;
    }

    public List<File> getSolvedFileList() {
        List<File> solvedFileList = Arrays.asList(solvedDataDir.listFiles(new SolverExampleFileFilter()));
        Collections.sort(solvedFileList);
        return solvedFileList;
    }

    public Solution getSolution() {
        return guiScoreDirector.getWorkingSolution();
    }

    public void setSolution(Solution solution) {
        guiScoreDirector.setWorkingSolution(solution);
    }

    public Score getScore() {
        return guiScoreDirector.calculateScore();
    }

    public boolean isSolving() {
        return solver.isSolving();
    }

    public void registerForBestSolutionChanges(final SolverAndPersistenceFrame solverAndPersistenceFrame) {
        solver.addEventListener(new SolverEventListener() {
            // Not called on the event thread
            public void bestSolutionChanged(BestSolutionChangedEvent event) {
                // Avoid ConcurrentModificationException when there is an unprocessed ProblemFactChange
                // because the paint method uses the problem facts instances as the solver
                // unlike the planning entities which are cloned
                if (solver.isEveryProblemFactChangeProcessed()) {
                    // final is also needed for thread visibility
                    final Solution latestBestSolution = event.getNewBestSolution();
                    // Migrate it to the event thread
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            guiScoreDirector.setWorkingSolution(latestBestSolution);
                            solverAndPersistenceFrame.bestSolutionChanged();
                        }
                    });
                }
            }
        });
    }

    public List<ScoreDetail> getScoreDetailList() {
        if (!(guiScoreDirector instanceof DroolsScoreDirector)) {
            return null;
        }
        Map<String, ScoreDetail> scoreDetailMap = new HashMap<String, ScoreDetail>();
        WorkingMemory workingMemory = ((DroolsScoreDirector) guiScoreDirector).getWorkingMemory();
        if (workingMemory == null) {
            return Collections.emptyList();
        }
        Iterator<ConstraintOccurrence> it = (Iterator<ConstraintOccurrence>) workingMemory.iterateObjects(
                new ClassObjectFilter(ConstraintOccurrence.class));
        while (it.hasNext()) {
            ConstraintOccurrence constraintOccurrence = it.next();
            ScoreDetail scoreDetail = scoreDetailMap.get(constraintOccurrence.getRuleId());
            if (scoreDetail == null) {
                scoreDetail = new ScoreDetail(constraintOccurrence.getRuleId(), constraintOccurrence.getConstraintType());
                scoreDetailMap.put(constraintOccurrence.getRuleId(), scoreDetail);
            }
            scoreDetail.addConstraintOccurrence(constraintOccurrence);
        }
        List<ScoreDetail> scoreDetailList = new ArrayList<ScoreDetail>(scoreDetailMap.values());
        Collections.sort(scoreDetailList);
        return scoreDetailList;
    }

    public void importSolution(File file) {
        Solution solution = importer.readSolution(file);
        guiScoreDirector.setWorkingSolution(solution);
    }

    public void openSolution(File file) {
        Solution solution = solutionDao.readSolution(file);
        guiScoreDirector.setWorkingSolution(solution);
    }

    public void saveSolution(File file) {
        Solution solution = guiScoreDirector.getWorkingSolution();
        solutionDao.writeSolution(solution, file);
    }

    public void exportSolution(File file) {
        Solution solution = guiScoreDirector.getWorkingSolution();
        exporter.writeSolution(solution, file);
    }

    public void doMove(Move move) {
        if (solver.isSolving()) {
            logger.error("Not doing user move ({}) because the solver is solving.", move);
            return;
        }
        if (!move.isMoveDoable(guiScoreDirector)) {
            logger.warn("Not doing user move ({}) because it is not doable.", move);
            return;
        }
        logger.info("Doing user move ({}).", move);
        move.doMove(guiScoreDirector);
    }

    public void doProblemFactChange(ProblemFactChange problemFactChange) {
        if (solver.isSolving()) {
            solver.addProblemFactChange(problemFactChange);
        } else {
            problemFactChange.doChange(guiScoreDirector);
        }
    }

    /**
     * Can be called on any thread.
     * <p/>
     * Note: This method does not change the guiScoreDirector because that can only be changed on the event thread.
     * @param planningProblem never null
     * @return never null
     */
    public Solution solve(Solution planningProblem) {
        solver.setPlanningProblem(planningProblem);
        solver.solve();
        return solver.getBestSolution();
    }

    public void terminateSolvingEarly() {
        solver.terminateEarly();
    }

    public static class SolverExampleFileFilter implements FileFilter {

        public boolean accept(File file) {
            if (file.isDirectory() || file.isHidden()) {
                return false;
            }
            return file.getName().endsWith(".xml");
        }

    }

}
