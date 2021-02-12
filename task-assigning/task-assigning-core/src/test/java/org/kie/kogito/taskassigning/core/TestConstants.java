/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.taskassigning.core;

public class TestConstants {

    /**
     * Tag for marking a test as a "turtle test". These tests are only executed when the "runTurtleTests" profile
     * is activated. It's up to the productization scripts to determine when this tests should be
     * executed or not. Developers can always trigger them locally if needed.
     */
    public static final String TURTLE_TEST = "turtleTest";

    /**
     * Tag for marking test as a "development only test". These tests are only executed when the "runDevelopmentOnlyTests"
     * profile is activated and should not be part of the productization scripts, since they are only useful for
     * developers to test stuff locally during development (e.g. for executing random operations)
     * Don't abuse with the use this tests.
     */
    public static final String DEVELOPMENT_ONLY_TEST = "developmentOnlyTest";

    private TestConstants() {
    }
}
