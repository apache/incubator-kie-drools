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

public class Edge {
    private int                p1;

    private int                p2;

    private boolean            joined;

    private String             label;

    private boolean            plotted;

    final public static String NIL   = "empty";

    final public static String B     = "B";

    final public static String PLUS  = "+";

    final public static String MINUS = "-";

    public Edge() {

    }

    public Edge(final int p1,
                final int p2,
                final boolean joined,
                final String label,
                final boolean plotted) {
        this.p1 = p1;
        this.p2 = p2;
        this.joined = joined;
        this.label = label;
        this.plotted = plotted;
    }

    public int getP1() {
        return this.p1;
    }

    public void setP1(final int p1) {
        this.p1 = p1;
    }

    public int getP2() {
        return this.p2;
    }

    public void setP2(final int p2) {
        this.p2 = p2;
    }

    public String toString() {
        return "( Edge p1=" + this.p1 + ", p2=" + this.p2 + ", joined=" + this.joined + ", label=" + this.label + ", plotted=" + this.plotted + " )";
    }

    public boolean isJoined() {
        return this.joined;
    }

    public void setJoined(final boolean joined) {
        this.joined = joined;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public boolean getPlotted() {
        return this.plotted;
    }

    public void setPlotted(final boolean plotted) {
        this.plotted = plotted;
    }
}
