package org.optaplanner.examples.common.business;

import java.util.Comparator;

import org.junit.Test;

import static org.junit.Assert.*;

public class AlphaNumericStringComparatorTest {

    @Test
    public void compare() {
        AlphaNumericStringComparator comparator = new AlphaNumericStringComparator();
        assertCompareEquals(comparator, "aaa", "aaa");
        assertCompareLower(comparator, "aaa", "aaaa");
        assertCompareLower(comparator, "aaa", "aba");
        assertCompareLower(comparator, "aaa", "ba");

        assertCompareEquals(comparator, "a1", "a1");
        assertCompareEquals(comparator, "a123", "a123");
        assertCompareLower(comparator, "a1", "a2");
        assertCompareLower(comparator, "a2", "a10");
        assertCompareLower(comparator, "a99", "a100");
        assertCompareLower(comparator, "2", "10");
        assertCompareLower(comparator, "2a", "10a");
        assertCompareLower(comparator, "a-2", "a-10");
        assertCompareLower(comparator, "a-2.5", "a-10.0");
        assertCompareLower(comparator, "a-0.5", "a-0.6");
    }

    public <T> void assertCompareEquals(Comparator<T> comparator, T a, T b) {
        assertEquals(0, comparator.compare(a, b));
        assertEquals(0, comparator.compare(b, a));
    }

    public <T> void assertCompareLower(Comparator<T> comparator, T a, T b) {
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

}
