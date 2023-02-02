package org.optaplanner.examples.common.persistence;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.examples.common.app.LoggingMain;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractSolutionImporter<Solution_> extends LoggingMain {

    public boolean acceptInputFile(File inputFile) {
        return inputFile.getName().endsWith("." + getInputFileSuffix());
    }

    public abstract String getInputFileSuffix();

    public abstract Solution_ readSolution(File inputFile);

    public static abstract class InputBuilder extends LoggingMain {

    }

    public static BigInteger factorial(int base) {
        if (base > 100000) {
            // Calculation takes too long
            return null;
        }
        BigInteger value = BigInteger.ONE;
        for (int i = 1; i <= base; i++) {
            value = value.multiply(BigInteger.valueOf(i));
        }
        return value;
    }

    public static String getFlooredPossibleSolutionSize(BigInteger possibleSolutionSize) {
        if (possibleSolutionSize == null) {
            return null;
        }
        if (possibleSolutionSize.compareTo(BigInteger.valueOf(1000L)) < 0) {
            return possibleSolutionSize.toString();
        }
        BigDecimal possibleSolutionSizeBigDecimal = new BigDecimal(possibleSolutionSize);
        int decimalDigits = possibleSolutionSizeBigDecimal.scale() < 0
                ? possibleSolutionSizeBigDecimal.precision() - possibleSolutionSizeBigDecimal.scale()
                : possibleSolutionSizeBigDecimal.precision();
        return "10^" + decimalDigits;
    }

}
