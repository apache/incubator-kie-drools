/**
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier;

import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.data.VerifierReport;

public interface Verifier {

    /**
     * Add resource that is verified.
     * 
     * @param descr
     */
    public void addResourcesToVerify(Resource resource,
                                     ResourceType type);
    
    public void addResourcesToVerify(Resource resource,
            ResourceType type, ResourceConfiguration config);

    /**
     * Give model info optionally as a jar. This way verifier doesn't have to figure out the field types.
     */
    public void addObjectModel(JarInputStream jar);

    public void flushKnowledgeSession();

    /**
     * 
     * This will run the verifier.
     * 
     * @return true if everything worked.
     */
    public boolean fireAnalysis();

    public boolean fireAnalysis(ScopesAgendaFilter scopesAgendaFilter);

    public VerifierReport getResult();

    public boolean hasErrors();

    public List<VerifierError> getErrors();

    public void dispose();

}