/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.common.api.domain.solution;

import java.io.File;

import org.optaplanner.core.api.domain.solution.Solution;

/**
 * Reads or writes a {@link Solution} from or to a {@link File}.
 * <p>
 * An implementation must be thread-safe.
 */
public interface SolutionFileIO {

    /**
     * It's highly recommended that this method returns the same value as {@link #getOutputFileExtension()},
     * because a good output file is able to function as an input file.
     * @return never null, for example "xml"
     * @see #getOutputFileExtension()
     */
    String getInputFileExtension();

    /**
     * Every {@link Solution} type potentially has its own file extension.
     * If no specific file extension is defined by the use case, the following are recommended:
     * <ul>
     *     <li>If this {@link SolutionFileIO} implementation serializes to XML, use file extension "xml".</li>
     *     <li>If this {@link SolutionFileIO} implementation serializes to text, use file extension "txt".</li>
     *     <li>If this {@link SolutionFileIO} implementation serializes to binary, use file extension "dat".</li>
     * </ul>
     * <p>
     * It's good practice that both the input and the output file have the same file extension,
     * because a good output file is able to function as an input file.
     * <p>
     * The file extension does not include the dot that separates it from the base name.
     * <p>
     * This method is thread-safe.
     * @return never null, for example "xml"
     */
    String getOutputFileExtension();

    /**
     * This method is thread-safe.
     * @param inputSolutionFile never null
     * @return never null
     */
    Solution read(File inputSolutionFile);

    /**
     * This method is thread-safe.
     * @param solution never null
     * @param outputSolutionFile never null, parent directory already exists
     */
    void write(Solution solution, File outputSolutionFile);

}
