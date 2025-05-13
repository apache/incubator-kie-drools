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
package org.kie.dmn.api.identifiers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LocalComponentIdDmnTest {

    @Test
    void getEncodedNameSpace() {
        String nameSpace = "https://kie.org/dmn/_5B448C78-0DBF-4554-92A4-8C0247EB01FD";
        String retrieved = LocalComponentIdDmn.getEncodedNameSpace(nameSpace);
        String expected = "https%3A/kie.org/dmn/_5B448C78-0DBF-4554-92A4-8C0247EB01FD";
        assertThat(retrieved).isEqualTo(expected);

        nameSpace = "http://kie.org/dmn/_5B448C78-0DBF-4554-92A4-8C0247EB01FD";
        retrieved = LocalComponentIdDmn.getEncodedNameSpace(nameSpace);
        expected = "http%3A/kie.org/dmn/_5B448C78-0DBF-4554-92A4-8C0247EB01FD";
        assertThat(retrieved).isEqualTo(expected);


    }
}