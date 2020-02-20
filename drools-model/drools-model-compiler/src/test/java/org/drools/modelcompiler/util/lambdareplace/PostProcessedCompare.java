package org.drools.modelcompiler.util.lambdareplace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;

public class PostProcessedCompare {

    final String[] prefixes;

    public PostProcessedCompare(String... prefixes) {
        this.prefixes = prefixes;
    }

    public void compareIgnoringHash(String result, String expectedResult) {
        String actual = result;
        String expectedString = expectedResult;
        for (String p : prefixes) {
            actual = replaceHash(p, actual);
            expectedString = replaceHash(p, expectedString);
        }
        assertThat(actual, equalToIgnoringWhiteSpace(expectedString));
    }

    public static String replaceHash(String prefix, String string) {
        Pattern regexp = Pattern.compile(prefix + "([A-Z|\\d]+)");

        Matcher matcher = regexp.matcher(string);

        boolean b = matcher.find();
        if (!b) {
            throw new RuntimeException();
        }

        return matcher.replaceAll(prefix);
    }
}
