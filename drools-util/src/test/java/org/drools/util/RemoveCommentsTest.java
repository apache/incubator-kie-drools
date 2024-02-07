package org.drools.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoveCommentsTest {

    @Test
    public void test() {
        String result = RemoveCommentsMain.removeComments("src/test/resources/commented.properties", false);
        String expected = "provides-capabilities=org.drools.drl\ndeployment-artifact=org.drools\\:drools-quarkus-deployment\\:999-SNAPSHOT";
        assertEquals(expected, result);
    }
}
