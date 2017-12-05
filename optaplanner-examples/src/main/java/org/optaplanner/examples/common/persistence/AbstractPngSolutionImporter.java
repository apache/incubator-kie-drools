/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.persistence;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.core.api.domain.solution.PlanningSolution;

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
            return FilenameUtils.getBaseName(inputFile.getPath());
        }
    }

}
