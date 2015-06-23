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

package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.definitions.InternalKnowledgePackage;

/**
 * Each Dialect can have its own configuration. Implementations of this class are typically
 * loaded via reflection in PackageBuilderConfiguration during the call to buildDialectRegistry().
 * This Class api is subject to change.
 */
public interface DialectConfiguration {
    
    public void init(KnowledgeBuilderConfigurationImpl configuration);
    
    public Dialect newDialect(ClassLoader rootClassLoader,
                              KnowledgeBuilderConfigurationImpl pkgConf,
                              PackageRegistry pkgRegistry,
                              InternalKnowledgePackage pkg);
    
    public KnowledgeBuilderConfigurationImpl getPackageBuilderConfiguration();
}
