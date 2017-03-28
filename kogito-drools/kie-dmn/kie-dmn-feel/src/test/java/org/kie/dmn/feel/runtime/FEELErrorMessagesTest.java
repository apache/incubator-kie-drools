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
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.runtime.events.UnknownVariableErrorEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

public class FEELErrorMessagesTest {

    @Test
    public void unknownVariable() {
        FEEL feel = FEEL.newInstance();
        FEELEventListener fel = Mockito.mock( FEELEventListener.class );
        feel.addListener( fel );

        CompilerContext ctx = feel.newCompilerContext();
//        ctx.addInputVariableType( "a variable name", BuiltInType.STRING );
        CompiledExpression ce = feel.compile( "a variable name", ctx );

        ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass( FEELEvent.class );
        verify( fel, times(2) ).onEvent( captor.capture() );

        Assert.assertThat( captor.getAllValues().size(), is( 2 ) );
        Assert.assertThat( captor.getAllValues().get(1), is( instanceOf( UnknownVariableErrorEvent.class ) ) );
        Assert.assertThat( ((UnknownVariableErrorEvent)captor.getAllValues().get(1)).getOffendingSymbol(), is( "a variable name" ) );
    }


}
