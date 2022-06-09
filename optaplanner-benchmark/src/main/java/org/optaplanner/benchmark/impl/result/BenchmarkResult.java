package org.optaplanner.benchmark.impl.result;

import java.io.File;

import org.optaplanner.core.api.score.Score;

public interface BenchmarkResult {

    String getName();

    /**
     * @return the name of the directory that holds the benchmark's results
     */
    String getResultDirectoryName();

    /**
     * @return the benchmark result directory as a file
     */
    File getResultDirectory();

    /**
     * @return true if there is a failed child benchmark and the variable is initialized
     */
    boolean hasAnyFailure();

    /**
     * @return true if all child benchmarks were a success and the variable is initialized
     */
    boolean hasAllSuccess();

    Score getAverageScore();

}
