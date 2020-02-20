package org.drools.modelcompiler.util.lambdareplace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;

public class PostProcessedCompare {

    public void compareIgnoringHash(String result, String expectedResult) {
        assertThat(replaceHash(result), equalToIgnoringWhiteSpace(replaceHash(expectedResult)));
    }

    public static String replaceHash(String string) {
        Pattern regexp = Pattern.compile("([A-Z|\\d]{32})(\\s|\")");

        Matcher matcher = regexp.matcher(string);

        boolean found = matcher.find();
        if (found) {
            return matcher.replaceAll("");
        } else {
            return string;
        }
    }
}
