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

import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.NamedElement;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;


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
        assertThat(result).isNull();

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
        when(model.getImportAliasFor(any(), any())).thenReturn(Optional.of("inputName"));

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
        NamedElement source = mock(NamedElement.class);
        when(input.getSource()).thenReturn(source);

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn("modelNamespace");
        when(model.getImportAliasFor(any(), any())).thenReturn(Optional.empty());

        try (MockedStatic<MsgUtil> msgUtilMock = mockStatic(MsgUtil.class)) {
            try (MockedStatic<UnnamedImportUtils> unnamedImportMock = mockStatic(UnnamedImportUtils.class)) {
                unnamedImportMock.when(() -> UnnamedImportUtils.isInUnnamedImport(input, model)).thenReturn(false);
                String result = DecisionServiceCompiler.inputQualifiedNamePrefix(input, model);
                assertThat(result).isNull();
                // Verify error was reported
                msgUtilMock.verify(() -> MsgUtil.reportMessage(
                        any(),
                        eq(DMNMessage.Severity.ERROR),
                        eq(source),
                        eq(model),
                        isNull(),
                        isNull(),
                        eq(Msg.IMPORT_NOT_FOUND_FOR_NODE_MISSING_ALIAS),
                        eq(new QName("nodeNamespace", "modelname")),
                        eq(source)
                ));
            }
        }

    }

    @Test
    void resolveBuiltInType_nullReturnsNull() {
        assertThat(DecisionServiceCompiler.resolveBuiltInType(null)).isNull();
    }

    @Test
    void resolveBuiltInType_stringCaseInsensitive() {
        assertThat(DecisionServiceCompiler.resolveBuiltInType("STRING")).isEqualTo(BuiltInType.STRING);
    }

    @Test
    void resolveBuiltInType_date() {
        assertThat(DecisionServiceCompiler.resolveBuiltInType("date")).isEqualTo(BuiltInType.DATE);
    }

    @Test
    void resolveBuiltInType_dateAndTime() {
        assertThat(DecisionServiceCompiler.resolveBuiltInType("date and time")).isEqualTo(BuiltInType.DATE_TIME);
    }

    @Test
    void resolveBuiltInType_number() {
        assertThat(DecisionServiceCompiler.resolveBuiltInType("number")).isEqualTo(BuiltInType.NUMBER);
    }

    @Test
    void resolveBuiltInType_boolean() {
        assertThat(DecisionServiceCompiler.resolveBuiltInType("boolean")).isEqualTo(BuiltInType.BOOLEAN);
    }


    @Test
    void resolveDMNTypeString() {

        DMNModelImpl model = mock(DMNModelImpl.class);
        DMNTypeRegistry registry = mock(DMNTypeRegistry.class);

        when(model.getNamespace()).thenReturn("ns");
        when(model.getTypeRegistry()).thenReturn(registry);
        when(registry.resolveType("ns", "string"))
                .thenReturn(null);

        QName qName = new QName("", "string");

        DMNType result = DecisionServiceCompiler.resolveDMNType(qName, model);
        assertThat(result).isInstanceOf(SimpleTypeImpl.class);
        assertThat(((SimpleTypeImpl) result).getFeelType()).isEqualTo(BuiltInType.STRING);
    }

    @Test
    void resolveDMNTypeDateAndTime() {
        DMNTypeRegistry registry = mock(DMNTypeRegistry.class);
        when(registry.resolveType(any(), any())).thenReturn(null);

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn("ns");
        when(model.getTypeRegistry()).thenReturn(registry);

        DMNType result = DecisionServiceCompiler.resolveDMNType(new QName("", "date and time"), model);
        assertThat(result).isInstanceOf(SimpleTypeImpl.class);
        assertThat(((SimpleTypeImpl) result).getFeelType()).isEqualTo(BuiltInType.DATE_TIME);
        assertThat(result.isCollection()).isFalse();
    }

    @Test
    void resolveDMNType_listBuiltInIsMarkedAsCollection() {
        DMNTypeRegistry registry = mock(DMNTypeRegistry.class);
        when(registry.resolveType(any(), any())).thenReturn(null);

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn("ns");
        when(model.getTypeRegistry()).thenReturn(registry);

        DMNType result = DecisionServiceCompiler.resolveDMNType(new QName("", "list"), model);
        assertThat(result).isInstanceOf(SimpleTypeImpl.class);
        assertThat(((SimpleTypeImpl) result).getFeelType()).isEqualTo(BuiltInType.LIST);
        assertThat(result.isCollection()).isTrue();
    }

    @Test
    void resolveDMNType_unknownNameReturnsNull() {
        DMNTypeRegistry registry = mock(DMNTypeRegistry.class);
        when(registry.resolveType(any(), any())).thenReturn(null);

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn("ns");
        when(model.getTypeRegistry()).thenReturn(registry);

        assertThat(DecisionServiceCompiler.resolveDMNType(new QName("", "not-a-type"), model)).isNull();
    }

    // fi=scalar "string", fd=collection "tDateList" (base=date) — bases differ → false
    @Test
    void isReturnTypeCollectionCompatible_incompatibleType() {
        final String ns = "ns";
        SimpleTypeImpl dateType = new SimpleTypeImpl(ns, "date", null, false, null, null, null, BuiltInType.DATE);
        SimpleTypeImpl tDateList = new SimpleTypeImpl(ns, "tDateList", null, true, null, null, dateType, BuiltInType.DATE);

        DMNTypeRegistry registry = mock(DMNTypeRegistry.class);
        when(registry.resolveType(any(), any())).thenAnswer(inv -> "tDateList".equals(inv.getArgument(1)) ? tDateList : null);

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn(ns);
        when(model.getTypeRegistry()).thenReturn(registry);

        assertThat(DecisionServiceCompiler.isReturnTypeCollectionCompatible(
                new QName(ns, "string"), new QName(ns, "tDateList"), model)).isFalse();
    }

    // fi=scalar "date", fd=collection "tDateList" (base=date) — same base → true
    @Test
    void isReturnTypeCollectionCompatible_compatibleType() {
        final String ns = "ns";
        SimpleTypeImpl dateType = new SimpleTypeImpl(ns, "date", null, false, null, null, null, BuiltInType.DATE);
        SimpleTypeImpl tDateList = new SimpleTypeImpl(ns, "tDateList", null, true, null, null, dateType, BuiltInType.DATE);

        DMNTypeRegistry registry = mock(DMNTypeRegistry.class);
        when(registry.resolveType(any(), any())).thenAnswer(inv -> "tDateList".equals(inv.getArgument(1)) ? tDateList : null);

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn(ns);
        when(model.getTypeRegistry()).thenReturn(registry);

        assertThat(DecisionServiceCompiler.isReturnTypeCollectionCompatible(
                new QName(ns, "date"), new QName(ns, "tDateList"), model)).isTrue();
    }


    // fi=collection "tDateList" (base=date), fd=scalar "date" — compatible → true
    @Test
    void isReturnTypeCollectionCompatible_collectionFi_scalarFd_compatibleBase() {
        final String ns = "ns";
        SimpleTypeImpl dateType = new SimpleTypeImpl(ns, "date", null, false, null, null, null, BuiltInType.DATE);
        SimpleTypeImpl tDateList = new SimpleTypeImpl(ns, "tDateList", null, true, null, null, dateType, BuiltInType.DATE);

        DMNTypeRegistry registry = mock(DMNTypeRegistry.class);
        when(registry.resolveType(any(), any())).thenAnswer(inv -> "tDateList".equals(inv.getArgument(1)) ? tDateList : null);

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn(ns);
        when(model.getTypeRegistry()).thenReturn(registry);

        assertThat(DecisionServiceCompiler.isReturnTypeCollectionCompatible(
                new QName(ns, "tDateList"), new QName(ns, "date"), model)).isTrue();
    }


    // fi="date and time", fd="date" — date-to-dateTime coercion pattern → true
    @Test
    void isReturnTypeCollectionCompatible_dateTimeFi_dateFd_compatible() {
        DMNTypeRegistry registry = mock(DMNTypeRegistry.class);
        when(registry.resolveType(any(), any())).thenReturn(null);

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn("ns");
        when(model.getTypeRegistry()).thenReturn(registry);

        assertThat(DecisionServiceCompiler.isReturnTypeCollectionCompatible(
                new QName("", "date and time"), new QName("", "date"), model)).isTrue();
    }

    // fi="date", fd="date and time" — order matters → false
    @Test
    void isReturnTypeCollectionCompatible_dateFi_dateTimeFd_incompatible() {
        DMNTypeRegistry registry = mock(DMNTypeRegistry.class);
        when(registry.resolveType(any(), any())).thenReturn(null);

        DMNModelImpl model = mock(DMNModelImpl.class);
        when(model.getNamespace()).thenReturn("ns");
        when(model.getTypeRegistry()).thenReturn(registry);

        assertThat(DecisionServiceCompiler.isReturnTypeCollectionCompatible(
                new QName("", "date"), new QName("", "date and time"), model)).isFalse();
    }

}
