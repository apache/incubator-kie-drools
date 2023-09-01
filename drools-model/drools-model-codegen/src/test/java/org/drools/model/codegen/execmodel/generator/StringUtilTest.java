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
package org.drools.model.codegen.execmodel.generator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.modelcompiler.util.StringUtil.toId;

public class StringUtilTest {

    @Test
    public void test() {
        assertThat(toId("123stella")).isEqualTo("__123stella");
        assertThat(toId("123_stella")).isEqualTo("__123__stella");
        assertThat(toId("_stella")).isEqualTo("__stella");
        assertThat(toId("_stella_123")).isEqualTo("__stella__123");
        assertThat(toId("my stella")).isEqualTo("my_32stella");
        assertThat(toId("$tella")).isEqualTo("$tella");
        assertThat(toId("$tella(123)")).isEqualTo("$tella_40123_41");
        assertThat(toId("my-stella")).isEqualTo("my_45stella");
        assertThat(toId("my+stella")).isEqualTo("my_43stella");
        assertThat(toId("o'stella")).isEqualTo("o_39stella");
        assertThat(toId("stella&you")).isEqualTo("stella_38you");
        assertThat(toId("stella & Co.")).isEqualTo("stella_32_38_32Co_46");
    }
}
