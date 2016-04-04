/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

import javax.inject.Inject;
import javax.inject.Named;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author salaboy
 */
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
