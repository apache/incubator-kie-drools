/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
public class MyApplicationScopedBean {

    @Inject
    @KSession
//    @KReleaseId(groupId = "org.drools.workshop", artifactId = "my-first-drools-kjar", version = "1.0-SNAPSHOT")
    private KieSession kSession;

    @Inject
    private MyBean myBean;

    public MyApplicationScopedBean() {
        System.out.println(">>> new MyApplicationScopedBean: " + this.hashCode());

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
