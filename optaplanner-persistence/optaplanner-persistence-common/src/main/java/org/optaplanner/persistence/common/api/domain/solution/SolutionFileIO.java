package org.optaplanner.persistence.common.api.domain.solution;

import java.io.File;

import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * Reads or writes a {@link PlanningSolution} from or to a {@link File}.
 * <p>
 * An implementation must be thread-safe.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface SolutionFileIO<Solution_> {

    /**
     * Every {@link PlanningSolution} type potentially has its own file extension.
     * If no specific file extension is defined by the use case, the following are recommended:
     * <ul>
     * <li>If this {@link SolutionFileIO} implementation serializes to XML, use file extension "xml".</li>
     * <li>If this {@link SolutionFileIO} implementation serializes to text, use file extension "txt".</li>
     * <li>If this {@link SolutionFileIO} implementation serializes to binary, use file extension "dat".</li>
     * </ul>
     * <p>
     * It's good practice that both the input and the output file have the same file extension,
     * because a good output file is able to function as an input file.
     * Therefore {@link #getOutputFileExtension} defaults to returning the same as this method.
     * <p>
     * The file extension does not include the dot that separates it from the base name.
     * <p>
     * This method is thread-safe.
     *
     * @return never null, for example "xml"
     */
    String getInputFileExtension();

    /**
     * It's highly recommended that this method returns the same value as {@link #getInputFileExtension()},
     * which it does by default unless it's overridden,
     * because a good output file is able to function as an input file.
     *
     * @return never null, for example "xml"
     * @see #getInputFileExtension()
     */
    default String getOutputFileExtension() {
        return getInputFileExtension();
    }

    /**
     * This method is thread-safe.
     *
     * @param inputSolutionFile never null
     * @return never null
     */
    Solution_ read(File inputSolutionFile);

    /**
     * This method is thread-safe.
     *
     * @param solution never null
     * @param outputSolutionFile never null, parent directory already exists
     */
    void write(Solution_ solution, File outputSolutionFile);

}
