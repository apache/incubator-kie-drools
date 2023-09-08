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
package org.drools.mvel.java;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.JavaDialectConfiguration;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * There are options to use various flavours of runtime compilers.
 * Apache JCI is used as the interface to all the runtime compilers.
 * 
 * You can also use the system property "drools.dialect.java.compiler" to set the desired compiler.
 * The valid values are "ECLIPSE" and "NATIVE" only.
 * 
 * drools.dialect.java.compiler = <ECLIPSE|NATIVE>
 * drools.dialect.java.compiler.lnglevel = <1.5|1.6>
 * 
 * The default compiler is Eclipse and the default lngLevel is 1.5.
 * The lngLevel will attempt to autodiscover your system using the 
 * system property "java.version"
 */
public class JavaForMvelDialectConfiguration extends JavaDialectConfiguration {

    protected static final transient Logger logger = LoggerFactory.getLogger( JavaForMvelDialectConfiguration.class);

    public JavaForMvelDialectConfiguration() { }

    public JavaForMvelDialectConfiguration(KnowledgeBuilderConfigurationImpl conf) {
        super( conf );
    }

    @Override
    public Dialect newDialect(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, PackageRegistry pkgRegistry, InternalKnowledgePackage pkg) {
        return new JavaDialect(rootClassLoader, pkgConf, pkgRegistry, pkg);
    }
}
