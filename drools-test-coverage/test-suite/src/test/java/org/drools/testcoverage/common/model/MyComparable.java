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
package org.drools.testcoverage.common.model;

public class MyComparable implements Comparable<MyComparable> {

    private String strValue;
    private Integer intValue;

    public static final MyComparable ABC = new MyComparable("ABC", 1);
    public static final MyComparable DEF = new MyComparable("DEF", 1);
    public static final MyComparable GHI = new MyComparable("GHI", 1);
    public static final MyComparable JKL = new MyComparable("JKL", 1);
    public static final MyComparable MNO = new MyComparable("MNO", 1);
    public static final MyComparable PQR = new MyComparable("PQR", 1);

    public MyComparable() {
    }

    public MyComparable(String strValue, Integer intValue) {
        this.strValue = strValue;
        this.intValue = intValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    @Override
    public int compareTo(MyComparable o) {
        MyComparable other = o;
        int result = this.strValue.compareTo(other.strValue);
        if (result != 0) {
            return result;
        }
        return this.intValue.compareTo(other.intValue);
    }
}
