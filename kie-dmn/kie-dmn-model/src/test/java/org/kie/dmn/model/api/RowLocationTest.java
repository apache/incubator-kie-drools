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
package org.kie.dmn.model.api;

import javax.xml.stream.Location;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RowLocationTest {

    @Test
    void smokeTest() {
        RowLocation ut = new RowLocation(new DummyLocation());
        assertThat(ut.getCharacterOffset()).isEqualTo(-1);
        assertThat(ut.getColumnNumber()).isEqualTo(-1);
        assertThat(ut.toString()).hasSizeGreaterThan(0);
    }

    private static class DummyLocation implements Location {

        @Override
        public int getLineNumber() {
            return 47;
        }

        @Override
        public int getColumnNumber() {
            return -1;
        }

        @Override
        public int getCharacterOffset() {
            return -1;
        }

        @Override
        public String getPublicId() {
            return "publicId";
        }

        @Override
        public String getSystemId() {
            return "systemId";
        }

    }
}
