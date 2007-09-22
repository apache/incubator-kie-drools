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
        defaultEclipseSettings.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
        defaultEclipseSettings.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
        defaultEclipseSettings.put(CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.IGNORE);
        defaultEclipseSettings.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
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
		private static final long serialVersionUID = 1L;
	{
    	put("1.1", CompilerOptions.VERSION_1_1);
    	put("1.2", CompilerOptions.VERSION_1_2);
    	put("1.3", CompilerOptions.VERSION_1_3);
    	put("1.4", CompilerOptions.VERSION_1_4);
    	put("1.5", CompilerOptions.VERSION_1_5);
    	put("1.6", CompilerOptions.VERSION_1_6);
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

        map.put(CompilerOptions.OPTION_SuppressWarnings, isWarnings()?CompilerOptions.GENERATE:CompilerOptions.DO_NOT_GENERATE);
        map.put(CompilerOptions.OPTION_ReportDeprecation, isDeprecations()?CompilerOptions.GENERATE:CompilerOptions.DO_NOT_GENERATE);
        map.put(CompilerOptions.OPTION_TargetPlatform, toNativeVersion(getTargetVersion()));
        map.put(CompilerOptions.OPTION_Source, toNativeVersion(getSourceVersion()));
        map.put(CompilerOptions.OPTION_Encoding, getSourceEncoding());

        return map;
    }
    
    public String toString() {
        return toNativeSettings().toString();
    }
}
