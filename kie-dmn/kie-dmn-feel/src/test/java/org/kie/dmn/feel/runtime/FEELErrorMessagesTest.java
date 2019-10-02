/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime;

import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;
import org.kie.dmn.feel.runtime.events.UnknownVariableErrorEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FEELErrorMessagesTest {

    @Test
    public void unknownVariable() {
        final FEEL feel = FEEL.newInstance();
        final FEELEventListener fel = Mockito.mock(FEELEventListener.class );
        feel.addListener( fel );

        final CompilerContext ctx = feel.newCompilerContext();
        feel.compile( "a variable name", ctx );

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class );
        verify( fel, times(2) ).onEvent( captor.capture() );

        Assert.assertThat( captor.getAllValues().size(), is( 2 ) );
        Assert.assertThat( captor.getAllValues().get(1), is( instanceOf( UnknownVariableErrorEvent.class ) ) );
        Assert.assertThat( ((UnknownVariableErrorEvent)captor.getAllValues().get(1)).getOffendingSymbol(), is( "a variable name" ) );
    }

    @Test
    public void ifWithoutElse() {
        final FEEL feel = FEEL.newInstance();
        final FEELEventListener fel = Mockito.mock(FEELEventListener.class);
        feel.addListener(fel);

        final CompilerContext ctx = feel.newCompilerContext();
        feel.compile("if true then 123", ctx);

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
        verify(fel, times(1)).onEvent(captor.capture());

        Assert.assertThat(captor.getAllValues().size(), is(1));
        Assert.assertThat(captor.getAllValues().get(0), is(instanceOf(SyntaxErrorEvent.class)));
        Assert.assertThat(((SyntaxErrorEvent) captor.getAllValues().get(0)).getMessage(), startsWith("Detected 'if' expression without 'else' part"));
    }

    @Test
    public void ifWithoutElse2() {
        final FEEL feel = FEEL.newInstance();
        final FEELEventListener fel = Mockito.mock(FEELEventListener.class);
        feel.addListener(fel);

        final CompilerContext ctx = feel.newCompilerContext();
        feel.compile("if true then 123 456", ctx);

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
        verify(fel, times(1)).onEvent(captor.capture());

        Assert.assertThat(captor.getAllValues().size(), is(1));
        Assert.assertThat(captor.getAllValues().get(0), is(instanceOf(SyntaxErrorEvent.class)));
        Assert.assertThat(((SyntaxErrorEvent) captor.getAllValues().get(0)).getMessage(), is("missing 'else' at '456'"));
    }

    @Test
    public void ifWithoutThen() {
        final FEEL feel = FEEL.newInstance();
        final FEELEventListener fel = Mockito.mock(FEELEventListener.class);
        feel.addListener(fel);

        final CompilerContext ctx = feel.newCompilerContext();
        feel.compile("if true", ctx);

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
        verify(fel, times(1)).onEvent(captor.capture());

        Assert.assertThat(captor.getAllValues().size(), is(1));
        Assert.assertThat(captor.getAllValues().get(0), is(instanceOf(SyntaxErrorEvent.class)));
        Assert.assertThat(((SyntaxErrorEvent) captor.getAllValues().get(0)).getMessage(), startsWith("Detected 'if' expression without 'then' part"));
    }

    @Test
    public void ifWithoutThen2() {
        final FEEL feel = FEEL.newInstance();
        final FEELEventListener fel = Mockito.mock(FEELEventListener.class);
        feel.addListener(fel);

        final CompilerContext ctx = feel.newCompilerContext();
        feel.compile("if true 123", ctx);

        final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
        verify(fel, times(2)).onEvent(captor.capture());

        Assert.assertThat(captor.getAllValues().size(), is(2));
        Assert.assertThat(captor.getAllValues().get(0), is(instanceOf(SyntaxErrorEvent.class)));
        Assert.assertThat(((SyntaxErrorEvent) captor.getAllValues().get(0)).getMessage(), startsWith("missing 'then' at '123'"));
        Assert.assertThat(captor.getAllValues().get(1), is(instanceOf(SyntaxErrorEvent.class)));
        Assert.assertThat(((SyntaxErrorEvent) captor.getAllValues().get(1)).getMessage(), startsWith("Detected 'if' expression without 'then' part"));
    }

}
