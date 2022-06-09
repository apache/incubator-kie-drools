package org.optaplanner.examples.common.persistence;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.examples.common.business.SolutionBusiness;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractPngSolutionImporter<Solution_> extends AbstractSolutionImporter<Solution_> {

    private static final String DEFAULT_INPUT_FILE_SUFFIX = "png";

    @Override
    public String getInputFileSuffix() {
        return DEFAULT_INPUT_FILE_SUFFIX;
    }

    public abstract PngInputBuilder<Solution_> createPngInputBuilder();

    @Override
    public Solution_ readSolution(File inputFile) {
        try {
            BufferedImage image = ImageIO.read(inputFile);
            PngInputBuilder<Solution_> pngInputBuilder = createPngInputBuilder();
            pngInputBuilder.setInputFile(inputFile);
            pngInputBuilder.setImage(image);
            try {
                Solution_ solution = pngInputBuilder.readSolution();
                logger.info("Imported: {}", inputFile);
                return solution;
            } catch (IllegalArgumentException | IllegalStateException e) {
                throw new IllegalArgumentException("Exception in inputFile (" + inputFile + ")", e);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the file (" + inputFile.getName() + ").", e);
        }
    }

    public static abstract class PngInputBuilder<Solution_> extends InputBuilder {

        protected File inputFile;
        protected BufferedImage image;

        public void setInputFile(File inputFile) {
            this.inputFile = inputFile;
        }

        public void setImage(BufferedImage image) {
            this.image = image;
        }

        public abstract Solution_ readSolution() throws IOException;

        // ************************************************************************
        // Helper methods
        // ************************************************************************

        public String getInputId() {
            return SolutionBusiness.getBaseFileName(inputFile);
        }
    }

}
