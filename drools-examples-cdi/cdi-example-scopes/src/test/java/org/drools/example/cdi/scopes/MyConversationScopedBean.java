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

import java.io.Serializable;
import java.util.UUID;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Inject;

import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

@ConversationScoped
public class MyConversationScopedBean implements PassivationCapable, Serializable{

    @Inject
    private Conversation conversation;
    
    @Inject
    @KSession
    private KieSession kSession;

    @Inject
    private MyBean myBean;

    public MyConversationScopedBean() {
        System.out.println(">>> new MyConversationScopedBean: " + this.hashCode());

    }

    public String getId() {
        return "MyConversationScopedBean-"+UUID.randomUUID().toString();
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
    
    public void begin(){
        conversation.begin();
    }
    
    public void begin(String id){
        conversation.begin(id);
    }
    
    public void end(){
        conversation.end();
    }

}
