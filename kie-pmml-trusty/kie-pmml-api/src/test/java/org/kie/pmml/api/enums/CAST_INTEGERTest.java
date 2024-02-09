/**
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
package org.kie.pmml.api.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CAST_INTEGERTest {

    @Test
    void getRound() {
        int retrieved = CAST_INTEGER.getRound(2.718);
        assertThat(retrieved).isEqualTo(3);
        retrieved = CAST_INTEGER.getRound(-2.718);
        assertThat(retrieved).isEqualTo(-3);
        retrieved = CAST_INTEGER.getRound(2.418);
        assertThat(retrieved).isEqualTo(2);
        retrieved = CAST_INTEGER.getRound(-2.418);
        assertThat(retrieved).isEqualTo(-2);
    }

    @Test
    void getCeiling() {
        int retrieved = CAST_INTEGER.getCeiling(2.718);
        assertThat(retrieved).isEqualTo(3);
        retrieved = CAST_INTEGER.getCeiling(-2.718);
        assertThat(retrieved).isEqualTo(-2);
        retrieved = CAST_INTEGER.getCeiling(2.418);
        assertThat(retrieved).isEqualTo(3);
        retrieved = CAST_INTEGER.getCeiling(-2.418);
        assertThat(retrieved).isEqualTo(-2);
    }

    @Test
    void getFloor() {
        int retrieved = CAST_INTEGER.getFloor(2.718);
        assertThat(retrieved).isEqualTo(2);
        retrieved = CAST_INTEGER.getFloor(-2.718);
        assertThat(retrieved).isEqualTo(-3);
        retrieved = CAST_INTEGER.getFloor(2.418);
        assertThat(retrieved).isEqualTo(2);
        retrieved = CAST_INTEGER.getFloor(-2.418);
        assertThat(retrieved).isEqualTo(-3);
    }
}