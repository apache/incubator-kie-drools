/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder.dialect.mvel;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectConfiguration;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;

/**
 * The MVEL dialect.
 * 
 * drools.dialect.mvel.strict = <true|false>
 * 
 * Default strict is true, which means all expressions and the consequence are type safe.
 * However dynamic mode is still used while executed nested accessors in the field constraints.
 */
public class MVELDialectConfiguration
    implements
        DialectConfiguration {

    private KnowledgeBuilderConfigurationImpl conf;

    private boolean                     strict;
    
    private int                         langLevel;

    public Dialect newDialect(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, PackageRegistry pkgRegistry, InternalKnowledgePackage pkg) {
        return new MVELDialect(rootClassLoader, pkgConf, pkgRegistry, pkg);
    }

    public void init(KnowledgeBuilderConfigurationImpl conf) {
        this.conf = conf;
        setStrict( determineStrict() );
        setLangLevel( determineLangLevel() );
    }

    public KnowledgeBuilderConfigurationImpl getPackageBuilderConfiguration() {
        return this.conf;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public boolean isStrict() {
        return this.strict;
    }
    
    public void setLangLevel(int langLevel) {
        this.langLevel = langLevel;
    }
    
    public int getLangLevel() {
        return this.langLevel;
    }

    private boolean determineStrict() {
        final String prop = this.conf.getChainedProperties().getProperty( "drools.dialect.mvel.strict",
                                                                          "true" );
        return Boolean.valueOf( prop ).booleanValue();
    }
    
    private int determineLangLevel() {
        final String prop = this.conf.getChainedProperties().getProperty( "drools.dialect.mvel.langLevel",
                                                                          "4" );
        return Integer.valueOf( prop ).intValue();
    }

}
