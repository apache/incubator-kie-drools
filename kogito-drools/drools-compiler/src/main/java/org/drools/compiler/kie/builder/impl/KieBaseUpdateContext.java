/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.kie.builder.impl;

import java.util.List;

import org.drools.compiler.kie.util.KieJarChangeSet;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.builder.ReleaseId;

public class KieBaseUpdateContext {

    public final KieProject kProject;
    public final InternalKnowledgeBase kBase;
    public final InternalKieModule currentKM;
    public final ReleaseId newReleaseId;
    public final InternalKieModule newKM;
    public final KieJarChangeSet cs;
    public final List<Class<?>> modifiedClasses;
    public final boolean modifyingUsedClass;
    public final List<String> unchangedResources;
    public final ResultsImpl results;
    public final KieBaseModelImpl newKieBaseModel;
    public final KieBaseModelImpl currentKieBaseModel;

    public KieBaseUpdateContext( KieProject kProject, InternalKnowledgeBase kBase, InternalKieModule currentKM, ReleaseId newReleaseId,
                                 InternalKieModule newKM, KieJarChangeSet cs, List<Class<?>> modifiedClasses,
                                 boolean modifyingUsedClass, List<String> unchangedResources, ResultsImpl results,
                                 KieBaseModelImpl newKieBaseModel, KieBaseModelImpl currentKieBaseModel ) {
        this.kProject = kProject;
        this.kBase = kBase;
        this.currentKM = currentKM;
        this.newReleaseId = newReleaseId;
        this.newKM = newKM;
        this.cs = cs;
        this.modifiedClasses = modifiedClasses;
        this.modifyingUsedClass = modifyingUsedClass;
        this.unchangedResources = unchangedResources;
        this.results = results;
        this.newKieBaseModel = newKieBaseModel;
        this.currentKieBaseModel = currentKieBaseModel;
    }
}
