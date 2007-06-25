/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.commons.jci.compilers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

/**
 * Native Eclipse compiler settings
 * 
 * @author tcurdt
 */
public final class EclipseJavaCompilerSettings extends JavaCompilerSettings {

    final private Map defaultEclipseSettings = new HashMap();

    public EclipseJavaCompilerSettings() {
        defaultEclipseSettings.put( CompilerOptions.OPTION_LineNumberAttribute,
                                    CompilerOptions.GENERATE );
        defaultEclipseSettings.put( CompilerOptions.OPTION_SourceFileAttribute,
                                    CompilerOptions.GENERATE );
        defaultEclipseSettings.put( CompilerOptions.OPTION_ReportUnusedImport,
                                    CompilerOptions.IGNORE );
        defaultEclipseSettings.put( CompilerOptions.OPTION_LocalVariableAttribute,
                                    CompilerOptions.GENERATE );
    }

    public EclipseJavaCompilerSettings(final Map pMap) {
        defaultEclipseSettings.putAll( pMap );
    }

    public Map getMap() {
        final Map map = new HashMap( defaultEclipseSettings );

        map.put( CompilerOptions.OPTION_ReportDeprecation,
                 CompilerOptions.GENERATE ); // Not sure what we put here from JavaCompilerSettings
        map.put( CompilerOptions.OPTION_TargetPlatform,
                 (getTargetVersion() != null) ? getTargetVersion() : CompilerOptions.VERSION_1_4 );
        map.put( CompilerOptions.OPTION_Source,
                 (getSourceVersion() != null) ? getSourceVersion() : CompilerOptions.VERSION_1_4 );
        map.put( CompilerOptions.OPTION_Encoding,
                 (getSourceEncoding() != null) ? getSourceEncoding() : "UTF-8" );

        return map;
    }

    public String toString() {
        return defaultEclipseSettings.toString();
    }
}
