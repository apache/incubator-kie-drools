/**
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

package org.drools.examples.manners;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Context
    implements
    Externalizable {

    /**
     *
     */
    private static final long    serialVersionUID = 400L;
    public static final int      START_UP         = 0;
    public static final int      ASSIGN_SEATS     = 1;
    public static final int      MAKE_PATH        = 2;
    public static final int      CHECK_DONE       = 3;
    public static final int      PRINT_RESULTS    = 4;

    public static final String[] stateStrings     = {"START_UP", "ASSIGN_SEATS", "MAKE_PATH", "CHECK_DONE", "PRINT_RESULTS"};

    private int                  state;

    public Context() {
    }

    public Context(final String state) {
        if ( "start".equals( state ) ) {
            this.state = Context.START_UP;
        } else {
            throw new RuntimeException( "Context '" + state + "' does not exist for Context Enum" );
        }
    }

    public Context(final int state) {
        this.state = state;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        state   = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(state);
    }

    public void setState(final int state) {
        this.state = state;
    }

    public boolean isState(final int state) {
        return this.state == state;
    }

    public int getState() {
        return this.state;
    }

    public String getStringValue() {
        return Context.stateStrings[this.state];
    }

    public String toString() {
        return "[Context state=" + getStringValue() + "]";
    }
}