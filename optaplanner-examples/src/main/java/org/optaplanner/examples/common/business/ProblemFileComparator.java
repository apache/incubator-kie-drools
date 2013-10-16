package org.optaplanner.examples.common.business;

import java.io.File;
import java.util.Comparator;
import java.util.Locale;

import org.apache.commons.lang.builder.CompareToBuilder;

public class ProblemFileComparator implements Comparator<File> {

    private static final AlphaNumericStringComparator ALPHA_NUMERIC_STRING_COMPARATOR = new AlphaNumericStringComparator();

    public int compare(File a, File b) {
        String aLowerCaseName = a.getName().toLowerCase(Locale.US);
        String bLowerCaseName = b.getName().toLowerCase(Locale.US);
        return new CompareToBuilder()
                .append(a.getParent(), b.getParent(), ALPHA_NUMERIC_STRING_COMPARATOR)
                .append(a.isDirectory(), b.isDirectory())
                .append(!aLowerCaseName.startsWith("demo"), !bLowerCaseName.startsWith("demo"))
                .append(aLowerCaseName, bLowerCaseName, ALPHA_NUMERIC_STRING_COMPARATOR)
                .append(a.getName(), b.getName())
                .toComparison();
    }

}
