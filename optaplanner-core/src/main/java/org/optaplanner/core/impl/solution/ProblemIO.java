/*
 * Copyright 2011 JBoss Inc
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

package org.optaplanner.core.impl.solution;

import java.io.File;

/**
 * Reads or writes a {@link Solution} from or to a {@link File}.
 * <p/>
 * Every implementation must be thread-safe.
 */
public interface ProblemIO {

    /**
     * Every {@link Solution} type potentially has its own file extension.
     * If no specific file extension is defined by the use case, the following are recommended:
     * <ul>
     *     <li>If this {@link ProblemIO} implementation serializes to XML, use file extension "xml".</li>
     *     <li>If this {@link ProblemIO} implementation serializes to text, use file extension "txt".</li>
     *     <li>If this {@link ProblemIO} implementation serializes to binary, use file extension "dat".</li>
     * </ul>
     * <p/>
     * It's good practice that both the input and the output file have the same file extension,
     * because an output file should be able to function as an input file.
     * If that isn't the case, this method should return the output file extension.
     * <p/>
     * The file extension does not include the dot that separates it from the base name.
     * <p/>
     * This method is thread-safe.
     * @return never null, for example "xml"
     */
    String getFileExtension();

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
