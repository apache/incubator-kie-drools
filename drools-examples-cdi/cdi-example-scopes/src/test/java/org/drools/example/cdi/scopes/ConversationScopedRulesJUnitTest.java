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

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

/**
 *
 * @author salaboy
 */
@RunWith(Arquillian.class)
public class ConversationScopedRulesJUnitTest {

    public ConversationScopedRulesJUnitTest() {
    }

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addClasses(MyBean.class, MyConversationScopedBean.class)
                .addPackages(true, "org.apache.deltaspike")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
//        System.out.println(war.toString(true));
        return jar;
    }

    @Inject
    private MyConversationScopedBean myConversationBean;

    @Test
    public void helloConversationScoped() {
        Assert.assertNotNull(myConversationBean);
        ContextControl ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

        ctxCtrl.startContext(SessionScoped.class);
        ctxCtrl.startContext(ConversationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        myConversationBean.begin();
        String myBeanId = myConversationBean.getMyBean().getId();
        long myKieSessionId = myConversationBean.getkSession().getIdentifier();

        int result = myConversationBean.doSomething("hello 0");
        Assert.assertEquals(1, result);

        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanId, myConversationBean.getMyBean().getId());
        Assert.assertEquals(myKieSessionId, myConversationBean.getkSession().getIdentifier());

        myBeanId = myConversationBean.getMyBean().getId();
        myKieSessionId = myConversationBean.getkSession().getIdentifier();

        result = myConversationBean.doSomething("hello 1");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanId, myConversationBean.getMyBean().getId());
        Assert.assertEquals(myKieSessionId, myConversationBean.getkSession().getIdentifier());

        result = myConversationBean.doSomething("hello 2");
        Assert.assertEquals(1, result);

        myBeanId = myConversationBean.getMyBean().getId();
        myKieSessionId = myConversationBean.getkSession().getIdentifier();

        myConversationBean.end();
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(ConversationScoped.class);

        ctxCtrl.startContext(ConversationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        myConversationBean.begin();
        
        Assert.assertNotEquals(myBeanId, myConversationBean.getMyBean().getId());
        Assert.assertNotEquals(myKieSessionId, myConversationBean.getkSession().getIdentifier());
        result = myConversationBean.doSomething("hello 3");
        Assert.assertEquals(1, result);

        myBeanId = myConversationBean.getMyBean().getId();
        myKieSessionId = myConversationBean.getkSession().getIdentifier();
        
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanId, myConversationBean.getMyBean().getId());
        Assert.assertEquals(myKieSessionId, myConversationBean.getkSession().getIdentifier());

        myBeanId = myConversationBean.getMyBean().getId();
        myKieSessionId = myConversationBean.getkSession().getIdentifier();

        result = myConversationBean.doSomething("hello 4");
        Assert.assertEquals(1, result);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.startContext(RequestScoped.class);

        Assert.assertEquals(myBeanId, myConversationBean.getMyBean().getId());
        Assert.assertEquals(myKieSessionId, myConversationBean.getkSession().getIdentifier());

        result = myConversationBean.doSomething("hello 5");
        Assert.assertEquals(1, result);

        myBeanId = myConversationBean.getMyBean().getId();
        myKieSessionId = myConversationBean.getkSession().getIdentifier();
        myConversationBean.end();
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(ConversationScoped.class);

        ctxCtrl.startContext(ConversationScoped.class);
        ctxCtrl.startContext(RequestScoped.class);
        
        
        myConversationBean.begin();
        Assert.assertNotEquals(myBeanId, myConversationBean.getMyBean().getId());
        Assert.assertNotEquals(myKieSessionId, myConversationBean.getkSession().getIdentifier());
        myConversationBean.end();
        
        
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(ConversationScoped.class);
        ctxCtrl.stopContext(SessionScoped.class);

    }

}
