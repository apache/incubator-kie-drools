/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmark.waltzdb;

public class Stage {
    final public static String DUPLICATE = "A";
    final public static String DETECT_JUNCTIONS = "B";
    final public static String FIND_INITIAL_BOUNDARY = "C";
    final public static String FIND_SECOND_BOUDARY = "D";
    final public static String LABELING = "E";
    final public static String VISITING_3J = "F";
    final public static String VISITING_2J = "G";
    final public static String MARKING = "H";
    final public static String CHECKING = "I";
    final public static String REMOVE_LABEL = "J";
    final public static String PRINTING = "K";

    private String value;

    public Stage() {
        super();
    }

    public Stage(String value) {
        super();
        this.value = value;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
        	return true;
        if (obj == null)
        	return false;
        if (getClass() != obj.getClass())
        	return false;
        final Stage other = (Stage) obj;
        if (value == null) {
        	if (other.value != null)
        		return false;
        } else if (!value.equals(other.value))
        	return false;
        return true;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
