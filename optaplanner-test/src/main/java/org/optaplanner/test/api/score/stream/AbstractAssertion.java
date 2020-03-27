/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.test.api.score.stream;

public abstract class AbstractAssertion<Solution_, Assertion extends AbstractAssertion<Solution_, Assertion, Verifier>,
        Verifier extends AbstractConstraintVerifier<Solution_, Assertion, Verifier>> {

    private final Verifier parentConstraintVerifier;

    protected AbstractAssertion(Verifier constraintVerifier) {
        this.parentConstraintVerifier = constraintVerifier;
    }

    protected final Verifier getParentConstraintVerifier() {
        return parentConstraintVerifier;
    }

}
