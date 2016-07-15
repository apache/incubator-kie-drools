/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler;

/**
 * Serves as a JUnit's {@link org.junit.experimental.categories.Category} to mark turtle tests (tests which are slow
 * and should not be executed by default). These slow tests can be then enabled by defining property `runTurtleTests`
 */
public class TurtleTestCategory {
}
