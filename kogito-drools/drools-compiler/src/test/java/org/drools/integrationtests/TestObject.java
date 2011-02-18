package org.drools.integrationtests;

import java.util.Arrays;
import java.util.List;

public class TestObject {
    private List list;

    public TestObject(List list) {
        this.list = list;
    }

    public boolean checkHighestPriority(String promoType,
                                        long priority) {
        this.list.add( "TestObject.checkHighestPriority: " + promoType + '|' + priority );
        return true;
    }

    public boolean stayHasDaysOfWeek(String daysOfWeek,
                                     boolean allDaysRequired,
                                     String[][] days) {
        this.list.add( "TestObject.stayHasDaysOfWeek: " + daysOfWeek + '|' + allDaysRequired + '|' + Arrays.toString( days[0] ) );
        return true;
    }

    public void applyValueAddPromo(long aRuleId,
                                   int aRuleVersion,
                                   long aValueAddDctCode,
                                   int aPromoType,
                                   String aPromoCode) {
        this.list.add( "TestObject.applyValueAddPromo: " + aRuleId + '|' + aRuleVersion + '|' + aValueAddDctCode + '|' + aPromoType + '|' + aPromoCode );
    }

    public static String[][] array(String arg1,
                                   String arg2) {
        return new String[][]{{arg1, arg2}};
    }
}
