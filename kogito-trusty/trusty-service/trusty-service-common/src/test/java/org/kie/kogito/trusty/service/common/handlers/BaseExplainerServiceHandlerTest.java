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
package org.kie.kogito.trusty.service.common.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseExplainerServiceHandlerTest<H extends ExplainerServiceHandler<R, D>, R extends BaseExplainabilityResult, D extends BaseExplainabilityResultDto> {

    protected static final String EXECUTION_ID = "executionId";

    protected static final String FAILURE_MESSAGE = "Something went wrong";

    protected H handler;

    protected TrustyStorageService storageService;

    protected Storage<String, R> storage;

    protected R result;

    protected Decision decision;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        this.storage = mock(Storage.class);
        this.storageService = mock(TrustyStorageService.class);
        this.handler = getHandler();
        this.result = mock(getResult());
        this.decision = mock(Decision.class);

        setupMockStorage();
    }

    protected abstract H getHandler();

    protected abstract Class<R> getResult();

    protected abstract Class<D> getResultDto();

    protected abstract void setupMockStorage();

    @Test
    public void testExplainabilityResult() {
        assertTrue(handler.supports(getResult()));
        assertFalse(handler.supports(BaseExplainabilityResult.class));
    }

    @Test
    public void testExplainabilityResultDto() {
        assertTrue(handler.supportsDto(getResultDto()));
        assertFalse(handler.supportsDto(BaseExplainabilityResultDto.class));
    }

    @Test
    public void testGetExplainabilityResultById_WhenStored() {
        when(storage.containsKey(anyString())).thenReturn(true);
        when(storage.get(eq(EXECUTION_ID))).thenReturn(result);

        assertEquals(result, handler.getExplainabilityResultById(EXECUTION_ID));
    }

    @Test
    public void testGetExplainabilityResultById_WhenNotStored() {
        when(storage.containsKey(anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> handler.getExplainabilityResultById(EXECUTION_ID));
    }

    @Test
    public void testStoreExplainabilityResult_WhenAlreadyStored() {
        when(storage.containsKey(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> handler.storeExplainabilityResult(EXECUTION_ID, result));
    }

    @Test
    public void testStoreExplainabilityResultById_WhenNotAlreadyStored() {
        when(storage.containsKey(anyString())).thenReturn(false);

        handler.storeExplainabilityResult(EXECUTION_ID, result);

        verify(storage).put(eq(EXECUTION_ID), eq(result));
    }

    @Test
    public abstract void testExplainabilityResultFrom_Success();

    @Test
    public abstract void testExplainabilityResultFrom_Failure();
}
