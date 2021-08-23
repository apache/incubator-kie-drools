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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.feel.gwt.functions.api.FunctionDefinition;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.api.Parameter;
import org.kie.dmn.feel.gwt.functions.api.Type;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.kie.dmn.feel.gwt.functions.rebind.FileCreator.GENERATED_CLASS_FQCN;
import static org.kie.dmn.feel.gwt.functions.rebind.FileCreator.PACKAGE_NAME;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileCreatorTest {

    @Mock
    private GeneratorContext context;

    @Mock
    private TreeLogger logger;

    @Mock
    private ClassSourceFileComposerFactory composerFactory;

    @Mock
    private PrintWriter printWriter;

    @Mock
    private SourceWriter sourceWriter;

    private FileCreator fileCreator;

    @Before
    public void setup() {
        fileCreator = spy(new FileCreator(context, logger));
    }

    @Test
    public void testWrite() {

        doReturn(composerFactory).when(fileCreator).getClassSourceFileComposerFactory();
        when(context.tryCreate(logger, PACKAGE_NAME, GENERATED_CLASS_FQCN)).thenReturn(printWriter);
        when(composerFactory.createSourceWriter(context, printWriter)).thenReturn(sourceWriter);

        fileCreator.write();

        verify(sourceWriter).print(MethodTemplates.getTemplate());
        verify(sourceWriter).commit(logger);
    }

    @Test
    public void testGetClassSourceFileComposerFactory() {

        doReturn(composerFactory).when(fileCreator).makeComposerFactory();

        final ClassSourceFileComposerFactory actualFactory = fileCreator.getClassSourceFileComposerFactory();

        verify(composerFactory).addImport(FEELFunctionProvider.class.getCanonicalName());
        verify(composerFactory).addImport(FunctionDefinition.class.getCanonicalName());
        verify(composerFactory).addImport(FunctionOverrideVariation.class.getCanonicalName());
        verify(composerFactory).addImport(BuiltInType.class.getCanonicalName());
        verify(composerFactory).addImport(Parameter.class.getCanonicalName());
        verify(composerFactory).addImport(List.class.getCanonicalName());
        verify(composerFactory).addImport(ArrayList.class.getCanonicalName());
        verify(composerFactory).addImport(Type.class.getCanonicalName());
        verify(composerFactory).addImplementedInterface(FEELFunctionProvider.class.getName());

        assertSame(composerFactory, actualFactory);
    }

    @Test
    public void testMakeComposerFactory() {
        final ClassSourceFileComposerFactory factory = fileCreator.makeComposerFactory();
        assertEquals(PACKAGE_NAME, factory.getCreatedPackage());
        assertEquals(GENERATED_CLASS_FQCN, factory.getCreatedClassShortName());
    }
}
