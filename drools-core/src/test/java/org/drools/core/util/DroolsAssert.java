package org.drools.core.util;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DroolsAssert {

    public static void assertEnumerationSize(int expectedSize, Enumeration<?> enumeration) {
        int actualSize = 0;
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement();
            actualSize++;
        }
        assertThat(actualSize).as("Enumeration size different than expected.").isEqualTo(expectedSize);
    }

    public static void assertUrlEnumerationContainsMatch(String regex, Enumeration<URL> enumeration) {
        List<URL> list = Collections.list(enumeration);
        for (URL url : list) {
            if (url.toExternalForm().matches(regex)) {
                return;
            }
        }
        throw new AssertionError("The enumeration (" + list
                + ") does not contain an URL that matches regex (" + regex + ").");
    }

    private DroolsAssert() {
    }

}
