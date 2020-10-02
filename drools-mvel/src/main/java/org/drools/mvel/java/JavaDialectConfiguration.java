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

package org.drools.mvel.java;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectConfiguration;
import org.drools.compiler.compiler.JavaConfiguration;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * There are options to use various flavours of runtime compilers.
 * Apache JCI is used as the interface to all the runtime compilers.
 * 
 * You can also use the system property "drools.compiler" to set the desired compiler.
 * The valid values are "ECLIPSE" and "JANINO" only. 
 * 
 * drools.dialect.java.compiler = <ECLIPSE|JANINO>
 * drools.dialect.java.compiler.lnglevel = <1.5|1.6>
 * 
 * The default compiler is Eclipse and the default lngLevel is 1.5.
 * The lngLevel will attempt to autodiscover your system using the 
 * system property "java.version"
 * 
 * The JavaDialectConfiguration will attempt to validate that the specified compiler
 * is in the classpath, using ClassLoader.loadClass(String). If you intented to
 * just Janino sa the compiler you must either overload the compiler property before 
 * instantiating this class or the PackageBuilder, or make sure Eclipse is in the 
 * classpath, as Eclipse is the default.
 */
public class JavaDialectConfiguration extends JavaConfiguration
    implements
    DialectConfiguration {

    protected static final transient Logger logger = LoggerFactory.getLogger(JavaDialectConfiguration.class);
    
    public JavaDialectConfiguration() {
    }

    public Dialect newDialect(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, PackageRegistry pkgRegistry, InternalKnowledgePackage pkg) {
        return new JavaDialect(rootClassLoader, pkgConf, pkgRegistry, pkg);
    }
}
