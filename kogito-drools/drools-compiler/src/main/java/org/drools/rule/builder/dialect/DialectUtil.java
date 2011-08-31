package org.drools.rule.builder.dialect;

import org.drools.commons.jci.readers.*;

import java.util.regex.*;

public final class DialectUtil {

    private static final Pattern NON_ALPHA_REGEX = Pattern.compile("[ -/:-@\\[-`\\{-\\xff]");

    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     * <p/>
     *
     * @param packageName
     * @param name
     * @param ext
     * @return
     */
    public static String getUniqueLegalName(final String packageName,
                                            final String name,
                                            final String ext,
                                            final String prefix,
                                            final ResourceReader src) {
        // replaces all non alphanumeric or $ chars with _
        final String newName = prefix + "_" + NON_ALPHA_REGEX.matcher(name).replaceAll("_");
        final String fileName = packageName.replace('.', '/') + "/" + newName;

        if (src == null || !src.isAvailable(fileName + "." + ext)) return newName;

        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        while (true) {

            counter++;
            final String actualName = fileName + "_" + counter + "." + ext;

            //MVEL:test null to Fix failing test on org.drools.rule.builder.dialect.mvel.MVELConsequenceBuilderTest.testImperativeCodeError()
            if (!src.isAvailable(actualName)) break;
        }
        // we have duplicate file names so append counter
        return newName + "_" + counter;
    }

}
