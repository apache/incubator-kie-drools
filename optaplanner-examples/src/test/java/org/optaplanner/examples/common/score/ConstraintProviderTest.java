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

package org.optaplanner.examples.common.score;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests using {@link org.optaplanner.test.api.score.stream.ConstraintVerifier} should use this annotation
 * instead of @{@link org.junit.jupiter.api.Test}.
 * This brings several benefits, such as parallel execution and testing on both Bavet and Drools.
 *
 * <p>
 * Each such test expects exactly one argument of type {@link org.optaplanner.test.api.score.stream.ConstraintVerifier}.
 * Values for that argument are read from {@link AbstractConstraintProviderTest#getDroolsAndBavetConstraintVerifierImpls()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Execution(ExecutionMode.CONCURRENT)
@ParameterizedTest(name = "constraintStreamImplType = {0}")
@MethodSource("getDroolsAndBavetConstraintVerifierImpls")
public @interface ConstraintProviderTest {
}
