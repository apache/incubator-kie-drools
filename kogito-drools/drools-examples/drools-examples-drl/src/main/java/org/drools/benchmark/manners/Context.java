/*
 * Copyright 2005 JBoss Inc
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

package org.drools.benchmark.manners;


import java.io.Serializable;

public class Context implements Serializable {

    private static final long serialVersionUID = 510l;

    public static final int START_UP = 0;

    public static final int ASSIGN_SEATS = 1;

    public static final int MAKE_PATH = 2;

    public static final int CHECK_DONE = 3;

    public static final int PRINT_RESULTS = 4;

    public static final String[] stateStrings = { "START_UP", "ASSIGN_SEATS",
        	"MAKE_PATH", "CHECK_DONE", "PRINT_RESULTS" };

    private int state;

    public Context() {

    }

    public Context(String state) {
        if ("start".equals(state)) {
        	this.state = Context.START_UP;
        } else {
        	throw new RuntimeException("Context '" + state
        			+ "' does not exist for Context Enum");
        }
    }

    public Context(int state) {
        this.state = state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isState(int state) {
        return this.state == state;
    }

    public int getState() {
        return this.state;
    }

    public String getStringValue() {
        return stateStrings[this.state];
    }

    public String toString() {
        return "[Context state=" + getStringValue() + "]";
    }
}
