/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.common.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.common.persistence.generator.StringDataGenerator;

public class StringDataGeneratorTest {

    @Test
    public void with2Parts() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        assertThat(generator.generateNextValue()).isEqualTo("a h");
        assertThat(generator.generateNextValue()).isEqualTo("b i");
        assertThat(generator.generateNextValue()).isEqualTo("c j");
        assertThat(generator.generateNextValue()).isEqualTo("d k");
        assertThat(generator.generateNextValue()).isEqualTo("a i");
        assertThat(generator.generateNextValue()).isEqualTo("b j");
        assertThat(generator.generateNextValue()).isEqualTo("c k");
        assertThat(generator.generateNextValue()).isEqualTo("d h");
        assertThat(generator.generateNextValue()).isEqualTo("a j");
        assertThat(generator.generateNextValue()).isEqualTo("b k");
        assertThat(generator.generateNextValue()).isEqualTo("c h");
        assertThat(generator.generateNextValue()).isEqualTo("d i");
    }

    @Test
    public void with3Parts() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        assertThat(generator.generateNextValue()).isEqualTo("a h o");
        assertThat(generator.generateNextValue()).isEqualTo("b i p");
        assertThat(generator.generateNextValue()).isEqualTo("c j q");
        assertThat(generator.generateNextValue()).isEqualTo("d k r");
        assertThat(generator.generateNextValue()).isEqualTo("a h p");
        assertThat(generator.generateNextValue()).isEqualTo("b i q");
        assertThat(generator.generateNextValue()).isEqualTo("c j r");
        assertThat(generator.generateNextValue()).isEqualTo("d k o");
    }

    @Test
    public void with4Parts() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        generator.addPart("v", "w", "x", "y");
        assertThat(generator.generateNextValue()).isEqualTo("a h o v");
        assertThat(generator.generateNextValue()).isEqualTo("b i p w");
        assertThat(generator.generateNextValue()).isEqualTo("c j q x");
        assertThat(generator.generateNextValue()).isEqualTo("d k r y");
        assertThat(generator.generateNextValue()).isEqualTo("a h p w");
        assertThat(generator.generateNextValue()).isEqualTo("b i q x");
        assertThat(generator.generateNextValue()).isEqualTo("c j r y");
        assertThat(generator.generateNextValue()).isEqualTo("d k o v");
        assertThat(generator.generateNextValue()).isEqualTo("a h q x");
        assertThat(generator.generateNextValue()).isEqualTo("b i r y");
        assertThat(generator.generateNextValue()).isEqualTo("c j o v");
        assertThat(generator.generateNextValue()).isEqualTo("d k p w");
    }

    @Test
    public void with4PartsMaximumSizeFor2() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        generator.addPart("v", "w", "x", "y");
        generator.predictMaximumSizeAndReset(9);
        assertThat(generator.generateNextValue()).isEqualTo("a v");
        assertThat(generator.generateNextValue()).isEqualTo("b w");
        assertThat(generator.generateNextValue()).isEqualTo("c x");
        assertThat(generator.generateNextValue()).isEqualTo("d y");
        assertThat(generator.generateNextValue()).isEqualTo("a w");
        assertThat(generator.generateNextValue()).isEqualTo("b x");
        assertThat(generator.generateNextValue()).isEqualTo("c y");
        assertThat(generator.generateNextValue()).isEqualTo("d v");
    }

    @Test
    public void with4PartsMaximumSizeFor3() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        generator.addPart("v", "w", "x", "y");
        generator.predictMaximumSizeAndReset((4 * 4) + 3);
        assertThat(generator.generateNextValue()).isEqualTo("a o v");
        assertThat(generator.generateNextValue()).isEqualTo("b p w");
        assertThat(generator.generateNextValue()).isEqualTo("c q x");
        assertThat(generator.generateNextValue()).isEqualTo("d r y");
        assertThat(generator.generateNextValue()).isEqualTo("a o w");
        assertThat(generator.generateNextValue()).isEqualTo("b p x");
        assertThat(generator.generateNextValue()).isEqualTo("c q y");
        assertThat(generator.generateNextValue()).isEqualTo("d r v");
    }

}
