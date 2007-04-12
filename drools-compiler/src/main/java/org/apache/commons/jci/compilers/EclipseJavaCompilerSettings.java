package org.apache.commons.jci.compilers;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.jci.compilers.JavaCompilerSettings;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

public class EclipseJavaCompilerSettings extends JavaCompilerSettings {

    final private Map defaultEclipseSettings = new HashMap();

    public EclipseJavaCompilerSettings() {
        this.defaultEclipseSettings.put( CompilerOptions.OPTION_LineNumberAttribute,
                                    CompilerOptions.GENERATE );
        this.defaultEclipseSettings.put( CompilerOptions.OPTION_SourceFileAttribute,
                                    CompilerOptions.GENERATE );
        this.defaultEclipseSettings.put( CompilerOptions.OPTION_ReportUnusedImport,
                                    CompilerOptions.IGNORE );
        this.defaultEclipseSettings.put( CompilerOptions.OPTION_LocalVariableAttribute,
                                    CompilerOptions.GENERATE );
    }

    public Map getMap() {
        final Map map = new HashMap( this.defaultEclipseSettings );

        map.put( CompilerOptions.OPTION_ReportDeprecation,
                 CompilerOptions.GENERATE );
        map.put( CompilerOptions.OPTION_TargetPlatform,
                 CompilerOptions.VERSION_1_4 );
        map.put( CompilerOptions.OPTION_Source,
                 CompilerOptions.VERSION_1_4 );
        map.put( CompilerOptions.OPTION_Encoding,
                 "UTF-8" );

        return map;
    }
}
