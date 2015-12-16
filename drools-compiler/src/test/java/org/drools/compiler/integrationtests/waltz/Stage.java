/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.waltz;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Stage
    implements
    Externalizable {
    private static final long serialVersionUID      = 510l;

    final public static int   START                 = 0;

    final public static int   DUPLICATE             = 1;

    final public static int   DETECT_JUNCTIONS      = 2;

    final public static int   FIND_INITIAL_BOUNDARY = 3;

    final public static int   FIND_SECOND_BOUNDARY  = 4;

    final public static int   LABELING              = 5;

    final public static int   PLOT_REMAINING_EDGES  = 9;

    final public static int   DONE                  = 10;

    private int               value;

    public Stage() {

    }

    public Stage(final int value) {
        this.value = value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        value   = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(value);
    }
    public int getValue() {
        return this.value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public static int resolveStageValue(final String str) {
        if ( str.equals( "start" ) ) {
            return 0;

        } else if ( str.equals( "duplicate" ) ) {
            return 1;

        } else if ( str.equals( "detect_junctions" ) ) {
            return 2;

        } else if ( str.equals( "find_initial_boundary" ) ) {
            return 3;

        } else if ( str.equals( "find_second_boundary" ) ) {
            return 4;

        } else if ( str.equals( "labeling" ) ) {
            return 5;

        } else if ( str.equals( "plot_remaining_edges" ) ) {
            return 9;

        } else if ( str.equals( "done" ) ) {
            return 10;
        } else {
            return -9999999;
        }
    }

    public String toString() {
        return "{Stage value=" + this.value + "}";
    }
}
