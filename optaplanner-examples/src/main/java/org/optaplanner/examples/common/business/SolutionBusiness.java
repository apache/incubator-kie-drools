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

package org.optaplanner.examples.common.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
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
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class SolutionBusiness<Solution_> {

    private static final ProblemFileComparator FILE_COMPARATOR = new ProblemFileComparator();

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final CommonApp app;
    private File dataDir;
    private SolutionFileIO<Solution_> solutionFileIO;

    private AbstractSolutionImporter<Solution_>[] importers;
    private AbstractSolutionExporter<Solution_> exporter;

    private File importDataDir;
    private File unsolvedDataDir;
    private File solvedDataDir;
    private File exportDataDir;

    // volatile because the solve method doesn't come from the event thread (like every other method call)
    private volatile Solver<Solution_> solver;
    private String solutionFileName = null;
    private ScoreDirector<Solution_> guiScoreDirector;

    private final AtomicReference<Solution_> skipToBestSolutionRef = new AtomicReference<>();

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

    public File getDataDir() {
        return dataDir;
    }

    public void setDataDir(File dataDir) {
        this.dataDir = dataDir;
    }

    public SolutionFileIO<Solution_> getSolutionFileIO() {
        return solutionFileIO;
    }

    public void setSolutionFileIO(SolutionFileIO<Solution_> solutionFileIO) {
        this.solutionFileIO = solutionFileIO;
    }

    public AbstractSolutionImporter<Solution_>[] getImporters() {
        return importers;
    }

    public void setImporters(AbstractSolutionImporter<Solution_>[] importers) {
        this.importers = importers;
    }

    public void setExporter(AbstractSolutionExporter<Solution_> exporter) {
        this.exporter = exporter;
    }

    public boolean hasImporter() {
        return importers.length > 0;
    }

    public boolean hasExporter() {
        return exporter != null;
    }

    public void updateDataDirs() {
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

    public void setSolver(Solver<Solution_> solver) {
        this.solver = solver;
        ScoreDirectorFactory<Solution_> scoreDirectorFactory = solver.getScoreDirectorFactory();
        guiScoreDirector = scoreDirectorFactory.buildScoreDirector();
    }

    public List<File> getUnsolvedFileList() {
        List<File> fileList = new ArrayList<>(
                FileUtils.listFiles(unsolvedDataDir, new String[]{solutionFileIO.getInputFileExtension()}, true));
        Collections.sort(fileList, FILE_COMPARATOR);
        return fileList;
    }

    public List<File> getSolvedFileList() {
        List<File> fileList = new ArrayList<>(
                FileUtils.listFiles(solvedDataDir, new String[]{solutionFileIO.getOutputFileExtension()}, true));
        Collections.sort(fileList, FILE_COMPARATOR);
        return fileList;
    }

    public Solution_ getSolution() {
        return guiScoreDirector.getWorkingSolution();
    }

    public void setSolution(Solution_ solution) {
        guiScoreDirector.setWorkingSolution(solution);
    }

    public String getSolutionFileName() {
        return solutionFileName;
    }

    public void setSolutionFileName(String solutionFileName) {
        this.solutionFileName = solutionFileName;
    }

    public Score getScore() {
        return guiScoreDirector.calculateScore();
    }

    public boolean isSolving() {
        return solver.isSolving();
    }

    public void registerForBestSolutionChanges(final SolverAndPersistenceFrame solverAndPersistenceFrame) {
        solver.addEventListener(event -> {
            // Called on the Solver thread, so not on the Swing Event thread
            /*
             * Avoid ConcurrentModificationException when there is an unprocessed ProblemFactChange
             * because the paint method uses the same problem facts instances as the Solver's workingSolution
             * unlike the planning entities of the bestSolution which are cloned from the Solver's workingSolution
             */
            if (solver.isEveryProblemFactChangeProcessed()) {
                // The final is also needed for thread visibility
                final Solution_ newBestSolution = event.getNewBestSolution();
                skipToBestSolutionRef.set(newBestSolution);
                SwingUtilities.invokeLater(() -> {
                    // Called on the Swing Event thread
                    Solution_ skipToBestSolution = skipToBestSolutionRef.get();
                    // Skip this event if a newer one arrived meanwhile to avoid flooding the Swing Event thread
                    if (newBestSolution != skipToBestSolution) {
                        return;
                    }
                    guiScoreDirector.setWorkingSolution(newBestSolution);
                    solverAndPersistenceFrame.bestSolutionChanged();
                });
            }
        });
    }

    public boolean isConstraintMatchEnabled() {
        return guiScoreDirector.isConstraintMatchEnabled();
    }

    public List<ConstraintMatchTotal> getConstraintMatchTotalList() {
        List<ConstraintMatchTotal> constraintMatchTotalList = new ArrayList<>(
                guiScoreDirector.getConstraintMatchTotals());
        Collections.sort(constraintMatchTotalList);
        return constraintMatchTotalList;
    }

    public Map<Object, Indictment> getIndictmentMap() {
        return guiScoreDirector.getIndictmentMap();
    }

    public void importSolution(File file) {
        AbstractSolutionImporter<Solution_> importer = determineImporter(file);
        Solution_ solution = importer.readSolution(file);
        solutionFileName = file.getName();
        guiScoreDirector.setWorkingSolution(solution);
    }

    private AbstractSolutionImporter<Solution_> determineImporter(File file) {
        for (AbstractSolutionImporter<Solution_> importer : importers) {
            if (importer.acceptInputFile(file)) {
                return importer;
            }
        }
        return importers[0];
    }

    public void openSolution(File file) {
        Solution_ solution = solutionFileIO.read(file);
        logger.info("Opened: {}", file);
        solutionFileName = file.getName();
        guiScoreDirector.setWorkingSolution(solution);
    }

    public void saveSolution(File file) {
        Solution_ solution = guiScoreDirector.getWorkingSolution();
        solutionFileIO.write(solution, file);
        logger.info("Saved: {}", file);
    }

    public void exportSolution(File file) {
        Solution_ solution = guiScoreDirector.getWorkingSolution();
        exporter.writeSolution(solution, file);
    }

    public void doMove(Move<Solution_> move) {
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

    public void doProblemFactChange(ProblemFactChange<Solution_> problemFactChange) {
        if (solver.isSolving()) {
            solver.addProblemFactChange(problemFactChange);
        } else {
            problemFactChange.doChange(guiScoreDirector);
            guiScoreDirector.calculateScore();
        }
    }

    /**
     * Can be called on any thread.
     * <p>
     * Note: This method does not change the guiScoreDirector because that can only be changed on the event thread.
     * @param problem never null
     * @return never null
     */
    public Solution_ solve(Solution_ problem) {
        return solver.solve(problem);
    }

    public void terminateSolvingEarly() {
        solver.terminateEarly();
    }

    public ChangeMove<Solution_> createChangeMove(Object entity, String variableName, Object toPlanningValue) {
        // TODO Solver should support building a ChangeMove
        InnerScoreDirector<Solution_> guiInnerScoreDirector = (InnerScoreDirector<Solution_>) this.guiScoreDirector;
        SolutionDescriptor<Solution_> solutionDescriptor = guiInnerScoreDirector.getSolutionDescriptor();
        GenuineVariableDescriptor<Solution_> variableDescriptor = solutionDescriptor.findGenuineVariableDescriptorOrFail(
                entity, variableName);
        if (variableDescriptor.isChained()) {
            SupplyManager supplyManager = guiInnerScoreDirector.getSupplyManager();
            SingletonInverseVariableSupply inverseVariableSupply = supplyManager.demand(
                    new SingletonInverseVariableDemand(variableDescriptor));
            return new ChainedChangeMove<>(entity, variableDescriptor, inverseVariableSupply, toPlanningValue);
        } else {
            return new ChangeMove<>(entity, variableDescriptor, toPlanningValue);
        }
    }

    public void doChangeMove(Object entity, String variableName, Object toPlanningValue) {
        ChangeMove<Solution_> move = createChangeMove(entity, variableName, toPlanningValue);
        doMove(move);
    }

    public SwapMove<Solution_> createSwapMove(Object leftEntity, Object rightEntity) {
        // TODO Solver should support building a SwapMove
        InnerScoreDirector<Solution_> guiInnerScoreDirector = (InnerScoreDirector<Solution_>) this.guiScoreDirector;
        SolutionDescriptor<Solution_> solutionDescriptor = guiInnerScoreDirector.getSolutionDescriptor();
        EntityDescriptor<Solution_> entityDescriptor = solutionDescriptor.findEntityDescriptor(leftEntity.getClass());
        List<GenuineVariableDescriptor<Solution_>> variableDescriptorList = entityDescriptor.getGenuineVariableDescriptorList();
        if (entityDescriptor.hasAnyChainedGenuineVariables()) {
            List<SingletonInverseVariableSupply> inverseVariableSupplyList
                    = new ArrayList<>(variableDescriptorList.size());
            SupplyManager supplyManager = guiInnerScoreDirector.getSupplyManager();
            for (GenuineVariableDescriptor variableDescriptor : variableDescriptorList) {
                SingletonInverseVariableSupply inverseVariableSupply;
                if (variableDescriptor.isChained()) {
                    inverseVariableSupply = supplyManager.demand(
                            new SingletonInverseVariableDemand(variableDescriptor));
                } else {
                    inverseVariableSupply = null;
                }
                inverseVariableSupplyList.add(inverseVariableSupply);
            }
            return new ChainedSwapMove<>(variableDescriptorList, inverseVariableSupplyList, leftEntity, rightEntity);
        } else {
            return new SwapMove<>(variableDescriptorList, leftEntity, rightEntity);
        }
    }

    public void doSwapMove(Object leftEntity, Object rightEntity) {
        SwapMove<Solution_> move = createSwapMove(leftEntity, rightEntity);
        doMove(move);
    }
}
