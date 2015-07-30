/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.rule.builder.dialect.asm;

import org.kie.internal.utils.ChainedProperties;

import static org.mvel2.asm.Opcodes.V1_5;
import static org.mvel2.asm.Opcodes.V1_6;
import static org.mvel2.asm.Opcodes.V1_7;
import static org.mvel2.asm.Opcodes.V1_8;

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
        } else if ( level.startsWith( "1.8" ) ) {
            return V1_8;
        } else {
            return V1_7;
        }
    }
}
