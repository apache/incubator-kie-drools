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
package org.drools.util;

/**
 * Utility class for handling float to double conversions with precision cleanup.
 */
public class FloatHelper {

    /**
     * Cleans up precision artifacts when converting float to double.
     * Rounds to approximately 7 significant digits (float precision).
     * 
     * @param d the double value to clean up
     * @return the cleaned double value
     */
    public static double cleanDouble(double d) {
        // Round to ~7 significant digits (float precision)
        return Math.round(d * 1e7) / 1e7;
    }
}