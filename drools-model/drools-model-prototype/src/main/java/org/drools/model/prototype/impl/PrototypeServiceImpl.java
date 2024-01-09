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
package org.drools.model.prototype.impl;

import java.util.Map;
import java.util.function.Function;

import org.drools.base.base.ObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.base.prototype.PrototypeObjectType;
import org.drools.model.Variable;
import org.drools.model.prototype.PrototypeVariable;
import org.drools.modelcompiler.PrototypeService;
import org.kie.api.prototype.Prototype;

public class PrototypeServiceImpl implements PrototypeService {

    public ObjectType getPrototypeObjectType(Map<String, ObjectType> objectTypeCache, Map<String, InternalKnowledgePackage> packages, Function<String, InternalKnowledgePackage> packageCreator, Variable patternVariable ) {
        Prototype prototype = ((PrototypeVariable) patternVariable).getPrototype();
        return objectTypeCache.computeIfAbsent( prototype.getFullName(), name -> {
            KnowledgePackageImpl pkg = (KnowledgePackageImpl) packages.computeIfAbsent(prototype.getPackage(), packageCreator );
            PrototypeObjectType objectType = new PrototypeObjectType(prototype);
            objectType.setEvent(prototype.isEvent());
            return objectType;
        } );
    }
}
