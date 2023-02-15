package org.optaplanner.examples.common.business;

import static java.util.stream.Collectors.toList;
import static org.optaplanner.core.api.solver.SolutionUpdatePolicy.NO_UPDATE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.SwingUtilities;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;
import org.optaplanner.core.impl.solver.change.DefaultProblemChangeDirector;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public final class SolutionBusiness<Solution_, Score_ extends Score<Score_>> implements AutoCloseable {

    public static String getBaseFileName(File file) {
        return getBaseFileName(file.getName());
    }

    public static String getBaseFileName(String name) {
        int indexOfLastDot = name.lastIndexOf('.');
        if (indexOfLastDot > 0) {
            return name.substring(0, indexOfLastDot);
        } else {
            return name;
        }
    }

    private static final Comparator<File> FILE_COMPARATOR = new ProblemFileComparator();
    private static final AtomicLong SOLVER_JOB_ID_COUNTER = new AtomicLong();
    private static final Logger LOGGER = LoggerFactory.getLogger(SolutionBusiness.class);

    private final CommonApp<Solution_> app;
    private final DefaultSolverFactory<Solution_> solverFactory;
    private final SolverManager<Solution_, Long> solverManager;
    private final SolutionManager<Solution_, Score_> solutionManager;

    private final AtomicReference<SolverJob<Solution_, Long>> solverJobRef = new AtomicReference<>();
    private final AtomicReference<Solution_> workingSolutionRef = new AtomicReference<>();

    private File dataDir;
    private SolutionFileIO<Solution_> solutionFileIO;
    private Set<AbstractSolutionImporter<Solution_>> importers;
    private Set<AbstractSolutionExporter<Solution_>> exporters;
    private File importDataDir;
    private File unsolvedDataDir;
    private File solvedDataDir;
    private File exportDataDir;
    private String solutionFileName = null;

    public SolutionBusiness(CommonApp<Solution_> app, SolverFactory<Solution_> solverFactory) {
        this.app = app;
        this.solverFactory = ((DefaultSolverFactory<Solution_>) solverFactory);
        this.solverManager = SolverManager.create(solverFactory);
        this.solutionManager = SolutionManager.create(solverFactory);
    }

    private static List<File> getFileList(File dataDir, String extension) {
        try (Stream<Path> paths = Files.walk(dataDir.toPath(), FileVisitOption.FOLLOW_LINKS)) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("." + extension))
                    .map(Path::toFile)
                    .sorted(FILE_COMPARATOR)
                    .collect(toList());
        } catch (IOException e) {
            throw new IllegalStateException("Error while crawling data directory (" + dataDir + ").", e);
        }
    }

    public String getAppName() {
        return app.getName();
    }

    public String getAppDescription() {
        return app.getDescription();
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

    public Set<AbstractSolutionImporter<Solution_>> getImporters() {
        return importers;
    }

    public void setImporters(Set<AbstractSolutionImporter<Solution_>> importers) {
        this.importers = importers;
    }

    public Set<AbstractSolutionExporter<Solution_>> getExporters() {
        return this.exporters;
    }

    public void setExporters(Set<AbstractSolutionExporter<Solution_>> exporters) {
        if (exporters == null) {
            throw new IllegalArgumentException("Passed exporters must not be null");
        }
        this.exporters = exporters;
    }

    public boolean hasImporter() {
        return !importers.isEmpty();
    }

    public boolean hasExporter() {
        return exporters != null && exporters.size() > 0;
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

    public List<File> getUnsolvedFileList() {
        return getFileList(unsolvedDataDir, solutionFileIO.getInputFileExtension());
    }

    public List<File> getSolvedFileList() {
        return getFileList(solvedDataDir, solutionFileIO.getOutputFileExtension());
    }

    public Solution_ getSolution() {
        return workingSolutionRef.get();
    }

    public void setSolution(Solution_ solution) {
        workingSolutionRef.set(solution);
    }

    public String getSolutionFileName() {
        return solutionFileName;
    }

    public void setSolutionFileName(String solutionFileName) {
        this.solutionFileName = solutionFileName;
    }

    public Score_ getScore() {
        return solutionManager.update(getSolution());
    }

    public boolean isSolving() {
        SolverJob<Solution_, Long> solverJob = solverJobRef.get();
        return solverJob != null && solverJob.getSolverStatus() == SolverStatus.SOLVING_ACTIVE;
    }

    public boolean isConstraintMatchEnabled() {
        return applyScoreDirector(InnerScoreDirector::isConstraintMatchEnabled);
    }

    private <Result_> Result_ applyScoreDirector(Function<InnerScoreDirector<Solution_, Score_>, Result_> function) {
        try (InnerScoreDirector<Solution_, Score_> scoreDirector =
                (InnerScoreDirector<Solution_, Score_>) solverFactory.getScoreDirectorFactory().buildScoreDirector(true,
                        true)) {
            scoreDirector.setWorkingSolution(getSolution());
            Result_ result = function.apply(scoreDirector);
            scoreDirector.triggerVariableListeners();
            scoreDirector.calculateScore();
            setSolution(scoreDirector.getWorkingSolution());
            return result;
        }
    }

    public List<ConstraintMatchTotal<Score_>> getConstraintMatchTotalList() {
        return solutionManager.explain(getSolution(), NO_UPDATE)
                .getConstraintMatchTotalMap()
                .values()
                .stream()
                .sorted()
                .collect(toList());
    }

    public Map<Object, Indictment<Score_>> getIndictmentMap() {
        return solutionManager.explain(getSolution(), NO_UPDATE).getIndictmentMap();
    }

    public void importSolution(File file) {
        AbstractSolutionImporter<Solution_> importer = determineImporter(file);
        Solution_ solution = importer.readSolution(file);
        solutionFileName = file.getName();
        setSolution(solution);
    }

    private AbstractSolutionImporter<Solution_> determineImporter(File file) {
        for (AbstractSolutionImporter<Solution_> importer : importers) {
            if (importer.acceptInputFile(file)) {
                return importer;
            }
        }
        return importers.stream()
                .findFirst()
                .orElseThrow();
    }

    public void openSolution(File file) {
        Solution_ solution = solutionFileIO.read(file);
        LOGGER.info("Opened: {}", file);
        solutionFileName = file.getName();
        workingSolutionRef.set(solution);
    }

    public void saveSolution(File file) {
        solutionFileIO.write(getSolution(), file);
        LOGGER.info("Saved: {}", file);
    }

    public void exportSolution(AbstractSolutionExporter<Solution_> exporter, File file) {
        exporter.writeSolution(getSolution(), file);
    }

    private void acceptScoreDirector(Consumer<InnerScoreDirector<Solution_, Score_>> consumer) {
        applyScoreDirector(s -> {
            consumer.accept(s);
            return null;
        });
    }

    public void doProblemChange(ProblemChange<Solution_> problemChange) {
        SolverJob<Solution_, Long> solverJob = solverJobRef.get();
        if (solverJob != null) {
            solverJob.addProblemChange(problemChange);
        } else {
            acceptScoreDirector(scoreDirector -> {
                DefaultProblemChangeDirector<Solution_> problemChangeDirector =
                        new DefaultProblemChangeDirector<>(scoreDirector);
                problemChangeDirector.doProblemChange(problemChange);
            });
        }
    }

    /**
     * Can be called on any thread.
     *
     * @param problem never null
     * @param bestSolutionConsumer never null
     * @return never null
     */
    public Solution_ solve(Solution_ problem, Consumer<Solution_> bestSolutionConsumer) {
        SolverJob<Solution_, Long> solverJob = solverManager.solveAndListen(SOLVER_JOB_ID_COUNTER.getAndIncrement(),
                id -> problem, bestSolution -> {
                    setSolution(bestSolution);
                    SwingUtilities.invokeLater(() -> {
                        // Skip this event if a newer one arrived meanwhile to avoid flooding the Swing Event thread.
                        Solution_ skipToBestSolution = getSolution();
                        if (bestSolution != skipToBestSolution) {
                            return;
                        }
                        bestSolutionConsumer.accept(bestSolution);
                    });
                });
        solverJobRef.set(solverJob);
        try {
            return solverJob.getFinalBestSolution();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Solver thread was interrupted.", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Solver threw an exception.", e);
        } finally {
            solverJobRef.set(null); // Don't keep references to jobs that have finished solving.
        }
    }

    public void terminateSolvingEarly() {
        SolverJob<Solution_, Long> solverJob = solverJobRef.get();
        if (solverJob != null) {
            solverJob.terminateEarly();
        }
    }

    @Override
    public void close() {
        terminateSolvingEarly();
        solverManager.close();
    }
}
