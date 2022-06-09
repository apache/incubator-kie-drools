package org.optaplanner.examples.common.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractTxtSolutionExporter<Solution_> extends AbstractSolutionExporter<Solution_> {

    protected static final String DEFAULT_OUTPUT_FILE_SUFFIX = "txt";

    @Override
    public String getOutputFileSuffix() {
        return DEFAULT_OUTPUT_FILE_SUFFIX;
    }

    public abstract TxtOutputBuilder<Solution_> createTxtOutputBuilder();

    @Override
    public void writeSolution(Solution_ solution, File outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"))) {
            TxtOutputBuilder<Solution_> txtOutputBuilder = createTxtOutputBuilder();
            txtOutputBuilder.setBufferedWriter(writer);
            txtOutputBuilder.setSolution(solution);
            txtOutputBuilder.writeSolution();
            logger.info("Exported: {}", outputFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not write the file (" + outputFile.getName() + ").", e);
        }
    }

    public static abstract class TxtOutputBuilder<Solution_> extends OutputBuilder {

        protected BufferedWriter bufferedWriter;
        protected Solution_ solution;

        public void setBufferedWriter(BufferedWriter bufferedWriter) {
            this.bufferedWriter = bufferedWriter;
        }

        public void setSolution(Solution_ solution) {
            this.solution = solution;
        }

        public abstract void writeSolution() throws IOException;

        // ************************************************************************
        // Helper methods
        // ************************************************************************

    }

}
