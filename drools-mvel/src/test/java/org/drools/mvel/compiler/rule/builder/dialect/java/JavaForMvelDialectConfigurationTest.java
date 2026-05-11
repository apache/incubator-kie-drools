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
package org.drools.mvel.compiler.rule.builder.dialect.java;

import org.drools.mvel.java.JavaForMvelDialectConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JavaForMvelDialectConfigurationTest {

    @Test
    public void acceptsSupportedVersions() {
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("17");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("18");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("19");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("20");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("21");
    }

    @Test
    public void rejectsObsoleteVersions() {
        assertThatThrownBy(() -> new JavaForMvelDialectConfiguration().setJavaLanguageLevel("1.8"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not a valid language level");
        assertThatThrownBy(() -> new JavaForMvelDialectConfiguration().setJavaLanguageLevel("11"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not a valid language level");
    }
}
