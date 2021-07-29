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
package org.kie.kogito.process.validation;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationContextTest {

    public static final String RESOURCE_ID = UUID.randomUUID().toString();
    private ValidationContext tested;

    private ValidationError error;

    private ValidationError error2;
    private RuntimeException exception;

    @BeforeEach
    private void init() {
        tested = ValidationContext.get();
        error = createError();
        error2 = createError();
        exception = new RuntimeException("Exception");
    }

    @Test
    void testValidationContext() {
        tested.add(RESOURCE_ID, error);
        tested.add(RESOURCE_ID, error2);
        tested.putException(exception);
        assertThat(tested.hasErrors(RESOURCE_ID)).isTrue();
        assertThat(tested.errors(RESOURCE_ID)).contains(error, error2);
        assertThat(tested.resourcesWithError()).containsOnly(RESOURCE_ID);
        assertThat(tested.resourcesWithError()).containsOnly(RESOURCE_ID);
        assertThat(tested.exception()).isPresent();
        assertThat(tested.exception()).contains(exception);
        tested.clear();
        assertThat(tested.hasErrors(RESOURCE_ID)).isFalse();
        assertThat(tested.resourcesWithError()).isEmpty();
        assertThat(tested.exception()).isNotPresent();
    }

    @Test
    void testValidationContextConcurrency() throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(
                testAddErrorAsync(UUID.randomUUID().toString()),
                testAddErrorAsync(UUID.randomUUID().toString()),
                testAddErrorAsync(UUID.randomUUID().toString()))
                .get();//wait for completion
    }

    private ValidationError createError() {
        return new ValidationError() {

            private String message = UUID.randomUUID().toString();

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public int hashCode() {
                // TODO to revisit hashCode and equals methods
                return getMessage().hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                // TODO to revisit hashCode and equals methods
                return getMessage().equals(((ValidationError) obj).getMessage());
            }
        };
    }

    private CompletableFuture<Void> testAddErrorAsync(String id) {
        return CompletableFuture.runAsync(() -> {
            ValidationError localError = createError();
            tested.add(id, localError);
            IllegalStateException localException = new IllegalStateException("Local Exception");
            tested.putException(localException);
            assertThat(tested.hasErrors()).isTrue();
            assertThat(tested.hasErrors(id)).isTrue();
            assertThat(tested.hasErrors(RESOURCE_ID)).isFalse();
            assertThat(tested.errors(id)).containsOnly(localError);
            assertThat(tested.errors(id)).doesNotContain(error);
            assertThat(tested.exception()).isPresent();
            assertThat(tested.exception()).contains(localException);
            tested.clear();
            assertThat(tested.hasErrors()).isFalse();
            assertThat(tested.exception()).isNotPresent();
        });
    }
}
