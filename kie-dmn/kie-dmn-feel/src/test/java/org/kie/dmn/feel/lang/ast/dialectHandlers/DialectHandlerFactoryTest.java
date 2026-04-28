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
package org.kie.dmn.feel.lang.ast.dialectHandlers;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELDialect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DialectHandlerFactory.getHandler() method
 */
class DialectHandlerFactoryTest {

    @Test
    void testGetHandler_NullContextReturnsFEELHandler() {
        DialectHandler handler = DialectHandlerFactory.getHandler(null);
        
        assertThat(handler).isNotNull();
        assertThat(handler).isInstanceOf(FEELDialectHandler.class);
    }

    @Test
    void testGetHandler_NullDialectReturnsFEELHandler() {
        EvaluationContext ctx = mock(EvaluationContext.class);
        when(ctx.getFEELDialect()).thenReturn(null);
        
        DialectHandler handler = DialectHandlerFactory.getHandler(ctx);
        
        assertThat(handler).isNotNull();
        assertThat(handler).isInstanceOf(FEELDialectHandler.class);
    }

    @Test
    void testGetHandler_FEELDialectReturnsFEELHandler() {
        EvaluationContext ctx = mock(EvaluationContext.class);
        when(ctx.getFEELDialect()).thenReturn(FEELDialect.FEEL);
        
        DialectHandler handler = DialectHandlerFactory.getHandler(ctx);
        
        assertThat(handler).isNotNull();
        assertThat(handler).isInstanceOf(FEELDialectHandler.class);
    }

    @Test
    void testGetHandler_BFEELDialectReturnsBFEELHandler() {
        EvaluationContext ctx = mock(EvaluationContext.class);
        when(ctx.getFEELDialect()).thenReturn(FEELDialect.BFEEL);
        
        DialectHandler handler = DialectHandlerFactory.getHandler(ctx);
        
        assertThat(handler).isNotNull();
        assertThat(handler).isInstanceOf(BFEELDialectHandler.class);
    }

    @Test
    void testGetHandler_DifferentDialectsReturnDifferentHandlers() {
        EvaluationContext ctxFEEL = mock(EvaluationContext.class);
        when(ctxFEEL.getFEELDialect()).thenReturn(FEELDialect.FEEL);
        
        EvaluationContext ctxBFEEL = mock(EvaluationContext.class);
        when(ctxBFEEL.getFEELDialect()).thenReturn(FEELDialect.BFEEL);
        
        DialectHandler handlerFEEL = DialectHandlerFactory.getHandler(ctxFEEL);
        DialectHandler handlerBFEEL = DialectHandlerFactory.getHandler(ctxBFEEL);
        
        assertThat(handlerFEEL).isInstanceOf(FEELDialectHandler.class);
        assertThat(handlerBFEEL).isInstanceOf(BFEELDialectHandler.class);
        assertThat(handlerFEEL).isNotSameAs(handlerBFEEL);
    }

    @Test
    void testGetHandler_FEELCachesSingletonInstances() {
        EvaluationContext ctx1 = mock(EvaluationContext.class);
        when(ctx1.getFEELDialect()).thenReturn(FEELDialect.FEEL);
        
        EvaluationContext ctx2 = mock(EvaluationContext.class);
        when(ctx2.getFEELDialect()).thenReturn(FEELDialect.FEEL);
        
        DialectHandler handler1 = DialectHandlerFactory.getHandler(ctx1);
        DialectHandler handler2 = DialectHandlerFactory.getHandler(ctx2);
        
        // Should return the same cached instance
        assertThat(handler1).isSameAs(handler2);
    }

    @Test
    void testGetHandler_BFEELCachesSingletonInstances() {
        EvaluationContext ctx1 = mock(EvaluationContext.class);
        when(ctx1.getFEELDialect()).thenReturn(FEELDialect.BFEEL);
        
        EvaluationContext ctx2 = mock(EvaluationContext.class);
        when(ctx2.getFEELDialect()).thenReturn(FEELDialect.BFEEL);
        
        DialectHandler handler1 = DialectHandlerFactory.getHandler(ctx1);
        DialectHandler handler2 = DialectHandlerFactory.getHandler(ctx2);
        
        // Should return the same cached instance
        assertThat(handler1).isSameAs(handler2);
    }
}
