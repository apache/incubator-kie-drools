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
package org.kie.dmn.core.compiler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.NamedElement;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DecisionServiceCompilerTest extends BaseInterpretedVsCompiledTest {

    @ParameterizedTest
    @MethodSource("params")
    void inputQualifiedNamePrefixWithSameNameSpace() {
        DMNNode input = mock(DMNNode.class);
        DMNModelImpl model = mock(DMNModelImpl.class);

        when(input.getModelNamespace()).thenReturn("modelNamespace");
        when(model.getNamespace()).thenReturn("modelNamespace");

        String result = DecisionServiceCompiler.inputQualifiedNamePrefix(input, model);
        assertThat(result).isNull();

    }

    @ParameterizedTest
    @MethodSource("params")
    void inputQualifiedNamePrefixWithUnnamedImportTrue() {

        DMNNode input = mock(DMNNode.class);
        when(input.getModelNamespace()).thenReturn("nodeNamespace");
        when(input.getName()).thenReturn("inputName");

        Import imported = mock(Import.class);
        when(imported.getName()).thenReturn("");
        when(imported.getNamespace()).thenReturn("nodeNamespace");
        Definitions definitions = mock(Definitions.class);
        when(definitions.getImport()).thenReturn(List.of(imported));

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn("modelNamespace");
        when(model.getDefinitions()).thenReturn(definitions);

        String result = DecisionServiceCompiler.inputQualifiedNamePrefix(input, model);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("inputName");

    }

    @ParameterizedTest
    @MethodSource("params")
    void inputQualifiedNamePrefixWithImportAlias() {
        DMNNode input = mock(DMNNode.class);
        when(input.getModelNamespace()).thenReturn("nodeNamespace");
        when(input.getName()).thenReturn("inputName");

        Import imported = mock(Import.class);
        when(imported.getNamespace()).thenReturn("importedNamespace");
        Definitions definitions = mock(Definitions.class);
        when(definitions.getImport()).thenReturn(List.of(imported));

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn("modelNamespace");
        when(model.getDefinitions()).thenReturn(definitions);
        when(model.getImportAliasFor(Mockito.any(), Mockito.any())).thenReturn(Optional.of("inputName"));

        String result = DecisionServiceCompiler.inputQualifiedNamePrefix(input, model);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("inputName");

    }

    @ParameterizedTest
    @MethodSource("params")
    void inputQualifiedNamePrefixWithEmptyImportAlias() {
        DMNBaseNode input = mock(DMNBaseNode.class);
        when(input.getModelNamespace()).thenReturn("nodeNamespace");
        when(input.getName()).thenReturn("inputName");
        when(input.getModelName()).thenReturn("modelname");
        when(input.getSource()).thenReturn(mock(NamedElement.class));

        Import imported = mock(Import.class);
        when(imported.getNamespace()).thenReturn("importedNamespace");
        Definitions definitions = mock(Definitions.class);
        when(definitions.getImport()).thenReturn(List.of(imported));

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn("modelNamespace");
        when(model.getDefinitions()).thenReturn(definitions);
        when(model.getImportAliasFor(Mockito.any(), Mockito.any())).thenReturn(Optional.empty());

        String result = DecisionServiceCompiler.inputQualifiedNamePrefix(input, model);
        assertThat(result).isNull();
        verify(model, times(1)).getImportAliasFor(Mockito.any(), Mockito.any());

    }
}
