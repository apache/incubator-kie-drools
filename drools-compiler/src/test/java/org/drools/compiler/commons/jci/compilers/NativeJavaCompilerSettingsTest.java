/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.commons.jci.compilers;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Index;
import org.junit.Test;

import java.util.List;

public class NativeJavaCompilerSettingsTest {

    @Test
    public void defaultSettings() {
        NativeJavaCompilerSettings settings = new NativeJavaCompilerSettings();
        List<String> options = settings.toOptionsList();
        Assertions.assertThat(options).hasSize(6);
        Assertions.assertThat(options).contains("-source", "-target", "-encoding");
        // check the order is correct, value of the option needs to be right after the option name
        Assertions.assertThat(options).contains("1.6", Index.atIndex(options.indexOf("-source") + 1));
        Assertions.assertThat(options).contains("1.6", Index.atIndex(options.indexOf("-target") + 1));
        Assertions.assertThat(options).contains("UTF-8", Index.atIndex(options.indexOf("-encoding") + 1));
    }

    @Test
    public void allSettings() {
        NativeJavaCompilerSettings settings = new NativeJavaCompilerSettings();
        settings.setDebug(true);
        settings.setWarnings(true);
        settings.setDeprecations(true);
        settings.setSourceEncoding("My-Custom-Encoding");
        settings.setSourceVersion("1.9");
        settings.setTargetVersion("1.9");
        List<String> options = settings.toOptionsList();

        Assertions.assertThat(options).hasSize(9);
        Assertions.assertThat(options).contains("-g");
        Assertions.assertThat(options).contains("-Xlint:all");
        Assertions.assertThat(options).contains("-deprecation");
        // check the order is correct, value of the option needs to be right after the option name
        Assertions.assertThat(options).contains("1.9", Index.atIndex(options.indexOf("-source") + 1));
        Assertions.assertThat(options).contains("1.9", Index.atIndex(options.indexOf("-target") + 1));
        Assertions.assertThat(options).contains("My-Custom-Encoding", Index.atIndex(options.indexOf("-encoding") + 1));
    }

}
