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

package org.kie.dmn.feel.gwt.functions.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FunctionProviderGeneratorTest {

    @Mock
    private TreeLogger logger;

    @Mock
    private GeneratorContext context;

    @Mock
    private TypeOracle typeOracle;

    @Mock
    private FileCreator fileCreator;

    private FunctionProviderGenerator generator;

    @Before
    public void setup() {
        generator = spy(new FunctionProviderGenerator());

        when(context.getTypeOracle()).thenReturn(typeOracle);
    }

    @Test
    public void testGenerate() {

        final String requestedClass = "requestedClass";
        final String expectedGenerate = "org.kie.dmn.feel.gwt.functions.client.FEELFunctionProviderImpl";

        doReturn(fileCreator).when(generator).getFileCreator(logger, context);
        doNothing().when(generator).assertFEELFunctionProviderClass(any());

        final String actualGenerate = generator.generate(logger, context, requestedClass);

        verify(fileCreator).write();
        assertEquals(expectedGenerate, actualGenerate);
    }

    @Test
    public void testGenerateWhenFunctionProviderClassAssertFails() {

        final String requestedClass = "requestedClass";

        doThrow(new RuntimeException()).when(generator).assertFEELFunctionProviderClass(any());

        final String actualGenerate = generator.generate(logger, context, requestedClass);

        verify(fileCreator, never()).write();
        assertNull(actualGenerate);
    }
}
