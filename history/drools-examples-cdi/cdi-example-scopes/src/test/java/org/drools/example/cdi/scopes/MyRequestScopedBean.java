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

package org.drools.example.cdi.scopes;

import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class MyRequestScopedBean {

    @Inject
    @KSession
    private KieSession kSession;

    @Inject
    private MyBean myBean;

    public MyRequestScopedBean() {
        System.out.println(">>> new MyRequestBean: " + this.hashCode());

    }

    public int doSomething(String string) {
        System.out.println(" >> Doing Something: " + string);
        kSession.insert(string);
        myBean.doSomething(string);
        return kSession.fireAllRules();
    }

    public KieSession getkSession() {
        return kSession;
    }

    public MyBean getMyBean() {
        return myBean;
    }

}
