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

import org.drools.ClassObjectFilter;
import org.drools.WorkingMemory;
import org.drools.planner.core.Solver;
import org.drools.planner.core.event.SolverEventListener;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.constraint.ConstraintOccurrence;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.AbstractSolver;
import org.drools.planner.core.solver.AbstractSolverScope;
import org.drools.planner.examples.common.persistence.AbstractSolutionExporter;
import org.drools.planner.examples.common.persistence.AbstractSolutionImporter;
import org.drools.planner.examples.common.persistence.SolutionDao;
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
    private AbstractSolverScope abstractSolverScope;

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
        if (hasImporter()) {
            importDataDir = new File(getDataDir(), "input");
            if (!importDataDir.exists()) {
                throw new IllegalStateException("The directory importDataDir (" + importDataDir.getAbsolutePath()
                        + ") does not exist. The working directory should be set to drools-planner-examples.");
            }
        }
        unsolvedDataDir = new File(getDataDir(), "unsolved");
        if (!unsolvedDataDir.exists()) {
            throw new IllegalStateException("The directory unsolvedDataDir (" + unsolvedDataDir.getAbsolutePath()
                    + ") does not exist. The working directory should be set to drools-planner-examples.");
        }
        solvedDataDir = new File(getDataDir(), "solved");
        if (!solvedDataDir.exists() && !solvedDataDir.mkdir()) {
            throw new IllegalStateException("The directory solvedDataDir (" + solvedDataDir.getAbsolutePath()
                    + ") does not exist and could not be created.");
        }
        if (hasExporter()) {
            exportDataDir = new File(getDataDir(), "output");
            if (!exportDataDir.exists() && !exportDataDir.mkdir()) {
                throw new IllegalStateException("The directory exportDataDir (" + exportDataDir.getAbsolutePath()
                        + ") does not exist and could not be created.");
            }
        }
    }

    public File getImportDataDir() {
        return importDataDir;
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
        this.abstractSolverScope = ((AbstractSolver) solver).getAbstractSolverScope();
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
        return abstractSolverScope.getWorkingSolution();
    }

    public Score getScore() {
        return abstractSolverScope.calculateScoreFromWorkingMemory();
    }

    public void addSolverEventLister(SolverEventListener eventListener) {
        solver.addEventListener(eventListener);
    }

    public List<ScoreDetail> getScoreDetailList() {
        Map<String, ScoreDetail> scoreDetailMap = new HashMap<String, ScoreDetail>();
        WorkingMemory workingMemory = abstractSolverScope.getWorkingMemory();
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
        solver.setStartingSolution(solution);
    }

    public void openSolution(File file) {
        Solution solution = solutionDao.readSolution(file);
        solver.setStartingSolution(solution);
    }

    public void saveSolution(File file) {
        Solution solution = abstractSolverScope.getWorkingSolution();
        solutionDao.writeSolution(solution, file);
    }

    public void exportSolution(File file) {
        Solution solution = abstractSolverScope.getWorkingSolution();
        exporter.writeSolution(solution, file);
    }

    public void doMove(Move move) {
        if (!move.isMoveDoable(abstractSolverScope.getWorkingMemory())) {
            logger.info("Not doing user move ({}) because it is not doable.", move);
            return;
        }
        logger.info("Doing user move ({}).", move);
        move.doMove(abstractSolverScope.getWorkingMemory());
    }

    public void solve() {
        solver.solve();
        Solution solution = solver.getBestSolution();
        solver.setStartingSolution(solution);
    }

    public void terminateSolvingEarly() {
        solver.terminateEarly();
    }

    public class SolverExampleFileFilter implements FileFilter {

        public boolean accept(File file) {
            if (file.isDirectory() || file.isHidden()) {
                return false;
            }
            return file.getName().endsWith(".xml");
        }

    }

}
