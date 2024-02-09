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
package org.kie.dmn.feel.runtime.functions;

/**
 * DMN v1.2 Table 66: Semantics of conversion functions
 */
public class FEELConversionFunctionNames {

    public static final String DATE = "date";
    public static final String DATE_AND_TIME = "date and time";
    public static final String TIME = "time";
    public static final String NUMBER = "number";
    public static final String STRING = "string";
    public static final String DURATION = "duration";
    public static final String YEARS_AND_MONTHS_DURATION = "years and months duration";

    private FEELConversionFunctionNames() {
        // Not allowed for util classes.
    }
}
