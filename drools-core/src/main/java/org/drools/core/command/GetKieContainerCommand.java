/*
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

package org.drools.core.command;

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.Context;

public class GetKieContainerCommand
    implements
    ExecutableCommand<KieContainer> {

    private static final long serialVersionUID = 8748826714594402049L;
    private ReleaseId releaseId;

    public GetKieContainerCommand(ReleaseId releaseId) {
        this.releaseId = releaseId;
    }

    public KieContainer execute(Context context) {
        // use the new API to retrieve the session by ID
        KieServices  kieServices  = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        ((RegistryContext)context).register( KieContainer.class, kieContainer );
        return kieContainer;
    }

    public ReleaseId getReleaseId() {
        return releaseId;
    }

    @Override
    public String toString() {
        return "GetKieContainerCommand{" +
               "releaseId=" + releaseId +
               '}';
    }
}
