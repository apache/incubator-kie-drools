package org.optaplanner.core.impl.util;

import java.util.Random;

import org.junit.Test;

public class RandomUtilsTest {

    @Test
    public void testNextLong() throws Exception {
        Random random = new Random(37);
        RandomUtils.nextLong(random, 10L + (long) Integer.MAX_VALUE);
    }

}
