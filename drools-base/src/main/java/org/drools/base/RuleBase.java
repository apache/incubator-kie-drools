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
package org.drools.base;


import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.InvalidPatternException;
import org.drools.base.rule.TypeDeclaration;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.KiePackage;

public interface RuleBase {
    ClassLoader getRootClassLoader();

    TypeDeclaration getOrCreateExactTypeDeclaration(Class<?> nodeClass);

    TypeDeclaration getTypeDeclaration(Class<?> classType);

    KieBaseConfiguration getConfiguration();
    
    InternalKnowledgePackage[] getPackages();
    InternalKnowledgePackage getPackage(String name);
    Future<KiePackage> addPackage(KiePackage pkg ); 
    void addPackages( Collection<? extends KiePackage> newPkgs );
    Map<String, InternalKnowledgePackage> getPackagesMap();

    void addRules( Collection<RuleImpl> rules ) throws InvalidPatternException;
    void removeRules( Collection<RuleImpl> rules ) throws InvalidPatternException;

    String getId();
    
    String getContainerId();
    void setContainerId(String containerId);
    
    Map<String, Type> getGlobals();


}
