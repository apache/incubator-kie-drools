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

package org.drools.compiler.commons.jci.compilers;

import java.util.HashMap;
import java.util.Map;

/**
 * Native Eclipse compiler settings
 */
public final class EclipseJavaCompilerSettings extends JavaCompilerSettings {
    
  //copied from org.eclipse.jdt.internal.compiler.impl.CompilerOptions as we can't access it
    public static final String CompilerOptions_VERSION_1_1 = "1.1"; //$NON-NLS-1$
    public static final String CompilerOptions_VERSION_1_2 = "1.2"; //$NON-NLS-1$
    public static final String CompilerOptions_VERSION_1_3 = "1.3"; //$NON-NLS-1$
    public static final String CompilerOptions_VERSION_1_4 = "1.4"; //$NON-NLS-1$
    public static final String CompilerOptions_VERSION_JSR14 = "jsr14"; //$NON-NLS-1$
    public static final String CompilerOptions_VERSION_CLDC1_1 = "cldc1.1"; //$NON-NLS-1$
    public static final String CompilerOptions_VERSION_1_5 = "1.5"; //$NON-NLS-1$
    public static final String CompilerOptions_VERSION_1_6 = "1.6"; //$NON-NLS-1$
    public static final String CompilerOptions_VERSION_1_7 = "1.7"; //$NON-NLS-1$
    public static final String CompilerOptions_VERSION_1_8 = "1.8"; //$NON-NLS-1$

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

    private static Map nativeVersions = new HashMap() {
        private static final long serialVersionUID = 510l;
    {
        put("1.1", CompilerOptions_VERSION_1_1);
        put("1.2", CompilerOptions_VERSION_1_2);
        put("1.3", CompilerOptions_VERSION_1_3);
        put("1.4", CompilerOptions_VERSION_1_4);
        put("1.5", CompilerOptions_VERSION_1_5);
        put("1.6", CompilerOptions_VERSION_1_6);
        put("1.7", CompilerOptions_VERSION_1_7);
        put("1.8", CompilerOptions_VERSION_1_8);
    }};
    
    private String toNativeVersion( final String pVersion ) {
        final String nativeVersion = (String) nativeVersions.get(pVersion);

        if (nativeVersion == null) {
            throw new RuntimeException("unknown version " + pVersion);
        }

        return nativeVersion;
    }
    
    Map toNativeSettings() {
        final Map map = new HashMap(defaultEclipseSettings);

        map.put(CompilerOptions_OPTION_SuppressWarnings, isWarnings()?CompilerOptions_GENERATE:CompilerOptions_DO_NOT_GENERATE);
        map.put(CompilerOptions_OPTION_ReportDeprecation, isDeprecations()?CompilerOptions_GENERATE:CompilerOptions_DO_NOT_GENERATE);
        map.put(CompilerOptions_OPTION_TargetPlatform, toNativeVersion(getTargetVersion()));
        map.put(CompilerOptions_OPTION_Source, toNativeVersion(getSourceVersion()));
        map.put(CompilerOptions_OPTION_Encoding, getSourceEncoding());

        return map;
    }
    
    public String toString() {
        return toNativeSettings().toString();
    }
}
