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
package org.drools.examples.manners;

import java.io.Serializable;

public class Context
    implements
    Serializable {

    /**
     * 
     */
    private static final long    serialVersionUID = -5876473269153584875L;
    public static final Integer      START_UP         = new Integer(0);
    public static final Integer      ASSIGN_SEATS     = new Integer(1);
    public static final Integer      MAKE_PATH        = new Integer(2);
    public static final Integer      CHECK_DONE       = new Integer(3);
    public static final Integer      PRINT_RESULTS    = new Integer(4);

    public static final String[] stateStrings     = {"START_UP", "ASSIGN_SEATS", "MAKE_PATH", "CHECK_DONE", "PRINT_RESULTS"};

    private Integer                  state;
    
    public Context() {
    }

    public Context(final String state) {
        if ( "start".equals( state ) ) {
            this.state = Context.START_UP;
        } else {
            throw new RuntimeException( "Context '" + state + "' does not exist for Context Enum" );
        }
    }

    public Context(final Integer state) {
        this.state = state;
    }

    public void setState(final Integer state) {
        this.state = state;
    }

    public boolean isState(final Integer state) {
        return this.state == state;
    }

    public Integer getState() {
        return this.state;
    }

    public String getStringValue() {
        return Context.stateStrings[this.state.intValue()];
    }

    public String toString() {
        return "[Context state=" + getStringValue() + "]";
    }
}