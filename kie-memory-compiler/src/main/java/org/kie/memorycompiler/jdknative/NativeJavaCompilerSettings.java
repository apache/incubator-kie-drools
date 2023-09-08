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
package org.kie.memorycompiler.jdknative;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import org.kie.memorycompiler.JavaCompilerSettings;

public class NativeJavaCompilerSettings extends JavaCompilerSettings {

    public NativeJavaCompilerSettings() {
        super();
    }

    public NativeJavaCompilerSettings( final JavaCompilerSettings pSettings ) {
        super(pSettings);
    }

    /**
     * Creates list of 'javac' options which can be directly used in
     * #{{@link javax.tools.JavaCompiler#getTask(Writer, JavaFileManager, DiagnosticListener, Iterable, Iterable, Iterable)}}
     *
     * See http://docs.oracle.com/javase/8/docs/technotes/tools/unix/javac.html for full list of javac options.
     *
     * @return list of strings which represent the compiler options
     */
    public List<String> toOptionsList() {
        List<String> options = new ArrayList<>();
        options.add("-source");
        options.add(getSourceVersion());
        options.add("-target");
        options.add(getTargetVersion());
        options.add("-encoding");
        options.add(getSourceEncoding());
        if (isDeprecations()) {
            options.add("-deprecation");
        }
        if (isDebug()) {
            options.add("-g");
        }
        if (isWarnings()) {
            options.add("-Xlint:all");
        }
        options.addAll( getOptions() );
        return options;
    }

}
