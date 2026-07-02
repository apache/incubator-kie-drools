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

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.provider.Arguments;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.impl.testutil.DisabledInProductizationCheck;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

/**
 * @see ConstraintProviderTest
 */
@TestInstance(PER_CLASS)
@DisplayNameGeneration(SimplifiedTestNameGenerator.class)
public abstract class AbstractConstraintProviderTest<ConstraintProvider_ extends ConstraintProvider, Solution_> {

    private final ConstraintVerifier<ConstraintProvider_, Solution_> bavetConstraintVerifier = createConstraintVerifier()
            .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
    private final ConstraintVerifier<ConstraintProvider_, Solution_> droolsWithoutAncConstraintVerifier =
            createConstraintVerifier()
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS)
                    .withDroolsAlphaNetworkCompilationEnabled(false);
    private final ConstraintVerifier<ConstraintProvider_, Solution_> droolsWithAncConstraintVerifier =
            createConstraintVerifier()
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS)
                    .withDroolsAlphaNetworkCompilationEnabled(true);

    protected abstract ConstraintVerifier<ConstraintProvider_, Solution_> createConstraintVerifier();

    protected final Stream<? extends Arguments> getDroolsAndBavetConstraintVerifierImpls() {
        if (DisabledInProductizationCheck.isProductized()) {
            return Stream.of(
                    arguments(named("DROOLS (without ANC)", droolsWithoutAncConstraintVerifier)),
                    arguments(named("DROOLS (with ANC)", droolsWithAncConstraintVerifier)));
        } else {
            return Stream.of(
                    arguments(named("BAVET", bavetConstraintVerifier)),
                    arguments(named("DROOLS (without ANC)", droolsWithoutAncConstraintVerifier)),
                    arguments(named("DROOLS (with ANC)", droolsWithAncConstraintVerifier)));
        }
    }
}
