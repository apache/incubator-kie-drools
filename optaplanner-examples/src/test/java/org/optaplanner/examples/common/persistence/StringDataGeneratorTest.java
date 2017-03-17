/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;

import static org.junit.Assert.*;

public class StringDataGeneratorTest {

    @Test
    public void with2Parts() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        assertEquals("a h", generator.generateNextValue());
        assertEquals("b i", generator.generateNextValue());
        assertEquals("c j", generator.generateNextValue());
        assertEquals("d k", generator.generateNextValue());
        assertEquals("a i", generator.generateNextValue());
        assertEquals("b j", generator.generateNextValue());
        assertEquals("c k", generator.generateNextValue());
        assertEquals("d h", generator.generateNextValue());
        assertEquals("a j", generator.generateNextValue());
        assertEquals("b k", generator.generateNextValue());
        assertEquals("c h", generator.generateNextValue());
        assertEquals("d i", generator.generateNextValue());
    }

    @Test
    public void with3Parts() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        assertEquals("a h o", generator.generateNextValue());
        assertEquals("b i p", generator.generateNextValue());
        assertEquals("c j q", generator.generateNextValue());
        assertEquals("d k r", generator.generateNextValue());
        assertEquals("a h p", generator.generateNextValue());
        assertEquals("b i q", generator.generateNextValue());
        assertEquals("c j r", generator.generateNextValue());
        assertEquals("d k o", generator.generateNextValue());
    }

    @Test
    public void with4Parts() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        generator.addPart("v", "w", "x", "y");
        assertEquals("a h o v", generator.generateNextValue());
        assertEquals("b i p w", generator.generateNextValue());
        assertEquals("c j q x", generator.generateNextValue());
        assertEquals("d k r y", generator.generateNextValue());
        assertEquals("a h p w", generator.generateNextValue());
        assertEquals("b i q x", generator.generateNextValue());
        assertEquals("c j r y", generator.generateNextValue());
        assertEquals("d k o v", generator.generateNextValue());
        assertEquals("a h q x", generator.generateNextValue());
        assertEquals("b i r y", generator.generateNextValue());
        assertEquals("c j o v", generator.generateNextValue());
        assertEquals("d k p w", generator.generateNextValue());
    }

    @Test
    public void with4PartsMaximumSizeFor2() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        generator.addPart("v", "w", "x", "y");
        generator.predictMaximumSizeAndReset(9);
        assertEquals("a v", generator.generateNextValue());
        assertEquals("b w", generator.generateNextValue());
        assertEquals("c x", generator.generateNextValue());
        assertEquals("d y", generator.generateNextValue());
        assertEquals("a w", generator.generateNextValue());
        assertEquals("b x", generator.generateNextValue());
        assertEquals("c y", generator.generateNextValue());
        assertEquals("d v", generator.generateNextValue());
    }

    @Test
    public void with4PartsMaximumSizeFor3() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        generator.addPart("v", "w", "x", "y");
        generator.predictMaximumSizeAndReset((4 * 4) + 3);
        assertEquals("a o v", generator.generateNextValue());
        assertEquals("b p w", generator.generateNextValue());
        assertEquals("c q x", generator.generateNextValue());
        assertEquals("d r y", generator.generateNextValue());
        assertEquals("a o w", generator.generateNextValue());
        assertEquals("b p x", generator.generateNextValue());
        assertEquals("c q y", generator.generateNextValue());
        assertEquals("d r v", generator.generateNextValue());
    }

}
