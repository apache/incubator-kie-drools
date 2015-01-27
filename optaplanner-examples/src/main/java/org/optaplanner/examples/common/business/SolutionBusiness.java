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

package org.optaplanner.examples.common.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedSwapMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolutionBusiness {

    private static final ProblemFileComparator FILE_COMPARATOR = new ProblemFileComparator();

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final CommonApp app;
    private SolutionDao solutionDao;

    private AbstractSolutionImporter importer;
    private AbstractSolutionExporter exporter;

    private File importDataDir;
    private File unsolvedDataDir;
    private File solvedDataDir;
    private File exportDataDir;

    // volatile because the solve method doesn't come from the event thread (like every other method call)
    private volatile Solver solver;
    private String solutionFileName = null;
    private ScoreDirector guiScoreDirector;

    public SolutionBusiness(CommonApp app) {
        this.app = app;
    }

    public String getAppName() {
        return app.getName();
    }

    public String getAppDescription() {
        return app.getDescription();
    }

    public String getAppIconResource() {
        return app.getIconResource();
    }

    public void setSolutionDao(SolutionDao solutionDao) {
        this.solutionDao = solutionDao;
    }

    public void setImporter(AbstractSolutionImporter importer) {
        this.importer = importer;
    }

    public void setExporter(AbstractSolutionExporter exporter) {
        this.exporter = exporter;
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
        File dataDir = solutionDao.getDataDir();
        if (hasImporter()) {
            importDataDir = new File(dataDir, "import");
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
            exportDataDir = new File(dataDir, "export");
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

    public boolean isImportFileDirectory() {
        return importer.isInputFileDirectory();
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

    public String getExportFileSuffix() {
        return exporter.getOutputFileSuffix();
    }

    public void setSolver(Solver solver) {
        this.solver = solver;
        ScoreDirectorFactory scoreDirectorFactory = solver.getScoreDirectorFactory();
        guiScoreDirector = scoreDirectorFactory.buildScoreDirector();
    }

    public List<File> getUnsolvedFileList() {
        List<File> fileList = new ArrayList<File>(
                FileUtils.listFiles(unsolvedDataDir, new String[]{solutionDao.getFileExtension()} , true));
        Collections.sort(fileList, FILE_COMPARATOR);
        return fileList;
    }

    public List<File> getSolvedFileList() {
        List<File> fileList = new ArrayList<File>(
                FileUtils.listFiles(solvedDataDir, new String[]{solutionDao.getFileExtension()} , true));
        Collections.sort(fileList, FILE_COMPARATOR);
        return fileList;
    }

    public Solution getSolution() {
        return guiScoreDirector.getWorkingSolution();
    }

    public void setSolution(Solution solution) {
        guiScoreDirector.setWorkingSolution(solution);
    }

    public String getSolutionFileName() {
        return solutionFileName;
    }

    public Score getScore() {
        return guiScoreDirector.calculateScore();
    }

    public boolean isSolving() {
        return solver.isSolving();
    }

    public void registerForBestSolutionChanges(final SolverAndPersistenceFrame solverAndPersistenceFrame) {
        solver.addEventListener(new SolverEventListener<Solution>() {
            // Not called on the event thread
            public void bestSolutionChanged(BestSolutionChangedEvent<Solution> event) {
                // Avoid ConcurrentModificationException when there is an unprocessed ProblemFactChange
                // because the paint method uses the same problem facts instances as the Solver's workingSolution
                // unlike the planning entities of the bestSolution which are cloned from the Solver's workingSolution
                if (solver.isEveryProblemFactChangeProcessed()) {
                    // final is also needed for thread visibility
                    final Solution latestBestSolution = event.getNewBestSolution();
                    // Migrate it to the event thread
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            // TODO by the time we process this event, a newer bestSolution might already be queued
                            guiScoreDirector.setWorkingSolution(latestBestSolution);
                            solverAndPersistenceFrame.bestSolutionChanged();
                        }
                    });
                }
            }
        });
    }

    public boolean isConstraintMatchEnabled() {
        return guiScoreDirector.isConstraintMatchEnabled();
    }

    public List<ConstraintMatchTotal> getConstraintMatchTotalList() {
        List<ConstraintMatchTotal> constraintMatchTotalList = new ArrayList<ConstraintMatchTotal>(
                guiScoreDirector.getConstraintMatchTotals());
        Collections.sort(constraintMatchTotalList);
        return constraintMatchTotalList;
    }

    public void importSolution(File file) {
        Solution solution = importer.readSolution(file);
        solutionFileName = file.getName();
        guiScoreDirector.setWorkingSolution(solution);
    }

    public void openSolution(File file) {
        Solution solution = solutionDao.readSolution(file);
        solutionFileName = file.getName();
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
        guiScoreDirector.calculateScore();
    }

    public void doProblemFactChange(ProblemFactChange problemFactChange) {
        if (solver.isSolving()) {
            solver.addProblemFactChange(problemFactChange);
        } else {
            problemFactChange.doChange(guiScoreDirector);
            guiScoreDirector.calculateScore();
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
        solver.solve(planningProblem);
        return solver.getBestSolution();
    }

    public void terminateSolvingEarly() {
        solver.terminateEarly();
    }

    public ChangeMove createChangeMove(Object entity, String variableName, Object toPlanningValue) {
        SolutionDescriptor solutionDescriptor = ((InnerScoreDirector) guiScoreDirector).getSolutionDescriptor();
        GenuineVariableDescriptor variableDescriptor = solutionDescriptor.findGenuineVariableDescriptorOrFail(
                entity, variableName);
        if (variableDescriptor.isChained()) {
            return new ChainedChangeMove(entity, variableDescriptor, toPlanningValue);
        } else {
            return new ChangeMove(entity, variableDescriptor, toPlanningValue);
        }
    }

    public void doChangeMove(Object entity, String variableName, Object toPlanningValue) {
        ChangeMove move = createChangeMove(entity, variableName, toPlanningValue);
        doMove(move);
    }

    public SwapMove createSwapMove(Object leftEntity, Object rightEntity) {
        SolutionDescriptor solutionDescriptor = ((InnerScoreDirector) guiScoreDirector).getSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptor(leftEntity.getClass());
        Collection<GenuineVariableDescriptor> variableDescriptors = entityDescriptor.getGenuineVariableDescriptors();
        if (entityDescriptor.hasAnyChainedGenuineVariables()) {
            return new ChainedSwapMove(variableDescriptors, leftEntity, rightEntity);
        } else {
            return new SwapMove(variableDescriptors, leftEntity, rightEntity);
        }
    }

    public void doSwapMove(Object leftEntity, Object rightEntity) {
        SwapMove move = createSwapMove(leftEntity, rightEntity);
        doMove(move);
    }

}
