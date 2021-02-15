/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.core.context.variable;

import java.util.stream.Stream;

import javax.lang.model.SourceVersion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VariableTest {

    private Variable tested;

    @BeforeEach
    public void setUp() {
        tested = new Variable();
    }

    @Test
    void testValidIdentifierName() {
        final String name = "valid";
        tested.setName(name);
        assertValidSanitizedName(name);
        assertThat(tested.getSanitizedName()).isEqualTo(name);
    }

    private void assertValidSanitizedName(String name) {
        assertThat(tested.getName()).isEqualTo(name);
        assertThat(SourceVersion.isName(tested.getSanitizedName())).isTrue();
    }

    @Test
    void testInvalidIdentifierName() {
        final String name = "123valid%^á+-)([]?!@";
        tested.setName(name);
        assertValidSanitizedName(name);
        assertThat(tested.getSanitizedName()).isNotEqualTo(name).isEqualTo("valid");
    }

    @Test
    void testInvalidIdentifierWithReservedWordName() {
        final String name = "123class%^á+-)([]?!@";
        tested.setName(name);
        assertValidSanitizedName(name);
        assertThat(tested.getSanitizedName()).isNotEqualTo(name).isEqualTo("v$class");
    }

    @Test
    void testReservedWordsName() {
        Stream.of("abstract", "continue", "for", "new", "switch", "assert", "default",
                  "goto", "package", "synchronized", "boolean", "do", "if", "private",
                  "this", "break", "double", "implements", "protected", "throw", "byte",
                  "else", "import", "public", "throws", "case", "enum", "instanceof",
                  "return", "transient", "catch", "extends", "int", "short", "try", "char",
                  "final", "interface", "static", "void", "class", "finally", "long",
                  "strictfp", "volatile", "const", "float", "native", "super", "while")
                .forEach(name -> {
                    tested.setName(name);
                    assertValidSanitizedName(name);
                    assertThat(tested.getSanitizedName()).isNotEqualTo(name).isEqualTo("v$" + tested.getName());
                });
    }
}