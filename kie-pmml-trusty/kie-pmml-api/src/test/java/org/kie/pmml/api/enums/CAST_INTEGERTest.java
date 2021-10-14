/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.api.enums;

import org.junit.Test;

import static org.junit.Assert.*;

public class CAST_INTEGERTest {

    @Test
    public void getRound() {
        int retrieved = CAST_INTEGER.getRound(2.718);
        assertEquals(3, retrieved);
        retrieved = CAST_INTEGER.getRound(-2.718);
        assertEquals(-3, retrieved);
        retrieved = CAST_INTEGER.getRound(2.418);
        assertEquals(2, retrieved);
        retrieved = CAST_INTEGER.getRound(-2.418);
        assertEquals(-2, retrieved);
    }

    @Test
    public void getCeiling() {
        int retrieved = CAST_INTEGER.getCeiling(2.718);
        assertEquals(3, retrieved);
        retrieved = CAST_INTEGER.getCeiling(-2.718);
        assertEquals(-2, retrieved);
        retrieved = CAST_INTEGER.getCeiling(2.418);
        assertEquals(3, retrieved);
        retrieved = CAST_INTEGER.getCeiling(-2.418);
        assertEquals(-2, retrieved);
    }

    @Test
    public void getFloor() {
        int retrieved = CAST_INTEGER.getFloor(2.718);
        assertEquals(2, retrieved);
        retrieved = CAST_INTEGER.getFloor(-2.718);
        assertEquals(-3, retrieved);
        retrieved = CAST_INTEGER.getFloor(2.418);
        assertEquals(2, retrieved);
        retrieved = CAST_INTEGER.getFloor(-2.418);
        assertEquals(-3, retrieved);
    }
}