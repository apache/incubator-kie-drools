package org.drools.core.util;


import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testFindEndOfMethodArgsIndex() {
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"myId\")", 12);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"myId\").call()", 12);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('myId')", 12);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('myId').call()", 12);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id\")", 13);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id\").call()", 13);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id'\")", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id'\").call()", 14);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('my\"Id\"')", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('my\"Id\"').call()", 14);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('my\"Id')", 13);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('my\"Id').call()", 13);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\\\"Id\")", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\\\"Id\").call()", 14);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('myId', 'something')", 25);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"myId\", \"something\")", 25);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id\", \"somet'hing\")", 27);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id'\", \"somet'hing\")", 28);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'(Id\", \"somet'(hing'\")", 30);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setObject(new Object())", 22);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setObject(new Object(\"string param\"))", 36);
    }

    private void findEndOfMethodArgsIndexAndAssertItEqualsToExpected(String strExpr, int expectedIndex) {
        int actualIndex = StringUtils.findEndOfMethodArgsIndex(strExpr, strExpr.indexOf('('));
        Assert.assertEquals("Expected and actual end of method args index for expr '" + strExpr + "' are not equal!",
                expectedIndex, actualIndex);
    }
}
