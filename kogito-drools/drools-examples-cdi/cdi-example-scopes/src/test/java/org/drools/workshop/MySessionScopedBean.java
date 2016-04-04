/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

import java.io.Serializable;
import java.util.UUID;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Inject;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author salaboy
 */
@SessionScoped
public class MySessionScopedBean implements PassivationCapable, Serializable {

    
    @Inject
    @KSession
//    @KReleaseId(groupId = "org.drools.workshop", artifactId = "my-first-drools-kjar", version = "1.0-SNAPSHOT")
    private KieSession kSession;

    @Inject
    private MyBean myBean;

    public MySessionScopedBean() {
        System.out.println(">>> new MySessionScopedBean: " + this.hashCode());

    }

    public String getId() {
        return "MySessionScopedBean-"+UUID.randomUUID().toString();
    }
    
    

    public int doSomething(String string) {
        System.out.println(" >> Doing Something: " + string);
        kSession.insert(string);
        String doSomething = myBean.doSomething(string);
        return kSession.fireAllRules();
    }

    public KieSession getkSession() {
        return kSession;
    }

    public MyBean getMyBean() {
        return myBean;
    }

}
