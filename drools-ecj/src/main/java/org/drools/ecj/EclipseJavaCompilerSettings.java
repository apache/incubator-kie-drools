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
package org.drools.ecj;

import java.util.HashMap;
import java.util.Map;

import org.kie.memorycompiler.JavaCompilerSettings;

/**
 * Native Eclipse compiler settings.
 *
 * Supported Java version strings are defined in {@link org.kie.memorycompiler.JavaConfiguration#LANGUAGE_LEVELS}.
 * Version strings are passed through to the Eclipse compiler as-is.
 */
public final class EclipseJavaCompilerSettings extends JavaCompilerSettings {

    public static final String CompilerOptions_GENERATE = "generate";//$NON-NLS-1$
    public static final String CompilerOptions_DO_NOT_GENERATE = "do not generate"; //$NON-NLS-1$
    public static final String CompilerOptions_PRESERVE = "preserve"; //$NON-NLS-1$
    public static final String CompilerOptions_OPTIMIZE_OUT = "optimize out"; //$NON-NLS-1$
    public static final String CompilerOptions_ERROR = "error"; //$NON-NLS-1$
    public static final String CompilerOptions_WARNING = "warning"; //$NON-NLS-1$
    public static final String CompilerOptions_IGNORE = "ignore"; //$NON-NLS-1$

    public static final String CompilerOptions_OPTION_LineNumberAttribute = "org.eclipse.jdt.core.compiler.debug.lineNumber"; //$NON-NLS-1$
    public static final String CompilerOptions_OPTION_SourceFileAttribute = "org.eclipse.jdt.core.compiler.debug.sourceFile"; //$NON-NLS-1$
    public static final String CompilerOptions_OPTION_LocalVariableAttribute = "org.eclipse.jdt.core.compiler.debug.localVariable"; //$NON-NLS-1$
    public static final String CompilerOptions_OPTION_ReportUnusedImport = "org.eclipse.jdt.core.compiler.problem.unusedImport"; //$NON-NLS-1$

    public static final String CompilerOptions_OPTION_SuppressWarnings =  "org.eclipse.jdt.core.compiler.problem.suppressWarnings"; //$NON-NLS-1$
    public static final String CompilerOptions_OPTION_Encoding = "org.eclipse.jdt.core.encoding"; //$NON-NLS-1$
    public static final String CompilerOptions_OPTION_Source = "org.eclipse.jdt.core.compiler.source"; //$NON-NLS-1$
    public static final String CompilerOptions_OPTION_TargetPlatform = "org.eclipse.jdt.core.compiler.codegen.targetPlatform"; //$NON-NLS-1$
    public static final String CompilerOptions_OPTION_Compliance = "org.eclipse.jdt.core.compiler.compliance"; //$NON-NLS-1$
    public static final String CompilerOptions_OPTION_ReportDeprecation = "org.eclipse.jdt.core.compiler.problem.deprecation"; //$NON-NLS-1$    

    final private Map defaultEclipseSettings = new HashMap();

    public EclipseJavaCompilerSettings() {
        defaultEclipseSettings.put(CompilerOptions_OPTION_LineNumberAttribute, CompilerOptions_GENERATE);
        defaultEclipseSettings.put(CompilerOptions_OPTION_SourceFileAttribute, CompilerOptions_GENERATE);
        defaultEclipseSettings.put(CompilerOptions_OPTION_ReportUnusedImport, CompilerOptions_IGNORE);
        defaultEclipseSettings.put(CompilerOptions_OPTION_LocalVariableAttribute, CompilerOptions_GENERATE);
    }
    
    public EclipseJavaCompilerSettings( final JavaCompilerSettings pSettings ) {
        super(pSettings);

        if (pSettings instanceof EclipseJavaCompilerSettings) {
            defaultEclipseSettings.putAll(((EclipseJavaCompilerSettings)pSettings).toNativeSettings());
        }
    }
    
    public EclipseJavaCompilerSettings( final Map pMap ) {
        defaultEclipseSettings.putAll(pMap);
    }

    
    Map toNativeSettings() {
        final Map map = new HashMap(defaultEclipseSettings);

        map.put(CompilerOptions_OPTION_SuppressWarnings, isWarnings()?CompilerOptions_GENERATE:CompilerOptions_DO_NOT_GENERATE);
        map.put(CompilerOptions_OPTION_ReportDeprecation, isDeprecations()?CompilerOptions_GENERATE:CompilerOptions_DO_NOT_GENERATE);
        map.put(CompilerOptions_OPTION_TargetPlatform, getTargetVersion());
        map.put(CompilerOptions_OPTION_Source, getSourceVersion());
        map.put(CompilerOptions_OPTION_Compliance, getSourceVersion());
        map.put(CompilerOptions_OPTION_Encoding, getSourceEncoding());

        return map;
    }
    
    public String toString() {
        return toNativeSettings().toString();
    }
}
