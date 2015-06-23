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

import org.drools.compiler.kproject.xml.PomModel;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;

import java.io.InputStream;

public interface InternalKieScanner extends KieScanner {

    enum Status {
        STARTING, SCANNING, UPDATING, RUNNING, STOPPED, SHUTDOWN
    }

    void setKieContainer(KieContainer kieContainer);

    KieModule loadArtifact(ReleaseId releaseId);
    
    KieModule loadArtifact(ReleaseId releaseId, InputStream pomXML);

    KieModule loadArtifact(ReleaseId releaseId, PomModel pomModel);

    String getArtifactVersion(ReleaseId releaseId);

    ReleaseId getScannerReleaseId();

    ReleaseId getCurrentReleaseId();

    Status getStatus();

    long getPollingInterval();
}
