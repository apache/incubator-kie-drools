/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.memorycompiler;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Most common denominator for JavaCompiler settings.
 * 
 * If you need more specific settings you have to provide
 * the native compiler configurations to the compilers.
 * Writing of a custom factory is suggested. 
 */
public class JavaCompilerSettings {

    private String targetVersion = "1.8";
    private String sourceVersion = "1.8";
    private String sourceEncoding = "UTF-8";
    private boolean warnings = false;
    private boolean deprecations = false;
    private boolean debug = false;
    private List<File> classpaths;

    private final List<String> options;

    /** @deprecated */
    private boolean verbose = false;

    public JavaCompilerSettings() {
        options = new ArrayList<>();
    }
    
    public JavaCompilerSettings( final JavaCompilerSettings pSettings ) {
        targetVersion = pSettings.targetVersion;
        sourceVersion = pSettings.sourceVersion;
        sourceEncoding = pSettings.sourceEncoding;
        warnings = pSettings.warnings;
        deprecations = pSettings.deprecations;
        debug = pSettings.debug;
        options = pSettings.options;
    }
    
    public void setTargetVersion( final String pTargetVersion ) {
        targetVersion = pTargetVersion;
    }

    public String getTargetVersion() {
        return targetVersion;
    }


    public void setSourceVersion( final String pSourceVersion ) {
        sourceVersion = pSourceVersion;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }


    public void setSourceEncoding( final String pSourceEncoding ) {
        sourceEncoding = pSourceEncoding;
    }

    public String getSourceEncoding() {
        return sourceEncoding;
    }


    public void setWarnings( final boolean pWarnings ) {
        warnings = pWarnings;
    }

    public boolean isWarnings() {
        return warnings;
    }


    public void setDeprecations( final boolean pDeprecations )  {
        deprecations = pDeprecations;
    }

    public boolean isDeprecations() {
        return deprecations;
    }

    public void setDebug( final boolean pDebug )  {
        debug = pDebug;
    }

    public boolean isDebug() {
        return debug;
    }

    /** @deprecated */
    public void setVerbose( final boolean pVerbose ) {
        verbose = pVerbose;
    }

    /** @deprecated */
    public boolean isVerbose() {
        return verbose;
    }

    public List<File> getClasspathLocations() {
        return classpaths;
    }

    public void addClasspath(File classpath) {
        if ( classpaths == null) {
            classpaths = new ArrayList<>();
        }
        classpaths.add(classpath);
    }

    public List<String> getOptions() {
        return options;
    }

    public void addOption(String option) {
        options.add(option);
    }
}
