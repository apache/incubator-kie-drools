/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.example.cdi.scopes;

import org.kie.api.cdi.KReleaseId;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

import javax.inject.Inject;
import javax.inject.Named;

@KieBusinessScoped
@Named("rules")
public class KieBusinessScopedRules {
    
    @Inject
    @KSession
    @KReleaseId(groupId = "org.drools.workshop", artifactId = "my-first-drools-kjar", version = "1.0-SNAPSHOT")
    private KieSession kSession;

    public KieSession getkSession() {
        return kSession;
    }

    public void setkSession(KieSession kSession) {
        this.kSession = kSession;
    }

}
