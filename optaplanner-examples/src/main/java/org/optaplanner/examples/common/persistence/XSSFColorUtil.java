/*
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

package org.optaplanner.examples.common.persistence;

import java.awt.Color;

import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;

public final class XSSFColorUtil {

    private static final IndexedColorMap INDEXED_COLOR_MAP = new DefaultIndexedColorMap();

    public static XSSFColor getXSSFColor(Color awtColor) {
        byte[] rgb = new byte[] {
                intToByte(awtColor.getRed()),
                intToByte(awtColor.getGreen()),
                intToByte(awtColor.getBlue())
        };
        return new XSSFColor(rgb, INDEXED_COLOR_MAP);
    }

    private static byte intToByte(int integer) {
        return ((Integer) integer).byteValue();
    }

    private XSSFColorUtil() {
        // No external instances.
    }

}
