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

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author salaboy
 */
@ConversationScoped
public class MySimpleConversationScopedBean implements PassivationCapable, Serializable {

    @Inject
    private Conversation conversation;

    @Inject
    private MyBean myBean;

    public MySimpleConversationScopedBean() {
        System.out.println(">>> new MyConversationScopedBean: " + this.hashCode());

    }

    public String getId() {
        return "MyConversationScopedBean-" + UUID.randomUUID().toString();
    }

    public String doSomething(String string) {
        System.out.println(" >> Doing Something: " + string);
        return myBean.doSomething(string);
    }

    public MyBean getMyBean() {
        return myBean;
    }

    public void begin() {
        conversation.begin();
    }

    public void begin(String id) {
        conversation.begin(id);
    }

    public void end() {
        conversation.end();
    }

}
