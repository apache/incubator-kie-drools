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

package org.drools.compiler.kie.builder.impl;

import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

public interface KieProject {
    
    ReleaseId getGAV();
    
    InternalKieModule getKieModuleForKBase(String kBaseName);

    Collection<String> getKieBaseNames();

    KieBaseModel getKieBaseModel(String kBaseName);

    KieBaseModel getDefaultKieBaseModel();

    KieSessionModel getKieSessionModel(String kSessionName);

    KieSessionModel getDefaultKieSession();

    KieSessionModel getDefaultStatelessKieSession();

    void init();   
    
    ClassLoader getClassLoader();

    ClassLoader getClonedClassLoader();

    ResultsImpl verify();

    ResultsImpl verify( String... kModelNames );

    long getCreationTimestamp();

    Set<String> getTransitiveIncludes(String kBaseName);
    Set<String> getTransitiveIncludes(KieBaseModel kBaseModel);

    InputStream getPomAsStream();
}
