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

package org.drools.mvel.integrationtests.waltz;


public class Junction {
    public static String TEE   = "tee";

    public static String FORK  = "fork";

    public static String ARROW = "arrow";

    public static String L     = "L";

    private int          p1;

    private int          p2;

    private int          p3;

    private int          basePoint;

    private String       type;

    public Junction() {

    }

    public Junction(final int p1,
                    final int p2,
                    final int p3,
                    final int basePoint,
                    final String type) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.basePoint = basePoint;
        this.type = type;
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
        return "{Junction p1=" + this.p1 + ", p2=" + this.p2 + ", p3=" + this.p3 + ", basePoint=" + this.basePoint + ", type=" + this.type + "}";
    }

    public int getBasePoint() {
        return this.basePoint;
    }

    public void setBasePoint(final int basePoint) {
        this.basePoint = basePoint;
    }

    public int getP3() {
        return this.p3;
    }

    public void setP3(final int p3) {
        this.p3 = p3;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
