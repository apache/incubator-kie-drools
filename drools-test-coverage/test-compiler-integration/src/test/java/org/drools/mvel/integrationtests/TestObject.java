/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

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
