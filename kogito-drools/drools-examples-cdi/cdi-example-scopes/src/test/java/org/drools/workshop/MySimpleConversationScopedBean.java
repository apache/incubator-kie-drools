/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

import java.io.Serializable;
import java.util.UUID;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Inject;

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
