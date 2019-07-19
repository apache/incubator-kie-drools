package org.drools.core.util;

import java.util.Random;

import org.junit.Test;

import static org.drools.core.util.BitMaskUtil.toBaseInt;
import static org.drools.core.util.BitMaskUtil.toExtendedInt;
import static org.drools.core.util.BitMaskUtil.toLong;
import static org.junit.Assert.assertEquals;

public class BitMaskUtilTest {

    @Test
    public void test() {
        Random random = new Random();
        for (int i = 0; i < 1_000; i++) {
            long l = random.nextLong();
            long result = toLong( toBaseInt( l ), toExtendedInt( l ) );
            assertEquals(l, result);
        }
    }
}