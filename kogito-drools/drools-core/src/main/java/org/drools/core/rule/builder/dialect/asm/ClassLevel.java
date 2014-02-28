package org.drools.core.rule.builder.dialect.asm;

import org.kie.internal.utils.ChainedProperties;

import static org.mvel2.asm.Opcodes.V1_5;
import static org.mvel2.asm.Opcodes.V1_6;
import static org.mvel2.asm.Opcodes.V1_7;

public class ClassLevel {
    public static final String JAVA_LANG_LEVEL_PROPERTY = "drools.dialect.java.compiler.lnglevel";

    private static volatile int javaVersion = -1;

    public static int getJavaVersion(ClassLoader classLoader) {
        if (javaVersion < 0) {
            synchronized (ClassGenerator.class) {
                if (javaVersion < 0) {
                    findJavaVersion(classLoader);
                }
            }
        }
        return javaVersion;
    }

    private static void findJavaVersion(ClassLoader classLoader) {
        ChainedProperties chainedProperties = new ChainedProperties( "packagebuilder.conf",
                                                                     classLoader,
                                                                     true );

        if (chainedProperties.getProperty("drools.dialect.java", null) == null) {
            chainedProperties = new ChainedProperties( "packagebuilder.conf",
                                                       ClassGenerator.class.getClassLoader(),
                                                       true );
        }

        javaVersion = findJavaVersion(chainedProperties);
    }

    public static int findJavaVersion(ChainedProperties chainedProperties) {
        String level = chainedProperties.getProperty(JAVA_LANG_LEVEL_PROPERTY,
                                                     System.getProperty("java.version"));

        if ( level.startsWith( "1.5" ) ) {
            return V1_5;
        } else if ( level.startsWith( "1.6" ) ) {
            return V1_6;
        } else if ( level.startsWith( "1.7" ) ) {
            return V1_7;
        } else {
            return V1_6;
        }
    }
}
