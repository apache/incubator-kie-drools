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
package org.kie.dmn.feel.runtime;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.impl.FEELBuilder;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;
import org.kie.dmn.feel.runtime.events.UnknownVariableErrorEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


class FEELErrorMessagesTest {

    @Test
    void unknownVariable() {
        final FEEL feel = FEELBuilder.builder().build();
        final FEELEventListener fel = Mockito.mock(FEELEventListener.class );
        feel.addListener( fel );

        final CompilerContext ctx = feel.newCompilerContext();
        feel.compile( "a variable name", ctx );

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class );
        verify( fel, times(2) ).onEvent( captor.capture() );

        assertThat(captor.getAllValues()).hasSize(2);
        assertThat(captor.getAllValues().get(1)).isInstanceOf( UnknownVariableErrorEvent.class);
        assertThat(captor.getAllValues().get(1).getOffendingSymbol()).isEqualTo("a variable name");
    }

    @Test
    void ifWithoutElse() {
        final FEEL feel = FEELBuilder.builder().build();
        final FEELEventListener fel = Mockito.mock(FEELEventListener.class);
        feel.addListener(fel);

        final CompilerContext ctx = feel.newCompilerContext();
        feel.compile("if true then 123", ctx);

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
        verify(fel, times(1)).onEvent(captor.capture());

        assertThat(captor.getAllValues()).hasSize(1);
        assertThat(captor.getAllValues().get(0)).isInstanceOfAny(SyntaxErrorEvent.class);
        assertThat(captor.getAllValues().get(0).getMessage()).startsWith("Detected 'if' expression without 'else' part");
    }

    @Test
    void ifWithoutElse2() {
        final FEEL feel = FEELBuilder.builder().build();
        final FEELEventListener fel = Mockito.mock(FEELEventListener.class);
        feel.addListener(fel);

        final CompilerContext ctx = feel.newCompilerContext();
        feel.compile("if true then 123 456", ctx);

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
        verify(fel, times(1)).onEvent(captor.capture());

        assertThat(captor.getAllValues()).hasSize(1);
        assertThat(captor.getAllValues().get(0)).isInstanceOf(SyntaxErrorEvent.class);
        assertThat(captor.getAllValues().get(0).getMessage()).isEqualTo("missing 'else' at '456'");
    }

    @Test
    void ifWithoutThen() {
        final FEEL feel = FEELBuilder.builder().build();
        final FEELEventListener fel = Mockito.mock(FEELEventListener.class);
        feel.addListener(fel);

        final CompilerContext ctx = feel.newCompilerContext();
        feel.compile("if true", ctx);

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
        verify(fel, times(1)).onEvent(captor.capture());

        assertThat(captor.getAllValues()).hasSize(1);
        assertThat(captor.getAllValues().get(0)).isInstanceOf(SyntaxErrorEvent.class);
        assertThat(captor.getAllValues().get(0).getMessage()).startsWith("Detected 'if' expression without 'then' part");
    }

    @Test
    void ifWithoutThen2() {
        final FEEL feel = FEELBuilder.builder().build();
        final FEELEventListener fel = Mockito.mock(FEELEventListener.class);
        feel.addListener(fel);

        final CompilerContext ctx = feel.newCompilerContext();
        feel.compile("if true 123", ctx);

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
        verify(fel, times(2)).onEvent(captor.capture());

        assertThat(captor.getAllValues()).hasSize(2);
        assertThat(captor.getAllValues().get(0)).isInstanceOf(SyntaxErrorEvent.class);
        assertThat(captor.getAllValues().get(0).getMessage()).startsWith("missing 'then' at '123'");
        assertThat(captor.getAllValues().get(1)).isInstanceOf(SyntaxErrorEvent.class);
        assertThat(captor.getAllValues().get(1).getMessage()).startsWith("Detected 'if' expression without 'then' part");
    }

}
