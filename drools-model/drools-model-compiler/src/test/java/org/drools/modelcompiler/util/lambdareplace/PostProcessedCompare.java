package org.drools.modelcompiler.util.lambdareplace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;

public class PostProcessedCompare {

    final String prefix;

    public PostProcessedCompare(String prefix) {
        this.prefix = prefix;
    }

    public void compareIgnoringHash(String result, String expectedResult) {
        assertThat(replaceHash(prefix, result), equalToIgnoringWhiteSpace(replaceHash(prefix, expectedResult)));
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
