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
package org.drools.mvel.compiler.rule.builder.dialect.java;

import org.drools.mvel.java.JavaForMvelDialectConfiguration;
import org.junit.Test;

public class JavaForMvelDialectConfigurationTest {

    @Test
    public void checkVersion() {
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("1.5");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("1.6");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("1.7");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("9");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("10");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("11");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("12");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("13");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("14");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("15");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("16");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("17");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("18");
        new JavaForMvelDialectConfiguration().setJavaLanguageLevel("19");
    }

}