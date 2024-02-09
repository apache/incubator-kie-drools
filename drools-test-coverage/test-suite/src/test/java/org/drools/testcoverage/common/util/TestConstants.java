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
package org.drools.testcoverage.common.util;

/**
 * Various constants used in tests.
 */
public final class TestConstants {

    public static final String PACKAGE_TESTCOVERAGE = "org.drools.testcoverage";
    public static final String PACKAGE_TESTCOVERAGE_MODEL = "org.drools.testcoverage.common.model";
    public static final String PACKAGE_REGRESSION = PACKAGE_TESTCOVERAGE + ".regression";
    public static final String PACKAGE_FUNCTIONAL = PACKAGE_TESTCOVERAGE + ".functional";

    public static final String TEST_RESOURCES_FOLDER = "src/main/resources/";
    public static final String DRL_TEST_TARGET_PATH = TEST_RESOURCES_FOLDER + "rule.drl";


    private TestConstants() {
        // Creating instances of util classes should not be possible.
    }
}
