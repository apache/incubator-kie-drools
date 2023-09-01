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
package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.base.definitions.InternalKnowledgePackage;

/**
 * Each Dialect can have its own configuration. Implementations of this class are typically
 * loaded via reflection in PackageBuilderConfiguration during the call to buildDialectRegistry().
 * This Class api is subject to change.
 */
public interface DialectConfiguration {
    
    void init(KnowledgeBuilderConfigurationImpl configuration);
    
    Dialect newDialect(ClassLoader rootClassLoader,
                       KnowledgeBuilderConfigurationImpl pkgConf,
                       PackageRegistry pkgRegistry,
                       InternalKnowledgePackage pkg);
    
    KnowledgeBuilderConfigurationImpl getPackageBuilderConfiguration();
}
