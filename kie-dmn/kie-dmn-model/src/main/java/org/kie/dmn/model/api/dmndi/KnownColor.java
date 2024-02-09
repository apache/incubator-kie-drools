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
package org.kie.dmn.model.api.dmndi;

public enum KnownColor {


    /**
     * a color with a value of #800000
     * 
     */
    MAROON("maroon"),

    /**
     * a color with a value of #FF0000
     * 
     */
    RED("red"),

    /**
     * a color with a value of #FFA500
     * 
     */
    ORANGE("orange"),

    /**
     * a color with a value of #FFFF00
     * 
     */
    YELLOW("yellow"),

    /**
     * a color with a value of #808000
     * 
     */
    OLIVE("olive"),

    /**
     * a color with a value of #800080
     * 
     */
    PURPLE("purple"),

    /**
     * a color with a value of #FF00FF
     * 
     */
    FUCHSIA("fuchsia"),

    /**
     * a color with a value of #FFFFFF
     * 
     */
    WHITE("white"),

    /**
     * a color with a value of #00FF00
     * 
     */
    LIME("lime"),

    /**
     * a color with a value of #008000
     * 
     */
    GREEN("green"),

    /**
     * a color with a value of #000080
     * 
     */
    NAVY("navy"),

    /**
     * a color with a value of #0000FF
     * 
     */
    BLUE("blue"),

    /**
     * a color with a value of #00FFFF
     * 
     */
    AQUA("aqua"),

    /**
     * a color with a value of #008080
     * 
     */
    TEAL("teal"),

    /**
     * a color with a value of #000000
     * 
     */
    BLACK("black"),

    /**
     * a color with a value of #C0C0C0
     * 
     */
    SILVER("silver"),

    /**
     * a color with a value of #808080
     * 
     */
    GRAY("gray");
    private final String value;

    KnownColor(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KnownColor fromValue(String v) {
        for (KnownColor c: KnownColor.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
